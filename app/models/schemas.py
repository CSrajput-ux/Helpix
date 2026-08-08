"""
app/models/schemas.py
---------------------
Pydantic v2 request/response schemas for all API endpoints.

Validation philosophy
---------------------
Every input field has explicit:
  - Type       : enforced by Pydantic
  - Length     : min_length / max_length on every string
  - Format     : pattern regex or Enum where the domain is finite
  - Range      : ge / le on every numeric field
  - No sanitise: invalid input is REJECTED (422) — never silently cleaned

Shared reusable constants are defined at the top.
"""

from datetime import datetime
from enum import Enum
from typing import Annotated, List, Optional

from pydantic import BaseModel, EmailStr, Field, field_validator, model_validator


# ─────────────────────────────────────────────────────────────────────────────
# Shared type aliases (define once, reuse everywhere)
# ─────────────────────────────────────────────────────────────────────────────

# UUID v4 string  e.g. "550e8400-e29b-41d4-a716-446655440000"
_UUID4_PATTERN = r"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"

# YYYY-MM-DD date string
_DATE_PATTERN = r"^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$"

# HH:MM time string  e.g. "08:30"
_TIME_PATTERN = r"^([01]\d|2[0-3]):[0-5]\d$"

# Human name – letters, spaces, hyphens, apostrophes only
_NAME_PATTERN = r"^[A-Za-z\s\-']+$"

# Password – printable ASCII only (no control characters)
_PASSWORD_PATTERN = r"^[\x20-\x7E]+$"

UUID4Field = Annotated[str, Field(pattern=_UUID4_PATTERN)]


# ─────────────────────────────────────────────────────────────────────────────
# Enums
# ─────────────────────────────────────────────────────────────────────────────

class UserRole(str, Enum):
    PATIENT = "PATIENT"
    DOCTOR  = "DOCTOR"


class BloodGroup(str, Enum):
    A_POS  = "A+"
    A_NEG  = "A-"
    B_POS  = "B+"
    B_NEG  = "B-"
    AB_POS = "AB+"
    AB_NEG = "AB-"
    O_POS  = "O+"
    O_NEG  = "O-"


class Gender(str, Enum):
    MALE            = "Male"
    FEMALE          = "Female"
    OTHER           = "Other"
    PREFER_NOT      = "Prefer not to say"


# ─────────────────────────────────────────────────────────────────────────────
# Auth schemas
# ─────────────────────────────────────────────────────────────────────────────

class SignupRequest(BaseModel):
    full_name: str = Field(
        ...,
        min_length=2, max_length=100,
        pattern=_NAME_PATTERN,
        description="Full name – letters, spaces, hyphens and apostrophes only",
    )
    email: EmailStr
    password: str = Field(
        ...,
        min_length=8, max_length=128,
        pattern=_PASSWORD_PATTERN,
        description="8–128 printable ASCII characters",
    )
    role: UserRole = UserRole.PATIENT

    # Doctor-specific (required when role=DOCTOR, ignored otherwise)
    specialization: Optional[str] = Field(
        None, min_length=2, max_length=100,
        pattern=r"^[A-Za-z\s\-]+$",
    )
    license_number: Optional[str] = Field(
        None, min_length=5, max_length=50,
        pattern=r"^[A-Za-z0-9\-]+$",
    )
    clinic_address: Optional[str] = Field(
        None, min_length=5, max_length=250,
        pattern=r"^[\w\s,.\-/#]+$",
    )
    consultation_fee: float = Field(500.0, ge=0.0)
    experience_years: Optional[int] = Field(None, ge=0, le=100)

    @model_validator(mode="after")
    def doctor_fields_required(self) -> "SignupRequest":
        if self.role == UserRole.DOCTOR:
            if not self.specialization:
                raise ValueError("specialization is required for DOCTOR role")
            if not self.license_number:
                raise ValueError("license_number is required for DOCTOR role")
        return self


class LoginRequest(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=128, pattern=_PASSWORD_PATTERN)


class GoogleLoginRequest(BaseModel):
    id_token: str = Field(..., min_length=20, max_length=4096)
    role: UserRole = UserRole.PATIENT


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    role: UserRole
    user_id: UUID4Field


class UserProfileResponse(BaseModel):
    user_id: UUID4Field
    full_name: str = Field(..., min_length=2, max_length=100)
    email: EmailStr
    role: UserRole
    age: Optional[int] = Field(None, ge=0, le=120)
    blood_group: Optional[BloodGroup] = None
    gender: Optional[Gender] = None
    location: Optional[str] = Field(None, min_length=2, max_length=150)
    emergency_contact: Optional[str] = Field(None, pattern=r"^\+?[1-9]\d{6,14}$")
    specialization: Optional[str] = Field(None, min_length=2, max_length=100)
    license_number: Optional[str] = Field(None, min_length=5, max_length=50)
    clinic_address: Optional[str] = Field(None, min_length=5, max_length=250)
    allergies: Optional[str] = Field(None, max_length=500)
    consultation_fee: float = 500.0
    experience_years: Optional[int] = None
    discovery_radius: float = 20.0
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    profile_image_url: Optional[str] = None
    created_at: datetime


class UserProfileUpdateRequest(BaseModel):
    full_name: Optional[str] = Field(
        None, min_length=2, max_length=100, pattern=_NAME_PATTERN,
    )
    age: Optional[int] = Field(None, ge=0, le=120)
    blood_group: Optional[BloodGroup] = None
    gender: Optional[Gender] = None
    location: Optional[str] = Field(None, min_length=2, max_length=150)
    emergency_contact: Optional[str] = Field(None, pattern=r"^\+?[1-9]\d{6,14}$")
    specialization: Optional[str] = Field(
        None, min_length=2, max_length=100, pattern=r"^[A-Za-z\s\-]+$",
    )
    license_number: Optional[str] = Field(
        None, min_length=5, max_length=50, pattern=r"^[A-Za-z0-9\-]+$",
    )
    clinic_address: Optional[str] = Field(
        None, min_length=5, max_length=250, pattern=r"^[\w\s,.\-/#]+$",
    )
    allergies: Optional[str] = Field(None, max_length=500)
    consultation_fee: Optional[float] = Field(None, ge=0.0)
    experience_years: Optional[int] = Field(None, ge=0, le=100)
    discovery_radius: Optional[float] = Field(None, ge=0.0)
    latitude: Optional[float] = Field(None, ge=-90.0, le=90.0)
    longitude: Optional[float] = Field(None, ge=-180.0, le=180.0)
    profile_image_url: Optional[str] = None


# ─────────────────────────────────────────────────────────────────────────────
# Vitals schemas
# ─────────────────────────────────────────────────────────────────────────────

class VitalsSyncRequest(BaseModel):
    heart_rate: int   = Field(..., ge=30,   le=250,   description="BPM")
    steps:      int   = Field(..., ge=0,    le=100_000, description="Steps since last sync")
    spo2:       float = Field(..., ge=50.0, le=100.0, description="Blood oxygen %")
    blood_pressure: Optional[str] = Field(
        None,
        pattern=r"^\d{2,3}/\d{2,3}$",
        description="Systolic/Diastolic e.g. '120/80' – encrypted at rest",
    )
    device_id: Optional[str] = Field(
        None, min_length=3, max_length=100,
        pattern=r"^[A-Za-z0-9_\-:]+$",
    )
    client_timestamp: Optional[datetime] = None

    @field_validator("client_timestamp")
    @classmethod
    def timestamp_not_in_future(cls, v: Optional[datetime]) -> Optional[datetime]:
        if v is None:
            return v
        from datetime import timezone
        now = datetime.now(timezone.utc)
        ts  = v if v.tzinfo else v.replace(tzinfo=timezone.utc)
        if ts > now:
            raise ValueError("client_timestamp cannot be in the future")
        return v


class VitalsResponse(BaseModel):
    id:             str
    user_id:        UUID4Field
    heart_rate:     int
    steps:          int
    spo2:           float
    blood_pressure: Optional[str] = None
    device_id:      Optional[str] = None
    timestamp:      datetime


# ─────────────────────────────────────────────────────────────────────────────
# Prescription schemas
# ─────────────────────────────────────────────────────────────────────────────

class MedicineEntry(BaseModel):
    medicine_name: str = Field(..., min_length=1, max_length=150, pattern=r"^[\w\s\-\.]+$")
    dosage:        str = Field(..., min_length=1, max_length=50,  pattern=r"^[\w\s\.\-/]+$")
    frequency:     str = Field(..., min_length=1, max_length=100)
    duration:      Optional[str] = Field(None, max_length=100)
    instructions:  Optional[str] = Field(None, max_length=500)


class PrescriptionResponse(BaseModel):
    prescription_id:     str
    extracted_medicines: List[MedicineEntry]
    confidence:          float = Field(..., ge=0.0, le=1.0)
    processed_at:        datetime
    raw_text:            Optional[str] = None


# ─────────────────────────────────────────────────────────────────────────────
# Health Vault schemas
# ─────────────────────────────────────────────────────────────────────────────

class VaultFileResponse(BaseModel):
    file_id:      str
    filename:     str = Field(..., min_length=1, max_length=255)
    content_type: str
    size_bytes:   int = Field(..., ge=1)
    uploaded_at:  datetime
    uploaded_by:  UUID4Field
    file_url:     Optional[str] = None
    provider:     Optional[str] = None


# ─────────────────────────────────────────────────────────────────────────────
# Doctor-Patient link schemas
# ─────────────────────────────────────────────────────────────────────────────

class FollowPatientRequest(BaseModel):
    patient_id: UUID4Field = Field(..., description="UUID of the patient to follow")


class FollowPatientResponse(BaseModel):
    message:    str
    doctor_id:  UUID4Field
    patient_id: UUID4Field
    linked_at:  datetime


class PatientSummary(BaseModel):
    patient_id: UUID4Field
    full_name:  str = Field(..., min_length=2, max_length=100)
    email:      EmailStr
    linked_at:  datetime


class DoctorSummary(BaseModel):
    user_id:        UUID4Field
    full_name:      str = Field(..., min_length=2, max_length=100)
    specialization: Optional[str] = Field(None, min_length=2, max_length=100)
    clinic_address: Optional[str] = Field(None, min_length=5, max_length=250)
    consultation_fee: float = 500.0
    experience_years: Optional[int] = None
    distance: Optional[float] = None


class DoctorPatientsResponse(BaseModel):
    doctor_id: UUID4Field
    patients:  List[PatientSummary]


# ─────────────────────────────────────────────────────────────────────────────
# Medical Records schemas
# ─────────────────────────────────────────────────────────────────────────────

class MedicalRecordCreate(BaseModel):
    record_type:     str = Field(
        ..., min_length=2, max_length=100,
        pattern=r"^[\w\s\-\.]+$",
        description="e.g. 'Blood Test', 'X-Ray Report'",
    )
    medical_history: str = Field(
        ..., min_length=5, max_length=5000,
        description="Sensitive – encrypted at rest",
    )
    notes: Optional[str] = Field(None, max_length=1000)


class MedicalRecordResponse(BaseModel):
    record_id:       str
    user_id:         UUID4Field
    record_type:     str = Field(..., min_length=2, max_length=100)
    medical_history: str
    notes:           Optional[str] = None
    created_at:      datetime


# ─────────────────────────────────────────────────────────────────────────────
# Doctor Availability schemas
# ─────────────────────────────────────────────────────────────────────────────

class AvailabilityRequest(BaseModel):
    day_of_week: int = Field(..., ge=0, le=6)
    start_time:  str = Field(..., pattern=r"^\d{2}:\d{2}$")
    end_time:    str = Field(..., pattern=r"^\d{2}:\d{2}$")
    slot_duration_mins: int = 30


class AvailabilityResponse(BaseModel):
    availability_id: str
    doctor_id:       str
    day_of_week:     int
    start_time:      str
    end_time:        str
    is_active:       bool = True


# ─────────────────────────────────────────────────────────────────────────────
# Scan Analysis schemas
# ─────────────────────────────────────────────────────────────────────────────

class DiseasePredictionDto(BaseModel):
    disease_name:   str
    confidence:     float
    severity_level: str
    medical_advice: str


class ScanResultDto(BaseModel):
    scanId:     str
    timestamp:  int
    prediction: DiseasePredictionDto


# ─────────────────────────────────────────────────────────────────────────────
# Wallet & Transaction schemas
# ─────────────────────────────────────────────────────────────────────────────

class WalletTransactionResponse(BaseModel):
    transaction_id: str
    doctor_id: UUID4Field
    appointment_id: Optional[str] = None
    type: str = Field(..., description="EARNING | WITHDRAWAL")
    amount: float
    platform_fee: float
    net_amount: float
    status: str = Field(..., description="SUCCESS | PENDING | FAILED")
    created_at: datetime


class WalletResponse(BaseModel):
    doctor_id: UUID4Field
    total_balance: float
    pending_clearance: float
    recent_transactions: List[WalletTransactionResponse]


class WithdrawalRequest(BaseModel):
    amount: float = Field(..., gt=0)
    bank_account_id: Optional[str] = None

