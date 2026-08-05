"""
app/api/appointments.py
------------------------
Appointment booking routes:
  POST /appointments             – Book a new appointment
  GET  /appointments             – List own appointments (patient or doctor)
  GET  /appointments/{id}        – Get single appointment detail
  PATCH /appointments/{id}/status – Doctor updates appointment status
  DELETE /appointments/{id}      – Cancel an appointment
"""

import uuid
from datetime import datetime, timezone
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status
from pydantic import BaseModel, Field, field_validator

from app.core.db import get_users_collection
from app.core.security import get_current_user

router = APIRouter(prefix="/appointments", tags=["Appointments"])


# ---------------------------------------------------------------------------
# Schemas
# ---------------------------------------------------------------------------

_UUID4 = r"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"


class AppointmentStatus(str):
    """Valid values for appointment status."""
    __values__ = {"SCHEDULED", "COMPLETED", "CANCELLED", "PENDING"}


class BookAppointmentRequest(BaseModel):
    doctor_id: str = Field(..., pattern=_UUID4, description="UUID of the doctor")
    appointment_datetime: datetime
    reason: Optional[str] = Field(None, min_length=3, max_length=500)
    notes:  Optional[str] = Field(None, min_length=3, max_length=1000)

    @field_validator("appointment_datetime")
    @classmethod
    def must_be_future(cls, v: datetime) -> datetime:
        ts = v if v.tzinfo else v.replace(tzinfo=timezone.utc)
        if ts <= datetime.now(timezone.utc):
            raise ValueError("appointment_datetime must be in the future")
        return v


class UpdateStatusRequest(BaseModel):
    status: str = Field(
        ...,
        pattern=r"^(SCHEDULED|COMPLETED|CANCELLED|PENDING)$",
        description="SCHEDULED | COMPLETED | CANCELLED | PENDING",
    )


class AppointmentResponse(BaseModel):
    appointment_id:       str
    patient_id:           str
    doctor_id:            str
    appointment_datetime: datetime
    status:               str
    reason:               Optional[str] = None
    notes:                Optional[str] = None
    amount:               float = 0.0
    platform_fee:         float = 0.0
    payment_status:       str = "PENDING"
    booked_at:            datetime


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def get_appointments_collection():
    from app.core.db import db
    return db["appointments"]


def _doc_to_response(doc: dict) -> AppointmentResponse:
    return AppointmentResponse(
        appointment_id=doc["appointment_id"],
        patient_id=doc["patient_id"],
        doctor_id=doc["doctor_id"],
        appointment_datetime=doc["appointment_datetime"],
        status=doc["status"],
        reason=doc.get("reason"),
        notes=doc.get("notes"),
        amount=doc.get("amount", 0.0),
        platform_fee=doc.get("platform_fee", 0.0),
        payment_status=doc.get("payment_status", "PENDING"),
        booked_at=doc["booked_at"],
    )


# ---------------------------------------------------------------------------
# POST /appointments  – Book appointment
# ---------------------------------------------------------------------------
@router.post("", response_model=AppointmentResponse, status_code=status.HTTP_201_CREATED)
async def book_appointment(
    body: BookAppointmentRequest,
    current_user: dict = Depends(get_current_user),
):
    """
    Patient books an appointment with a doctor.
    - The doctor_id must belong to a registered DOCTOR.
    - Initial status is PENDING.
    """
    users = get_users_collection()
    doctor = await users.find_one({"user_id": body.doctor_id, "role": "DOCTOR"})
    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Doctor '{body.doctor_id}' not found.",
        )

    col = get_appointments_collection()
    appointment_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    
    fee = doctor.get("consultation_fee", 500.0)
    platform_fee = 0.0

    doc = {
        "appointment_id": appointment_id,
        "patient_id": current_user["sub"],
        "doctor_id": body.doctor_id,
        "appointment_datetime": body.appointment_datetime,
        "status": "PENDING",
        "reason": body.reason,
        "notes": body.notes,
        "amount": fee,
        "platform_fee": platform_fee,
        "payment_status": "PENDING",
        "booked_at": now,
        "updated_at": now,
    }
    await col.insert_one(doc)
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# GET /appointments  – List appointments
# ---------------------------------------------------------------------------
@router.get("", response_model=List[AppointmentResponse])
async def list_appointments(
    appt_status: Optional[str] = Query(None, alias="status"),
    limit: int = Query(default=20, ge=1, le=100),
    current_user: dict = Depends(get_current_user),
):
    """
    - PATIENT sees their own bookings.
    - DOCTOR sees appointments where they are the doctor.
    """
    col = get_appointments_collection()
    role = current_user.get("role")

    query: dict = {}
    if role == "DOCTOR":
        query["doctor_id"] = current_user["sub"]
    else:
        query["patient_id"] = current_user["sub"]

    if appt_status:
        query["status"] = appt_status.upper()

    cursor = col.find(query).sort("appointment_datetime", 1).limit(limit)
    results = []
    async for doc in cursor:
        results.append(_doc_to_response(doc))
    return results


# ---------------------------------------------------------------------------
# GET /appointments/{appointment_id}
# ---------------------------------------------------------------------------
@router.get("/{appointment_id}", response_model=AppointmentResponse)
async def get_appointment(
    appointment_id: str,
    current_user: dict = Depends(get_current_user),
):
    col = get_appointments_collection()
    doc = await col.find_one({"appointment_id": appointment_id})
    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Appointment not found.")

    uid = current_user["sub"]
    if doc["patient_id"] != uid and doc["doctor_id"] != uid:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied.")

    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# PATCH /appointments/{appointment_id}/status  – Update appointment status
# ---------------------------------------------------------------------------
@router.patch("/{appointment_id}/status", response_model=AppointmentResponse)
async def update_appointment_status(
    appointment_id: str,
    body: UpdateStatusRequest,
    current_user: dict = Depends(get_current_user),
):
    """Doctor marks appointment as COMPLETED or CANCELLED."""
    col = get_appointments_collection()
    doc = await col.find_one({"appointment_id": appointment_id})
    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Appointment not found.")

    uid = current_user["sub"]
    if doc["doctor_id"] != uid and doc["patient_id"] != uid:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied.")

    valid = {"SCHEDULED", "COMPLETED", "CANCELLED", "PENDING"}
    if body.status.upper() not in valid:
        raise HTTPException(status_code=400, detail=f"Invalid status. Choose from {valid}")

    now = datetime.now(timezone.utc)
    update_fields = {"status": body.status.upper(), "updated_at": now}

    if body.status.upper() == "COMPLETED" and doc.get("payment_status", "PENDING") == "PENDING":
        from app.core.db import db
        transactions = db["transactions"]
        
        amount = doc.get("amount", 0.0)
        platform_fee = doc.get("platform_fee", 0.0)
        net_amount = amount - platform_fee
        
        tx_id = str(uuid.uuid4())
        await transactions.insert_one({
            "transaction_id": tx_id,
            "doctor_id": doc["doctor_id"],
            "appointment_id": appointment_id,
            "type": "EARNING",
            "amount": amount,
            "platform_fee": platform_fee,
            "net_amount": net_amount,
            "status": "SUCCESS",
            "created_at": now
        })
        
        update_fields["payment_status"] = "SUCCESS"
        doc["payment_status"] = "SUCCESS"

    await col.update_one(
        {"appointment_id": appointment_id},
        {"$set": update_fields},
    )
    doc["status"] = body.status.upper()
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# DELETE /appointments/{appointment_id}
# ---------------------------------------------------------------------------
@router.delete("/{appointment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def cancel_appointment(
    appointment_id: str,
    current_user: dict = Depends(get_current_user),
):
    col = get_appointments_collection()
    doc = await col.find_one({"appointment_id": appointment_id})
    if not doc:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Appointment not found.")
    if doc["patient_id"] != current_user["sub"] and doc["doctor_id"] != current_user["sub"]:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied.")
    await col.delete_one({"appointment_id": appointment_id})
