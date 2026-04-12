"""
app/core/security.py
--------------------
Handles all security concerns:
  1. Password hashing   – bcrypt via passlib
  2. JWT tokens         – PyJWT
  3. AES encryption     – Fernet (symmetric, authenticated encryption)
  4. FastAPI dependency – get_current_user (JWT guard for protected routes)
"""

from datetime import datetime, timedelta, timezone
from typing import Optional

from cryptography.fernet import Fernet, InvalidToken
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
import jwt as pyjwt
from passlib.context import CryptContext

from app.core.config import settings

# ---------------------------------------------------------------------------
# Password hashing (bcrypt)
# ---------------------------------------------------------------------------
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


def hash_password(plain: str) -> str:
    """Return bcrypt-hashed password."""
    return pwd_context.hash(plain)


def verify_password(plain: str, hashed: str) -> bool:
    """Verify plain password against its bcrypt hash."""
    return pwd_context.verify(plain, hashed)


# ---------------------------------------------------------------------------
# JWT helpers
# ---------------------------------------------------------------------------
def create_access_token(data: dict, expires_delta: Optional[timedelta] = None) -> str:
    """Create a signed JWT access token."""
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + (
        expires_delta or timedelta(minutes=settings.JWT_EXPIRE_MINUTES)
    )
    to_encode.update({"exp": expire})
    # PyJWT returns str directly
    return pyjwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)


def decode_access_token(token: str) -> dict:
    """Decode and validate a JWT. Raises HTTPException on failure."""
    try:
        payload = pyjwt.decode(
            token,
            settings.JWT_SECRET_KEY,
            algorithms=[settings.JWT_ALGORITHM],
        )
        return payload
    except pyjwt.PyJWTError as exc:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid or expired token",
            headers={"WWW-Authenticate": "Bearer"},
        ) from exc


# ---------------------------------------------------------------------------
# AES Encryption (Fernet = AES-128-CBC + HMAC-SHA256)
# ---------------------------------------------------------------------------
def _get_fernet() -> Fernet:
    """Return a Fernet instance using the key from settings."""
    key = settings.ENCRYPTION_KEY
    if key == "CHANGE_ME":
        raise RuntimeError(
            "ENCRYPTION_KEY is not set. "
            "Run: python -c \"from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())\""
        )
    return Fernet(key.encode())


def encrypt_field(value: str) -> str:
    """Encrypt a string field and return the ciphertext as a string."""
    if not value:
        return value
    f = _get_fernet()
    return f.encrypt(value.encode()).decode()


def decrypt_field(ciphertext: str) -> str:
    """Decrypt an AES-encrypted field. Returns the original plaintext."""
    if not ciphertext:
        return ciphertext
    try:
        f = _get_fernet()
        return f.decrypt(ciphertext.encode()).decode()
    except InvalidToken:
        # Return a placeholder so the API doesn't crash on bad data
        return "[DECRYPTION ERROR]"


# ---------------------------------------------------------------------------
# FastAPI OAuth2 dependency
# ---------------------------------------------------------------------------
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")


async def get_current_user(token: str = Depends(oauth2_scheme)) -> dict:
    """
    FastAPI dependency that extracts and validates the JWT.
    Inject this into any route that requires authentication.

    Returns the decoded JWT payload dict containing at least:
      - sub   : user_id (str)
      - email : str
      - role  : "PATIENT" | "DOCTOR"
    """
    payload = decode_access_token(token)
    user_id: str = payload.get("sub")
    if user_id is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token payload missing 'sub' field",
        )
    return payload


async def require_doctor(current_user: dict = Depends(get_current_user)) -> dict:
    """FastAPI dependency that additionally enforces the DOCTOR role."""
    if current_user.get("role") != "DOCTOR":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only doctors can access this resource",
        )
    return current_user
