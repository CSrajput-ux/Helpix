"""
app/api/medicine_reminder.py
-----------------------------
Medicine Reminders — powers the "Medicine Reminders" tool and notifications.

  POST /reminders              – Create a reminder
  GET  /reminders              – List all reminders for a user
  GET  /reminders/{id}         – Get single reminder
  PATCH /reminders/{id}        – Update reminder
  DELETE /reminders/{id}       – Delete reminder
  POST /reminders/{id}/taken   – Mark dose as taken (logs compliance)
"""

import uuid
from datetime import datetime, timezone
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, Field, field_validator

from app.core.security import get_current_user

router = APIRouter(prefix="/reminders", tags=["Medicine Reminders"])


# ---------------------------------------------------------------------------
# Schemas
# ---------------------------------------------------------------------------

_DATE  = r"^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$"
_TIME  = r"^([01]\d|2[0-3]):[0-5]\d$"


class CreateReminderRequest(BaseModel):
    medicine_name:  str  = Field(..., min_length=1, max_length=150, pattern=r"^[\w\s\-\.]+$")
    dosage:         str  = Field(..., min_length=1, max_length=50,  pattern=r"^[\w\s\.\-/]+$")
    frequency:      str  = Field(
        ..., min_length=2, max_length=100,
        pattern=r"^(Once daily|Twice daily|Three times daily|Every \d+ hours?|As needed|[\w\s]+)$",
    )
    reminder_times: List[str] = Field(..., min_length=1, max_length=10)
    start_date:     str  = Field(..., pattern=_DATE)
    end_date:       Optional[str]  = Field(None, pattern=_DATE)
    instructions:   Optional[str]  = Field(None, min_length=2, max_length=500)
    is_active:      bool = True

    @field_validator("reminder_times", mode="before")
    @classmethod
    def validate_times(cls, v):
        import re
        if not isinstance(v, list):
            raise ValueError("reminder_times must be a list")
        for t in v:
            if not isinstance(t, str) or not re.match(r"^([01]\d|2[0-3]):[0-5]\d$", t):
                raise ValueError(f"Each reminder time must be in HH:MM format, got: {t!r}")
        return v


class UpdateReminderRequest(BaseModel):
    medicine_name:  Optional[str]       = Field(None, min_length=1, max_length=150, pattern=r"^[\w\s\-\.]+$")
    dosage:         Optional[str]       = Field(None, min_length=1, max_length=50,  pattern=r"^[\w\s\.\-/]+$")
    frequency:      Optional[str]       = Field(None, min_length=2, max_length=100)
    reminder_times: Optional[List[str]] = Field(None, min_length=1, max_length=10)
    end_date:       Optional[str]       = Field(None, pattern=_DATE)
    instructions:   Optional[str]       = Field(None, min_length=2, max_length=500)
    is_active:      Optional[bool]      = None

    @field_validator("reminder_times", mode="before")
    @classmethod
    def validate_times(cls, v):
        import re
        if v is None:
            return v
        if not isinstance(v, list):
            raise ValueError("reminder_times must be a list")
        for t in v:
            if not isinstance(t, str) or not re.match(r"^([01]\d|2[0-3]):[0-5]\d$", t):
                raise ValueError(f"Each reminder time must be in HH:MM format, got: {t!r}")
        return v


class ReminderResponse(BaseModel):
    reminder_id:       str
    user_id:           str
    medicine_name:     str
    dosage:            str
    frequency:         str
    reminder_times:    List[str]
    start_date:        str
    end_date:          Optional[str]
    instructions:      Optional[str]
    is_active:         bool
    created_at:        datetime
    next_dose_in_mins: Optional[int] = None


class DoseTakenResponse(BaseModel):
    message:      str
    reminder_id:  str
    taken_at:     datetime


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def get_reminders_collection():
    from app.core.db import db
    return db["medicine_reminders"]


def get_dose_log_collection():
    from app.core.db import db
    return db["dose_logs"]


def _doc_to_response(doc: dict) -> ReminderResponse:
    return ReminderResponse(
        reminder_id=doc["reminder_id"],
        user_id=doc["user_id"],
        medicine_name=doc["medicine_name"],
        dosage=doc["dosage"],
        frequency=doc["frequency"],
        reminder_times=doc.get("reminder_times", []),
        start_date=doc["start_date"],
        end_date=doc.get("end_date"),
        instructions=doc.get("instructions"),
        is_active=doc.get("is_active", True),
        created_at=doc["created_at"],
    )


# ---------------------------------------------------------------------------
# POST /reminders
# ---------------------------------------------------------------------------
@router.post("", response_model=ReminderResponse, status_code=status.HTTP_201_CREATED)
async def create_reminder(
    body: CreateReminderRequest,
    current_user: dict = Depends(get_current_user),
):
    """Create a new medicine reminder."""
    col = get_reminders_collection()
    reminder_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    doc = {
        "reminder_id": reminder_id,
        "user_id": current_user["sub"],
        "medicine_name": body.medicine_name,
        "dosage": body.dosage,
        "frequency": body.frequency,
        "reminder_times": body.reminder_times,
        "start_date": body.start_date,
        "end_date": body.end_date,
        "instructions": body.instructions,
        "is_active": body.is_active,
        "created_at": now,
        "updated_at": now,
    }
    await col.insert_one(doc)
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# GET /reminders
# ---------------------------------------------------------------------------
@router.get("", response_model=List[ReminderResponse])
async def list_reminders(
    current_user: dict = Depends(get_current_user),
):
    col = get_reminders_collection()
    cursor = col.find({"user_id": current_user["sub"]}).sort("created_at", -1)
    results = []
    async for doc in cursor:
        results.append(_doc_to_response(doc))
    return results


# ---------------------------------------------------------------------------
# GET /reminders/{reminder_id}
# ---------------------------------------------------------------------------
@router.get("/{reminder_id}", response_model=ReminderResponse)
async def get_reminder(
    reminder_id: str,
    current_user: dict = Depends(get_current_user),
):
    col = get_reminders_collection()
    doc = await col.find_one({"reminder_id": reminder_id, "user_id": current_user["sub"]})
    if not doc:
        raise HTTPException(status_code=404, detail="Reminder not found.")
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# PATCH /reminders/{reminder_id}
# ---------------------------------------------------------------------------
@router.patch("/{reminder_id}", response_model=ReminderResponse)
async def update_reminder(
    reminder_id: str,
    body: UpdateReminderRequest,
    current_user: dict = Depends(get_current_user),
):
    col = get_reminders_collection()
    doc = await col.find_one({"reminder_id": reminder_id, "user_id": current_user["sub"]})
    if not doc:
        raise HTTPException(status_code=404, detail="Reminder not found.")

    updates = {k: v for k, v in body.model_dump().items() if v is not None}
    updates["updated_at"] = datetime.now(timezone.utc)

    await col.update_one({"reminder_id": reminder_id}, {"$set": updates})
    doc.update(updates)
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# DELETE /reminders/{reminder_id}
# ---------------------------------------------------------------------------
@router.delete("/{reminder_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_reminder(
    reminder_id: str,
    current_user: dict = Depends(get_current_user),
):
    col = get_reminders_collection()
    result = await col.delete_one({"reminder_id": reminder_id, "user_id": current_user["sub"]})
    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Reminder not found.")


# ---------------------------------------------------------------------------
# POST /reminders/{reminder_id}/taken  – Log dose taken
# ---------------------------------------------------------------------------
@router.post("/{reminder_id}/taken", response_model=DoseTakenResponse)
async def mark_dose_taken(
    reminder_id: str,
    current_user: dict = Depends(get_current_user),
):
    """Mark a medicine dose as taken. Logs to dose_logs for compliance tracking."""
    col = get_reminders_collection()
    doc = await col.find_one({"reminder_id": reminder_id, "user_id": current_user["sub"]})
    if not doc:
        raise HTTPException(status_code=404, detail="Reminder not found.")

    dose_col = get_dose_log_collection()
    now = datetime.now(timezone.utc)
    await dose_col.insert_one({
        "log_id": str(uuid.uuid4()),
        "reminder_id": reminder_id,
        "user_id": current_user["sub"],
        "medicine_name": doc["medicine_name"],
        "taken_at": now,
    })

    return DoseTakenResponse(
        message=f"Dose of {doc['medicine_name']} marked as taken.",
        reminder_id=reminder_id,
        taken_at=now,
    )
