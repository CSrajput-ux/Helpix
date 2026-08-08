"""
app/api/patient/vault.py
----------------
Health Vault (medical file storage) routes:
  POST /vault/upload  – Upload a medical PDF or image
  GET  /vault/list    – List own uploaded files
  GET  /vault/{file_id} – Download a specific file
  DELETE /vault/{file_id} – Delete a file
"""

import httpx
from datetime import datetime, timezone
from bson import ObjectId
from fastapi import APIRouter, Depends, File, Form, HTTPException, UploadFile, status
from fastapi.responses import StreamingResponse
from typing import List, Optional

from app.core.db import get_vault_files_collection
from app.core.file_safety import sanitise_filename
from app.core.security import get_current_user
from app.core.storage import upload_image_cloudinary, delete_image_cloudinary, upload_document_supabase, delete_document_supabase
from app.models.schemas import VaultFileResponse

router = APIRouter(prefix="/vault", tags=["Health Vault"])

# ---------------------------------------------------------------------------
# POST /vault/upload
# ---------------------------------------------------------------------------
@router.post("/upload", response_model=VaultFileResponse, status_code=status.HTTP_201_CREATED)
async def upload_file(
    file: UploadFile = File(...),
    record_type: Optional[str] = Form(default="General", description="e.g. 'Blood Report', 'X-Ray'"),
    current_user: dict = Depends(get_current_user),
):
    """
    Upload a medical PDF or image to the Health Vault.
    Images -> Cloudinary, PDFs -> Supabase.
    - Max file size: 20 MB
    - Allowed types: PDF, JPG, PNG, WebP
    """
    from app.core.file_safety import validate_file_safety
    contents = await validate_file_safety(file, max_size_mb=20, allow_pdf=True, allow_image=True, allow_audio=False)
    safe_name = sanitise_filename(file.filename)
    now = datetime.now(timezone.utc)
    
    file_url = None
    provider = None
    
    # Check mime type to route
    if file.content_type in ["image/jpeg", "image/jpg", "image/png", "image/webp"]:
        provider = "cloudinary"
        file_url = await upload_image_cloudinary(contents, safe_name)
    elif file.content_type == "application/pdf":
        provider = "supabase"
        file_url = await upload_document_supabase(contents, safe_name, file.content_type)
    else:
        # Fallback to supabase for any other allowed types if any
        provider = "supabase"
        file_url = await upload_document_supabase(contents, safe_name, file.content_type)
        
    if not file_url:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to upload file to the storage provider."
        )

    vault_files = get_vault_files_collection()
    
    doc = {
        "filename": safe_name,
        "content_type": file.content_type,
        "size_bytes": len(contents),
        "record_type": record_type,
        "uploaded_at": now,
        "uploaded_by": current_user["sub"],
        "file_url": file_url,
        "provider": provider
    }
    
    result = await vault_files.insert_one(doc)

    return VaultFileResponse(
        file_id=str(result.inserted_id),
        filename=safe_name,
        content_type=file.content_type,
        size_bytes=len(contents),
        uploaded_at=now,
        uploaded_by=current_user["sub"],
        file_url=file_url,
        provider=provider
    )


# ---------------------------------------------------------------------------
# GET /vault/list
# ---------------------------------------------------------------------------
@router.get("/list", response_model=List[VaultFileResponse])
async def list_files(current_user: dict = Depends(get_current_user)):
    """List all files uploaded by the authenticated user."""
    vault_files = get_vault_files_collection()

    cursor = vault_files.find({"uploaded_by": current_user["sub"]}).sort("uploaded_at", -1)
    results = []

    async for doc in cursor:
        results.append(
            VaultFileResponse(
                file_id=str(doc["_id"]),
                filename=doc.get("filename", "unknown"),
                content_type=doc.get("content_type", "application/octet-stream"),
                size_bytes=doc.get("size_bytes", 0),
                uploaded_at=doc.get("uploaded_at"),
                uploaded_by=doc.get("uploaded_by"),
                file_url=doc.get("file_url"),
                provider=doc.get("provider")
            )
        )

    return results


# ---------------------------------------------------------------------------
# GET /vault/{file_id}
# ---------------------------------------------------------------------------
@router.get("/{file_id}")
async def download_file(file_id: str, current_user: dict = Depends(get_current_user)):
    """
    Download a specific file from the Health Vault.
    Proxies the download from Cloudinary or Supabase.
    """
    vault_files = get_vault_files_collection()

    try:
        object_id = ObjectId(file_id)
    except Exception:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid file_id format.")

    doc = await vault_files.find_one({"_id": object_id})

    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found.")

    if doc.get("uploaded_by") != current_user["sub"]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to access this file.",
        )

    file_url = doc.get("file_url")
    if not file_url:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File URL not found.")

    async def stream_from_url(url: str):
        async with httpx.AsyncClient() as client:
            async with client.stream("GET", url) as response:
                if response.status_code != 200:
                    raise HTTPException(status_code=status.HTTP_502_BAD_GATEWAY, detail="Failed to fetch file from storage provider.")
                async for chunk in response.aiter_bytes():
                    yield chunk

    safe_name = sanitise_filename(doc.get("filename", "download"))
    content_type = doc.get("content_type", "application/octet-stream")
    
    return StreamingResponse(
        stream_from_url(file_url),
        media_type=content_type,
        headers={"Content-Disposition": f'attachment; filename="{safe_name}"'},
    )


# ---------------------------------------------------------------------------
# DELETE /vault/{file_id}
# ---------------------------------------------------------------------------
@router.delete("/{file_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_file(file_id: str, current_user: dict = Depends(get_current_user)):
    """Delete a file. Only the owner can delete their files."""
    vault_files = get_vault_files_collection()

    try:
        object_id = ObjectId(file_id)
    except Exception:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid file_id format.")

    doc = await vault_files.find_one({"_id": object_id})

    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found.")

    if doc.get("uploaded_by") != current_user["sub"]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to delete this file.",
        )

    # Delete from external provider
    provider = doc.get("provider")
    file_url = doc.get("file_url")
    
    if file_url:
        if provider == "cloudinary":
            await delete_image_cloudinary(file_url)
        elif provider == "supabase":
            await delete_document_supabase(file_url)

    # Delete from DB
    await vault_files.delete_one({"_id": object_id})
