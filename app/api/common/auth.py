"""
app/api/auth.py
---------------
Authentication routes:
  POST /auth/signup  – Register as PATIENT or DOCTOR
  POST /auth/login   – Get JWT token
  GET  /auth/profile – Get current user profile (protected)
"""

import uuid
from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, status

from app.core.db import get_users_collection
from app.core.security import (
    create_access_token,
    get_current_user,
    hash_password,
    verify_password,
)
from app.models.schemas import (
    LoginRequest,
    SignupRequest,
    TokenResponse,
    UserProfileResponse,
    UserProfileUpdateRequest,
    UserRole,
)

router = APIRouter(prefix="/auth", tags=["Authentication"])


# ---------------------------------------------------------------------------
# POST /auth/signup
# ---------------------------------------------------------------------------
@router.post("/signup", response_model=UserProfileResponse, status_code=status.HTTP_201_CREATED)
async def signup(body: SignupRequest):
    """
    Register a new user (Patient or Doctor).

    - Email must be unique.
    - Passwords are bcrypt-hashed before storage.
    - A unique `user_id` (UUID4) is generated automatically.
    """
    users = get_users_collection()

    # Check for duplicate email
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
        "age": None,
        "blood_group": None,
        "gender": None,
        "location": None,
        "emergency_contact": None,
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
        created_at=now,
    )


# ---------------------------------------------------------------------------
# POST /auth/login
# ---------------------------------------------------------------------------
@router.post("/login", response_model=TokenResponse)
async def login(body: LoginRequest):
    """
    Authenticate with email + password.

    Returns a JWT Bearer token valid for `JWT_EXPIRE_MINUTES` (default 7 days).
    Use this token in the `Authorization: Bearer <token>` header for all
    protected endpoints.
    """
    users = get_users_collection()

    user = await users.find_one({"email": body.email})
    if not user or not verify_password(body.password, user["hashed_password"]):
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
# GET /auth/profile
# ---------------------------------------------------------------------------
@router.get("/profile", response_model=UserProfileResponse)
async def get_profile(current_user: dict = Depends(get_current_user)):
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
        created_at=user["created_at"],
    )


# ---------------------------------------------------------------------------
# PATCH /auth/profile
# ---------------------------------------------------------------------------
@router.patch("/profile", response_model=UserProfileResponse)
async def update_profile(
    body: UserProfileUpdateRequest,
    current_user: dict = Depends(get_current_user)
):
    """
    Update the authenticated user's profile details.
    Only provided fields will be updated ($set).
    """
    users = get_users_collection()
    user_id = current_user["sub"]

    # Prepare update data (exclude None values)
    update_data = {k: v for k, v in body.model_dump(exclude_unset=True).items() if v is not None}
    
    if not update_data:
        raise HTTPException(status_code=400, detail="No fields provided for update")

    update_data["updated_at"] = datetime.now(timezone.utc)

    # Perform update
    result = await users.update_one(
        {"user_id": user_id},
        {"$set": update_data}
    )

    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="User not found")

    # Return updated profile
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
        created_at=updated_user["created_at"],
    )
