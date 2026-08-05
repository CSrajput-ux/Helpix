"""
app/core/file_safety.py
-----------------------
Strict file upload validation: size, MIME type, and magic-byte deep inspection.

Design
------
- NEVER trusts the client's Content-Type or filename extension.
- Validates actual byte content against magic numbers BEFORE storing anything.
- Returns raw bytes so callers can pass them directly to GridFS / ML models.
- Sanitises filenames before they are stored (strips path separators and
  control characters that could enable path-traversal attacks).
- Uploaded files always go to GridFS (MongoDB) — never to the local filesystem,
  so they can never be served as executable code by a web server.
"""

import io
import logging
import re
import unicodedata

from PIL import Image
from fastapi import HTTPException, UploadFile, status

logger = logging.getLogger(__name__)

# ── Allowed MIME sets ─────────────────────────────────────────────────────────
ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg", "image/webp"}
ALLOWED_AUDIO_TYPES = {"audio/wav", "audio/mpeg", "audio/mp3", "audio/x-wav"}
ALLOWED_PDF_TYPES   = {"application/pdf"}

# Characters NOT allowed anywhere in a stored filename
_FILENAME_BLACKLIST_RE = re.compile(r'[\\/<>:"|?*\x00-\x1f]')


def sanitise_filename(raw: str | None) -> str:
    """
    Return a safe filename derived from the user-supplied name.

    - Strips path components (path-traversal mitigation).
    - Removes control characters and shell-special characters.
    - Normalises unicode to NFC.
    - Truncates to 200 characters.
    - Falls back to "upload" when nothing usable remains.
    """
    if not raw:
        return "upload"

    # Normalise unicode
    name = unicodedata.normalize("NFC", raw)

    # Strip path separators (path-traversal)
    name = name.replace("\\", "/").split("/")[-1]

    # Remove disallowed characters
    name = _FILENAME_BLACKLIST_RE.sub("", name)

    # Collapse repeated dots (hides double-extension tricks like "evil.php.jpg")
    # Keep at most one dot before the extension
    parts = name.rsplit(".", 1)
    stem = parts[0].replace(".", "_") if parts else name
    ext  = parts[1].lower() if len(parts) == 2 else ""

    name = f"{stem}.{ext}" if ext else stem

    # Length cap
    name = name[:200] or "upload"

    return name


# ── Core validator ────────────────────────────────────────────────────────────

async def validate_file_safety(
    file: UploadFile,
    *,
    max_size_mb: int = 20,
    allow_pdf:   bool = True,
    allow_image: bool = True,
    allow_audio: bool = False,
) -> bytes:
    """
    Validate an uploaded file and return its raw bytes.

    Steps
    -----
    1. Read all bytes (enforces max size server-side, not just via header).
    2. Reject empty files.
    3. Check the declared Content-Type against the allowed set.
    4. Verify magic bytes / content structure — rejects spoofed extensions.

    Raises HTTPException (400/413/415) on any violation.
    Returns raw bytes on success.
    """
    contents = await file.read()

    # 1. Empty-file guard
    if not contents:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Uploaded file is empty.",
        )

    # 2. Size guard
    max_bytes = max_size_mb * 1024 * 1024
    if len(contents) > max_bytes:
        raise HTTPException(
            status_code=status.HTTP_413_REQUEST_ENTITY_TOO_LARGE,
            detail=f"File exceeds the maximum allowed size of {max_size_mb} MB.",
        )

    # 3. MIME type guard (client-declared)
    allowed: set[str] = set()
    if allow_image: allowed.update(ALLOWED_IMAGE_TYPES)
    if allow_pdf:   allowed.update(ALLOWED_PDF_TYPES)
    if allow_audio: allowed.update(ALLOWED_AUDIO_TYPES)

    declared_type = (file.content_type or "").lower().split(";")[0].strip()
    if declared_type not in allowed:
        logger.warning(
            "file_safety: rejected Content-Type=%r filename=%r",
            file.content_type, file.filename,
        )
        raise HTTPException(
            status_code=status.HTTP_415_UNSUPPORTED_MEDIA_TYPE,
            detail="File type is not allowed.",   # don't echo the value back
        )

    # 4. Magic-byte / structural deep validation
    _validate_content_structure(declared_type, contents, file.filename)

    return contents


def _validate_content_structure(content_type: str, data: bytes, filename: str | None) -> None:
    """
    Validate the actual byte content against what the Content-Type claims.
    Raises HTTPException(400) on mismatch.
    """
    if content_type in ALLOWED_IMAGE_TYPES:
        try:
            img = Image.open(io.BytesIO(data))
            img.verify()          # Pillow raises on truncated / corrupt images
        except Exception:
            logger.warning("file_safety: image structural validation failed filename=%r", filename)
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Uploaded file is not a valid image.",
            )

    elif content_type in ALLOWED_PDF_TYPES:
        if not data.startswith(b"%PDF-"):
            logger.warning("file_safety: missing PDF magic bytes filename=%r", filename)
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Uploaded file is not a valid PDF.",
            )
        # Reject PDFs that embed JavaScript or launch actions
        if b"/JavaScript" in data or b"/JS " in data or b"/Launch" in data:
            logger.warning("file_safety: PDF contains JS/Launch action filename=%r", filename)
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="PDF contains active content and cannot be accepted.",
            )

    elif content_type in ALLOWED_AUDIO_TYPES:
        is_wav = data.startswith(b"RIFF") and b"WAVE" in data[8:16]
        is_mp3 = (
            data.startswith(b"ID3")      or
            data.startswith(b"\xff\xfb") or
            data.startswith(b"\xff\xf3") or
            data.startswith(b"\xff\xf2")
        )
        if not (is_wav or is_mp3):
            logger.warning("file_safety: audio magic-byte mismatch filename=%r", filename)
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Uploaded file is not a valid WAV or MP3 audio file.",
            )
