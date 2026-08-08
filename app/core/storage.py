"""
app/core/storage.py
-------------------
Helper functions for uploading, downloading, and deleting files from Cloudinary and Supabase.
"""

import uuid
import logging
from typing import Optional

import cloudinary.uploader
import cloudinary.api
from app.core.supabase_client import get_supabase_client
from app.core.config import settings

logger = logging.getLogger(__name__)

async def upload_image_cloudinary(file_bytes: bytes, filename: str) -> Optional[str]:
    """Uploads an image to Cloudinary and returns the secure URL."""
    if not settings.CLOUDINARY_CLOUD_NAME:
        logger.warning("Cloudinary not configured. Cannot upload image.")
        return None

    try:
        # Use a unique public ID based on filename and UUID
        public_id = f"{uuid.uuid4().hex}_{filename}"
        response = cloudinary.uploader.upload(
            file_bytes,
            public_id=public_id,
            resource_type="image"
        )
        return response.get("secure_url")
    except Exception as e:
        logger.error(f"Cloudinary upload failed: {e}")
        return None

async def delete_image_cloudinary(file_url: str) -> bool:
    """Deletes an image from Cloudinary given its URL."""
    if not settings.CLOUDINARY_CLOUD_NAME:
        return False

    try:
        # Extract public_id from URL
        # URL format: https://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<public_id>.<ext>
        parts = file_url.split("/")
        if len(parts) > 0:
            filename_with_ext = parts[-1]
            public_id = filename_with_ext.rsplit(".", 1)[0]
            cloudinary.uploader.destroy(public_id, resource_type="image")
            return True
    except Exception as e:
        logger.error(f"Cloudinary delete failed: {e}")
    return False

async def upload_document_supabase(file_bytes: bytes, filename: str, content_type: str) -> Optional[str]:
    """Uploads a document to Supabase Storage and returns the public URL."""
    supabase = get_supabase_client()
    if not supabase or not settings.SUPABASE_BUCKET:
        logger.warning("Supabase not configured. Cannot upload document.")
        return None

    try:
        bucket_name = settings.SUPABASE_BUCKET
        file_path = f"{uuid.uuid4().hex}_{filename}"
        
        response = supabase.storage.from_(bucket_name).upload(
            path=file_path,
            file=file_bytes,
            file_options={"content-type": content_type}
        )
        
        # Get public URL
        url_response = supabase.storage.from_(bucket_name).get_public_url(file_path)
        return url_response
    except Exception as e:
        logger.error(f"Supabase upload failed: {e}")
        return None

async def delete_document_supabase(file_url: str) -> bool:
    """Deletes a document from Supabase Storage given its URL."""
    supabase = get_supabase_client()
    if not supabase or not settings.SUPABASE_BUCKET:
        return False

    try:
        bucket_name = settings.SUPABASE_BUCKET
        # Extract file_path from URL
        # URL format: https://<project>.supabase.co/storage/v1/object/public/<bucket>/<file_path>
        parts = file_url.split(f"/{bucket_name}/")
        if len(parts) == 2:
            file_path = parts[1]
            supabase.storage.from_(bucket_name).remove([file_path])
            return True
    except Exception as e:
        logger.error(f"Supabase delete failed: {e}")
    return False
