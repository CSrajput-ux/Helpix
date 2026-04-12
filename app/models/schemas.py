"""
app/models/schemas.py
---------------------
Pydantic v2 schemas (request/response models) for all API endpoints.
"""

from datetime import datetime
from enum import Enum
from typing import List, Optional

from pydantic import BaseModel, EmailStr, Field


# ===========================================================================
# Enums
# ===========================================================================

class UserRole(str, Enum):
    PATIENT = "PATIENT"
    DOCTOR = "DOCTOR"


# ===========================================================================
# Auth Schemas
# ===========================================================================

class SignupRequest(BaseModel):
    full_name: str = Field(..., min_length=2, max_length=100)
    email: EmailStr
    password: str = Field(..., min_length=6)
    role: UserRole = UserRole.PATIENT
    # Optional doctor-specific fields
    specialization: Optional[str] = None   # e.g. "Cardiologist"
    license_number: Optional[str] = None
    clinic_address: Optional[str] = None   # Physical location of the clinic


class LoginRequest(BaseModel):
    email: EmailStr
    password: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    role: UserRole
    user_id: str


class UserProfileResponse(BaseModel):
    user_id: str
    full_name: str
    email: EmailStr
    role: UserRole
    # Profile information
    age: Optional[int] = None
    blood_group: Optional[str] = None
    gender: Optional[str] = None
    location: Optional[str] = None
    emergency_contact: Optional[str] = None
    # Doctor-specific fields
    specialization: Optional[str] = None
    license_number: Optional[str] = None
    clinic_address: Optional[str] = None
    created_at: datetime


class UserProfileUpdateRequest(BaseModel):
    """Payload for updating the user profile (all fields optional)."""
    full_name: Optional[str] = Field(None, min_length=2, max_length=100)
    age: Optional[int] = Field(None, ge=0, le=120)
    blood_group: Optional[str] = None
    gender: Optional[str] = None
    location: Optional[str] = None
    emergency_contact: Optional[str] = None
    specialization: Optional[str] = None
    license_number: Optional[str] = None
    clinic_address: Optional[str] = None


# ===========================================================================
# Vitals Schemas
# ===========================================================================

class VitalsSyncRequest(BaseModel):
    """Payload from the Android app / Pebble/Titan smartwatch."""
    heart_rate: int = Field(..., ge=30, le=250, description="BPM")
    steps: int = Field(..., ge=0, description="Step count since last sync")
    spo2: float = Field(..., ge=50.0, le=100.0, description="Blood oxygen %")
    # Optional encrypted fields
    blood_pressure: Optional[str] = Field(None, description="e.g. '120/80' (will be encrypted)")
    device_id: Optional[str] = None
    # Client can optionally send a timestamp; server will use UTC now if absent
    client_timestamp: Optional[datetime] = None


class VitalsResponse(BaseModel):
    id: str
    user_id: str
    heart_rate: int
    steps: int
    spo2: float
    blood_pressure: Optional[str] = None   # Decrypted before returning
    device_id: Optional[str] = None
    timestamp: datetime


# ===========================================================================
# Prescription Schemas
# ===========================================================================

class MedicineEntry(BaseModel):
    medicine_name: str
    dosage: str
    frequency: str
    duration: Optional[str] = None
    instructions: Optional[str] = None


class PrescriptionResponse(BaseModel):
    prescription_id: str
    extracted_medicines: List[MedicineEntry]
    confidence: float = Field(..., description="Overall OCR/AI confidence score 0–1")
    processed_at: datetime
    raw_text: Optional[str] = None


# ===========================================================================
# Health Vault Schemas
# ===========================================================================

class VaultFileResponse(BaseModel):
    file_id: str
    filename: str
    content_type: str
    size_bytes: int
    uploaded_at: datetime
    uploaded_by: str   # user_id


# ===========================================================================
# Doctor-Patient Link Schemas
# ===========================================================================

class FollowPatientRequest(BaseModel):
    patient_id: str = Field(..., description="The unique user_id of the patient to follow")


class FollowPatientResponse(BaseModel):
    message: str
    doctor_id: str
    patient_id: str
    linked_at: datetime


class PatientSummary(BaseModel):
    patient_id: str
    full_name: str
    email: EmailStr
    linked_at: datetime


class DoctorSummary(BaseModel):
    user_id: str
    full_name: str
    specialization: Optional[str] = None
    clinic_address: Optional[str] = None


class DoctorPatientsResponse(BaseModel):
    doctor_id: str
    patients: List[PatientSummary]


# ===========================================================================
# Medical Records Schemas
# ===========================================================================

class MedicalRecordCreate(BaseModel):
    record_type: str = Field(..., description="e.g. 'Blood Test', 'X-Ray Report'")
    medical_history: str = Field(..., description="Sensitive – will be encrypted at rest")
    notes: Optional[str] = None


class MedicalRecordResponse(BaseModel):
    record_id: str
    user_id: str
    record_type: str
    medical_history: str   # Decrypted
    notes: Optional[str] = None
    created_at: datetime
