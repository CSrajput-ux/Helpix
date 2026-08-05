"""
app/api/vault.py
----------------
Health Vault (medical file storage) routes:
  POST /vault/upload  – Upload a medical PDF or image (GridFS)
  GET  /vault/list    – List own uploaded files
  GET  /vault/{file_id} – Download a specific file
  DELETE /vault/{file_id} – Delete a file
"""

import io
from datetime import datetime, timezone

from bson import ObjectId
from fastapi import APIRouter, Depends, File, Form, HTTPException, UploadFile, status
from fastapi.responses import StreamingResponse
from typing import List, Optional

from app.core.db import get_fs_bucket, get_users_collection
from app.core.file_safety import sanitise_filename
from app.core.security import get_current_user
from app.models.schemas import VaultFileResponse

router = APIRouter(prefix="/vault", tags=["Health Vault"])

ALLOWED_TYPES = {
    "application/pdf",
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/webp",
}
MAX_FILE_SIZE_MB = 20
MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024


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
    Upload a medical PDF or image to the Health Vault (stored in MongoDB GridFS).

    - Max file size: 20 MB
    - Allowed types: PDF, JPG, PNG, WebP
    """
    from app.core.file_safety import validate_file_safety, sanitise_filename
    contents = await validate_file_safety(file, max_size_mb=20, allow_pdf=True, allow_image=True, allow_audio=False)
    safe_name = sanitise_filename(file.filename)

    fs = get_fs_bucket()
    now = datetime.now(timezone.utc)

    # Store file in GridFS with custom metadata
    file_id = await fs.upload_from_stream(
        safe_name,
        io.BytesIO(contents),
        metadata={
            "uploaded_by": current_user["sub"],
            "content_type": file.content_type,
            "record_type": record_type,
            "uploaded_at": now,
        },
    )

    return VaultFileResponse(
        file_id=str(file_id),
        filename=safe_name,
        content_type=file.content_type,
        size_bytes=len(contents),
        uploaded_at=now,
        uploaded_by=current_user["sub"],
    )


# ---------------------------------------------------------------------------
# GET /vault/list
# ---------------------------------------------------------------------------
@router.get("/list", response_model=List[VaultFileResponse])
async def list_files(current_user: dict = Depends(get_current_user)):
    """List all files uploaded by the authenticated user."""
    fs = get_fs_bucket()

    cursor = fs.find({"metadata.uploaded_by": current_user["sub"]})
    results = []

    async for grid_out in cursor:
        results.append(
            VaultFileResponse(
                file_id=str(grid_out._id),
                filename=grid_out.filename,
                content_type=grid_out.metadata.get("content_type", "application/octet-stream"),
                size_bytes=grid_out.length,
                uploaded_at=grid_out.metadata.get("uploaded_at", grid_out.upload_date),
                uploaded_by=grid_out.metadata.get("uploaded_by", current_user["sub"]),
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

    Only the file owner can download their files.
    """
    fs = get_fs_bucket()

    try:
        object_id = ObjectId(file_id)
    except Exception:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid file_id format.")

    # First, check ownership by reading metadata
    cursor = fs.find({"_id": object_id})
    grid_out = None
    async for doc in cursor:
        grid_out = doc
        break

    if not grid_out:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found.")

    if grid_out.metadata.get("uploaded_by") != current_user["sub"]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to access this file.",
        )

    # Stream the file content
    stream = await fs.open_download_stream(object_id)
    content_type = grid_out.metadata.get("content_type", "application/octet-stream")

    async def file_generator():
        while chunk := await stream.readchunk():
            yield chunk

    safe_name = sanitise_filename(grid_out.filename)
    return StreamingResponse(
        file_generator(),
        media_type=content_type,
        headers={"Content-Disposition": f'attachment; filename="{safe_name}"'},
    )


# ---------------------------------------------------------------------------
# DELETE /vault/{file_id}
# ---------------------------------------------------------------------------
@router.delete("/{file_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_file(file_id: str, current_user: dict = Depends(get_current_user)):
    """Delete a file. Only the owner can delete their files."""
    fs = get_fs_bucket()

    try:
        object_id = ObjectId(file_id)
    except Exception:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Invalid file_id format.")

    # Verify ownership
    cursor = fs.find({"_id": object_id})
    grid_out = None
    async for doc in cursor:
        grid_out = doc
        break

    if not grid_out:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="File not found.")

    if grid_out.metadata.get("uploaded_by") != current_user["sub"]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to delete this file.",
        )

    await fs.delete(object_id)
