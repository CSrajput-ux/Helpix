"""
app/api/doctor/management.py
-----------------------------
Doctor-Patient management and observation:
  POST /doctor/follow                        – Doctor follows a patient
  GET  /doctor/patients                      – List all patients linked to the doctor
  DELETE /doctor/unfollow/{patient_id}       – Unlink a patient
  GET  /doctor/vitals/patient/{patient_id}   – View live vitals for a linked patient
  POST /medical-records                      – Add medical record (doctor or patient)
  GET  /medical-records                      – Get own medical records
"""

import uuid
from datetime import datetime, timezone
from typing import List

from fastapi import APIRouter, Depends, HTTPException, Query, status

from app.core.db import (
    get_doctor_links_collection,
    get_medical_records_collection,
    get_users_collection,
    get_vitals_collection,
)
from app.core.security import (
    decrypt_field,
    encrypt_field,
    get_current_user,
    require_doctor,
)
from app.models.schemas import (
    DoctorPatientsResponse,
    FollowPatientRequest,
    FollowPatientResponse,
    MedicalRecordCreate,
    MedicalRecordResponse,
    PatientSummary,
    DoctorSummary,
    VitalsResponse,
)

router = APIRouter(tags=["Doctor Management"])


# ---------------------------------------------------------------------------
# POST /doctor/follow
# ---------------------------------------------------------------------------
@router.post("/doctor/follow", response_model=FollowPatientResponse, status_code=status.HTTP_201_CREATED)
async def follow_patient(
    body: FollowPatientRequest,
    current_user: dict = Depends(require_doctor),
):
    """
    Doctor follows a patient using their unique `patient_id`.
    """
    users = get_users_collection()
    patient = await users.find_one({"user_id": body.patient_id, "role": "PATIENT"})
    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Patient with id '{body.patient_id}' not found.",
        )

    if body.patient_id == current_user["sub"]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="You cannot follow yourself.",
        )

    links = get_doctor_links_collection()
    now = datetime.now(timezone.utc)
    await links.update_one(
        {"doctor_id": current_user["sub"], "patient_id": body.patient_id},
        {"$setOnInsert": {"linked_at": now}},
        upsert=True,
    )

    return FollowPatientResponse(
        message="Successfully linked to patient.",
        doctor_id=current_user["sub"],
        patient_id=body.patient_id,
        linked_at=now,
    )


# ---------------------------------------------------------------------------
# GET /doctor/patients
# ---------------------------------------------------------------------------
@router.get("/doctor/patients", response_model=DoctorPatientsResponse)
async def get_my_patients(current_user: dict = Depends(require_doctor)):
    """Return all patients linked to the authenticated doctor."""
    links = get_doctor_links_collection()
    users = get_users_collection()

    cursor = links.find({"doctor_id": current_user["sub"]})
    patients: List[PatientSummary] = []

    async for link in cursor:
        patient = await users.find_one({"user_id": link["patient_id"]})
        if patient:
            patients.append(
                PatientSummary(
                    patient_id=patient["user_id"],
                    full_name=patient["full_name"],
                    email=patient["email"],
                    linked_at=link["linked_at"],
                )
            )

    return DoctorPatientsResponse(doctor_id=current_user["sub"], patients=patients)


# ---------------------------------------------------------------------------
# GET /doctors
# ---------------------------------------------------------------------------
@router.get("/doctors", response_model=List[DoctorSummary])
async def list_doctors(current_user: dict = Depends(get_current_user)):
    """Return a list of all registered doctors."""
    users = get_users_collection()
    cursor = users.find({"role": "DOCTOR"})
    results = []
    async for doc in cursor:
        results.append(
            DoctorSummary(
                user_id=doc["user_id"],
                full_name=doc["full_name"],
                specialization=doc.get("specialization"),
                clinic_address=doc.get("clinic_address"),
            )
        )
    return results


# ---------------------------------------------------------------------------
# DELETE /doctor/unfollow/{patient_id}
# ---------------------------------------------------------------------------
@router.delete("/doctor/unfollow/{patient_id}", status_code=status.HTTP_204_NO_CONTENT)
async def unfollow_patient(
    patient_id: str,
    current_user: dict = Depends(require_doctor),
):
    """Remove the doctor-patient link."""
    links = get_doctor_links_collection()
    result = await links.delete_one(
        {"doctor_id": current_user["sub"], "patient_id": patient_id}
    )
    if result.deleted_count == 0:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Link not found. You are not following this patient.",
        )


# ---------------------------------------------------------------------------
# GET /doctor/vitals/patient/{patient_id} – For doctors
# ---------------------------------------------------------------------------
@router.get("/vitals/patient/{patient_id}", response_model=List[VitalsResponse])
async def get_patient_vitals(
    patient_id: str,
    limit: int = Query(default=20, ge=1, le=200),
    current_user: dict = Depends(get_current_user),
):
    """
    Doctor endpoint: View live vitals dashboard for a linked patient.
    """
    if current_user.get("role") != "DOCTOR":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only doctors can view patient vitals.",
        )

    links = get_doctor_links_collection()
    link = await links.find_one(
        {"doctor_id": current_user["sub"], "patient_id": patient_id}
    )
    if not link:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You are not linked to this patient. Call /doctor/follow first.",
        )

    vitals = get_vitals_collection()
    cursor = (
        vitals.find({"user_id": patient_id})
        .sort("timestamp", -1)
        .limit(limit)
    )

    results = []
    async for doc in cursor:
        results.append(_doc_to_response(doc))

    return results


# ---------------------------------------------------------------------------
# POST /medical-records
# ---------------------------------------------------------------------------
@router.post("/medical-records", response_model=MedicalRecordResponse, status_code=status.HTTP_201_CREATED)
async def create_medical_record(
    body: MedicalRecordCreate,
    current_user: dict = Depends(get_current_user),
):
    """Create a medical record for the authenticated user."""
    records = get_medical_records_collection()
    record_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    encrypted_history = encrypt_field(body.medical_history)

    doc = {
        "record_id": record_id,
        "user_id": current_user["sub"],
        "record_type": body.record_type,
        "medical_history_enc": encrypted_history,
        "notes": body.notes,
        "created_at": now,
    }
    await records.insert_one(doc)

    return MedicalRecordResponse(
        record_id=record_id,
        user_id=current_user["sub"],
        record_type=body.record_type,
        medical_history=body.medical_history,
        notes=body.notes,
        created_at=now,
    )


# ---------------------------------------------------------------------------
# GET /medical-records
# ---------------------------------------------------------------------------
@router.get("/medical-records", response_model=List[MedicalRecordResponse])
async def get_medical_records(current_user: dict = Depends(get_current_user)):
    """Return all medical records for the authenticated user (decrypted)."""
    records = get_medical_records_collection()
    cursor = records.find(
        {"user_id": current_user["sub"]},
        sort=[("created_at", -1)],
    )

    results = []
    async for doc in cursor:
        results.append(
            MedicalRecordResponse(
                record_id=doc["record_id"],
                user_id=doc["user_id"],
                record_type=doc["record_type"],
                medical_history=decrypt_field(doc.get("medical_history_enc", "")),
                notes=doc.get("notes"),
                created_at=doc["created_at"],
            )
        )
    return results


# ---------------------------------------------------------------------------
# Helper
# ---------------------------------------------------------------------------
def _doc_to_response(doc: dict) -> VitalsResponse:
    bp_enc = doc.get("blood_pressure_enc")
    bp = decrypt_field(bp_enc) if bp_enc else None

    return VitalsResponse(
        id=doc.get("vital_id", str(doc.get("_id", ""))),
        user_id=doc["user_id"],
        heart_rate=doc["heart_rate"],
        steps=doc["steps"],
        spo2=doc["spo2"],
        blood_pressure=bp,
        device_id=doc.get("device_id"),
        timestamp=doc["timestamp"],
    )
