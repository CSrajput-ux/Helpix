"""
app/api/auth.py
---------------
Authentication routes:
  POST /auth/signup         – Register as PATIENT or DOCTOR
  POST /auth/login          – Get JWT token
  GET  /auth/profile        – Get current user profile (protected)
  PATCH /auth/profile       – Update user profile (protected)
  POST /auth/google         – Google OAuth login/register
  POST /auth/refresh        – Refresh JWT token (protected)
  POST /auth/forgot-password – Initiate password reset (public)
  POST /auth/profile/image  – Upload profile picture (protected)
"""

import logging
import os
import uuid
from datetime import datetime, timedelta, timezone

from fastapi import APIRouter, Depends, File, HTTPException, Request, UploadFile, status
from google.oauth2 import id_token as google_id_token
from google.auth.transport import requests as google_requests

from app.core.config import settings
from app.core.db import get_users_collection
from app.core.security import (
    create_access_token,
    get_current_user,
    hash_password,
    verify_password,
)
from app.core.rate_limit import auth_rate_limit, user_rate_limit
from app.models.schemas import (
    LoginRequest,
    SignupRequest,
    TokenResponse,
    UserProfileResponse,
    UserProfileUpdateRequest,
    UserRole,
    GoogleLoginRequest,
)

router = APIRouter(prefix="/auth", tags=["Authentication"])
logger = logging.getLogger(__name__)

_PLACEHOLDER_GOOGLE_ID = "your-google-client-id.apps.googleusercontent.com"


# ---------------------------------------------------------------------------
# POST /auth/signup
# ---------------------------------------------------------------------------
@router.post("/signup", response_model=UserProfileResponse, status_code=status.HTTP_201_CREATED)
async def signup(request: Request, body: SignupRequest):
    """
    Register a new user (Patient or Doctor).

    - Email must be unique.
    - Passwords are bcrypt-hashed before storage.
    - A unique `user_id` (UUID4) is generated automatically.
    """
    await auth_rate_limit(request, body.email)
    users = get_users_collection()

    existing = await users.find_one({"email": body.email})
    if existing:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="An account with this email already exists.",
        )

    user_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    user_doc = {
        "user_id": user_id,
        "full_name": body.full_name,
        "email": body.email,
        "hashed_password": hash_password(body.password),
        "role": body.role.value,
        "specialization": body.specialization,
        "license_number": body.license_number,
        "clinic_address": body.clinic_address,
        "consultation_fee": getattr(body, "consultation_fee", 500.0),
        "experience_years": getattr(body, "experience_years", None),
        "discovery_radius": 20.0,
        "latitude": None,
        "longitude": None,
        "age": None,
        "blood_group": None,
        "gender": None,
        "location": None,
        "emergency_contact": None,
        "allergies": None,
        "profile_image_url": None,
        "created_at": now,
        "updated_at": now,
    }

    await users.insert_one(user_doc)

    return UserProfileResponse(
        user_id=user_id,
        full_name=body.full_name,
        email=body.email,
        role=body.role,
        specialization=body.specialization,
        license_number=body.license_number,
        clinic_address=body.clinic_address,
        consultation_fee=getattr(body, "consultation_fee", 500.0),
        experience_years=getattr(body, "experience_years", None),
        created_at=now,
    )


# ---------------------------------------------------------------------------
# POST /auth/login
# ---------------------------------------------------------------------------
@router.post("/login", response_model=TokenResponse)
async def login(request: Request):
    """
    Authenticate with email + password.

    Supports BOTH JSON (from Android app) and Form Data (from Swagger UI).
    Returns a JWT Bearer token valid for `JWT_EXPIRE_MINUTES`.
    """
    content_type = request.headers.get("content-type", "")
    
    if "application/json" in content_type:
        try:
            body = await request.json()
            email = body.get("email")
            password = body.get("password")
        except Exception:
            raise HTTPException(status_code=400, detail="Invalid JSON body")
    else:
        # Swagger UI sends x-www-form-urlencoded with 'username' and 'password'
        form = await request.form()
        email = form.get("username")
        password = form.get("password")

    if not email or not password:
        raise HTTPException(status_code=400, detail="Missing email or password")

    await auth_rate_limit(request, email)
    users = get_users_collection()

    user = await users.find_one({"email": email})
    if not user or not verify_password(password, user["hashed_password"]):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
        )

    token_data = {
        "sub": user["user_id"],
        "email": user["email"],
        "role": user["role"],
    }
    token = create_access_token(token_data)

    return TokenResponse(
        access_token=token,
        token_type="bearer",
        role=UserRole(user["role"]),
        user_id=user["user_id"],
    )


# ---------------------------------------------------------------------------
# POST /auth/google
# ---------------------------------------------------------------------------
@router.post("/google", response_model=TokenResponse)
async def google_login(request: Request, body: GoogleLoginRequest):
    """
    Authenticate or register a user using their Google ID Token.

    If the user does not exist in the database, a new account is automatically
    created with the specified role (defaults to PATIENT).

    IMPORTANT: Requires GOOGLE_CLIENT_ID to be configured in .env.
    """
    # FIX #3: Enforce that GOOGLE_CLIENT_ID is properly configured
    if not settings.GOOGLE_CLIENT_ID or settings.GOOGLE_CLIENT_ID == _PLACEHOLDER_GOOGLE_ID:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="Google OAuth is not configured on this server. Contact the administrator.",
        )

    try:
        idinfo = google_id_token.verify_oauth2_token(
            body.id_token,
            google_requests.Request(),
            settings.GOOGLE_CLIENT_ID,  # Always enforce the configured audience
        )
        if idinfo["iss"] not in ["accounts.google.com", "https://accounts.google.com"]:
            raise ValueError("Wrong issuer.")
    except Exception as e:
        logger.warning("Google ID token verification failed: %s", e)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid Google ID token.",
        )

    email = idinfo.get("email")
    full_name = idinfo.get("name", "Google User")

    if not email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Google ID token does not contain email.",
        )

    await auth_rate_limit(request, email)

    users = get_users_collection()
    user = await users.find_one({"email": email})
    now = datetime.now(timezone.utc)

    if not user:
        user_id = str(uuid.uuid4())
        random_password = uuid.uuid4().hex + uuid.uuid4().hex
        user_doc = {
            "user_id": user_id,
            "full_name": full_name,
            "email": email,
            "hashed_password": hash_password(random_password),
            "role": body.role.value,
            "specialization": None,
            "license_number": None,
            "clinic_address": None,
            "consultation_fee": getattr(body, "consultation_fee", 500.0) if hasattr(body, "consultation_fee") else 500.0,
            "experience_years": getattr(body, "experience_years", None) if hasattr(body, "experience_years") else None,
            "discovery_radius": 20.0,
            "latitude": None,
            "longitude": None,
            "age": None,
            "blood_group": None,
            "gender": None,
            "location": None,
            "emergency_contact": None,
            "allergies": None,
            "profile_image_url": None,
            "created_at": now,
            "updated_at": now,
        }
        await users.insert_one(user_doc)
        user = user_doc

    token_data = {
        "sub": user["user_id"],
        "email": user["email"],
        "role": user["role"],
    }
    token = create_access_token(token_data)

    return TokenResponse(
        access_token=token,
        token_type="bearer",
        role=UserRole(user["role"]),
        user_id=user["user_id"],
    )


# ---------------------------------------------------------------------------
# GET /auth/profile
# ---------------------------------------------------------------------------
@router.get("/profile", response_model=UserProfileResponse)
async def get_profile(current_user: dict = Depends(user_rate_limit)):
    """Return the authenticated user's profile."""
    users = get_users_collection()
    user = await users.find_one({"user_id": current_user["sub"]})
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    return UserProfileResponse(
        user_id=user["user_id"],
        full_name=user["full_name"],
        email=user["email"],
        role=UserRole(user["role"]),
        age=user.get("age"),
        blood_group=user.get("blood_group"),
        gender=user.get("gender"),
        location=user.get("location"),
        emergency_contact=user.get("emergency_contact"),
        specialization=user.get("specialization"),
        license_number=user.get("license_number"),
        clinic_address=user.get("clinic_address"),
        allergies=user.get("allergies"),
        consultation_fee=user.get("consultation_fee", 500.0),
        experience_years=user.get("experience_years"),
        discovery_radius=user.get("discovery_radius", 20.0),
        latitude=user.get("latitude"),
        longitude=user.get("longitude"),
        profile_image_url=user.get("profile_image_url"),
        created_at=user["created_at"],
    )


# ---------------------------------------------------------------------------
# PATCH /auth/profile
# ---------------------------------------------------------------------------
@router.patch("/profile", response_model=UserProfileResponse)
async def update_profile(
    body: UserProfileUpdateRequest,
    current_user: dict = Depends(user_rate_limit)
):
    """
    Update the authenticated user's profile details.
    Only provided fields will be updated ($set).
    """
    users = get_users_collection()
    user_id = current_user["sub"]

    update_data = {k: v for k, v in body.model_dump(exclude_unset=True).items() if v is not None}

    if not update_data:
        raise HTTPException(status_code=400, detail="No fields provided for update")

    update_data["updated_at"] = datetime.now(timezone.utc)

    result = await users.update_one(
        {"user_id": user_id},
        {"$set": update_data}
    )

    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="User not found")

    updated_user = await users.find_one({"user_id": user_id})
    return UserProfileResponse(
        user_id=updated_user["user_id"],
        full_name=updated_user["full_name"],
        email=updated_user["email"],
        role=UserRole(updated_user["role"]),
        age=updated_user.get("age"),
        blood_group=updated_user.get("blood_group"),
        gender=updated_user.get("gender"),
        location=updated_user.get("location"),
        emergency_contact=updated_user.get("emergency_contact"),
        specialization=updated_user.get("specialization"),
        license_number=updated_user.get("license_number"),
        clinic_address=updated_user.get("clinic_address"),
        allergies=updated_user.get("allergies"),
        consultation_fee=updated_user.get("consultation_fee", 500.0),
        experience_years=updated_user.get("experience_years"),
        discovery_radius=updated_user.get("discovery_radius", 20.0),
        latitude=updated_user.get("latitude"),
        longitude=updated_user.get("longitude"),
        profile_image_url=updated_user.get("profile_image_url"),
        created_at=updated_user["created_at"],
    )


# ---------------------------------------------------------------------------
# POST /auth/profile/image  — FIX #4: Actually save the file to disk
# ---------------------------------------------------------------------------
@router.post("/profile/image", response_model=UserProfileResponse)
async def upload_profile_image(
    image: UploadFile = File(...),
    current_user: dict = Depends(user_rate_limit),
):
    """Upload or update user profile picture. File is saved to UPLOAD_DIR."""
    from app.core.file_safety import validate_file_safety
    contents = await validate_file_safety(image, max_size_mb=10, allow_pdf=False, allow_image=True, allow_audio=False)

    users = get_users_collection()
    user_id = current_user["sub"]

    # Upload to Cloudinary
    from app.core.storage import upload_image_cloudinary
    image_url = await upload_image_cloudinary(contents, image.filename or "profile.jpg")
    
    if not image_url:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to upload image to Cloudinary"
        )

    await users.update_one(
        {"user_id": user_id},
        {"$set": {"profile_image_url": image_url, "updated_at": datetime.now(timezone.utc)}}
    )

    updated_user = await users.find_one({"user_id": user_id})
    if not updated_user:
        raise HTTPException(status_code=404, detail="User not found")

    return UserProfileResponse(
        user_id=updated_user["user_id"],
        full_name=updated_user["full_name"],
        email=updated_user["email"],
        role=UserRole(updated_user["role"]),
        age=updated_user.get("age"),
        blood_group=updated_user.get("blood_group"),
        gender=updated_user.get("gender"),
        location=updated_user.get("location"),
        emergency_contact=updated_user.get("emergency_contact"),
        specialization=updated_user.get("specialization"),
        license_number=updated_user.get("license_number"),
        clinic_address=updated_user.get("clinic_address"),
        allergies=updated_user.get("allergies"),
        consultation_fee=updated_user.get("consultation_fee", 500.0),
        experience_years=updated_user.get("experience_years"),
        discovery_radius=updated_user.get("discovery_radius", 20.0),
        latitude=updated_user.get("latitude"),
        longitude=updated_user.get("longitude"),
        profile_image_url=updated_user.get("profile_image_url"),
        created_at=updated_user["created_at"],
    )


# ---------------------------------------------------------------------------
# POST /auth/refresh  — FIX #5: JWT token refresh
# ---------------------------------------------------------------------------
@router.post("/refresh", response_model=TokenResponse)
async def refresh_token(current_user: dict = Depends(get_current_user)):
    """
    Issue a new access token using the existing (still-valid) token.
    Call this before the current token expires to stay logged in.
    """
    new_token = create_access_token({
        "sub": current_user["sub"],
        "email": current_user["email"],
        "role": current_user["role"],
    })

    users = get_users_collection()
    user = await users.find_one({"user_id": current_user["sub"]})
    if not user:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")

    return TokenResponse(
        access_token=new_token,
        token_type="bearer",
        role=UserRole(current_user["role"]),
        user_id=current_user["sub"],
    )


# ---------------------------------------------------------------------------
# POST /auth/forgot-password  — FIX #11: Password reset initiation
# ---------------------------------------------------------------------------
from pydantic import BaseModel, EmailStr

class ForgotPasswordRequest(BaseModel):
    email: EmailStr


class ResetPasswordRequest(BaseModel):
    token: str
    new_password: str


@router.post("/forgot-password")
async def forgot_password(body: ForgotPasswordRequest, request: Request):
    """
    Initiate password reset flow.

    Always returns success to prevent email enumeration attacks.
    In production: integrate an email service (SendGrid / AWS SES) to send the reset link.
    The reset token is a short-lived JWT (30 min) with type='password_reset'.
    """
    await auth_rate_limit(request, body.email)
    users = get_users_collection()
    user = await users.find_one({"email": body.email})

    if user:
        # Create a short-lived token for password reset
        reset_token = create_access_token(
            {
                "sub": user["user_id"],
                "email": user["email"],
                "role": user["role"],
                "type": "password_reset",
            },
            expires_delta=timedelta(minutes=30),
        )
        # TODO: Send reset_token via email using SendGrid / AWS SES
        # Example: send_reset_email(user["email"], reset_token)
        logger.info("Password reset token generated for user_id=%s", user["user_id"])

    # Always return success (prevents email enumeration)
    return {"message": "If that email is registered, a password reset link has been sent."}


@router.post("/reset-password")
async def reset_password(body: ResetPasswordRequest, request: Request):
    """
    Complete the password reset using the token received via email.
    The token must be a valid password_reset JWT issued by /auth/forgot-password.
    """
    from app.core.security import decode_access_token
    from app.models.schemas import _PASSWORD_PATTERN
    import re

    if not re.match(_PASSWORD_PATTERN, body.new_password) or len(body.new_password) < 8:
        raise HTTPException(status_code=400, detail="Password must be 8–128 printable ASCII characters.")

    payload = decode_access_token(body.token)

    if payload.get("type") != "password_reset":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid token type. Use the token from the reset email.",
        )

    users = get_users_collection()
    result = await users.update_one(
        {"user_id": payload["sub"]},
        {"$set": {"hashed_password": hash_password(body.new_password), "updated_at": datetime.now(timezone.utc)}},
    )

    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="User not found.")

    logger.info("Password reset completed for user_id=%s", payload["sub"])
    return {"message": "Password has been reset successfully. Please log in with your new password."}
