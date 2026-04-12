"""
app/api/patient/vitals.py
-------------------------
Smartwatch vitals sync routes for Patients:
  POST /vitals/sync        – Receive real-time vitals from Android/Wearable
  GET  /vitals/history     – Retrieve own vitals history (paginated)
  GET  /vitals/latest      – Retrieve latest single vitals reading

High-Frequency Data Strategy
-----------------------------
1. **Time-Series Collection**: MongoDB 5.0+ vitals_ts collection.
2. **Client-Side Batching**: Android app buffers for 30–60 seconds.
3. **Rate Limiting**: Future enhancement.
"""

import uuid
from datetime import datetime, timezone
from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status

from app.core.db import get_vitals_collection
from app.core.security import decrypt_field, encrypt_field, get_current_user
from app.models.schemas import VitalsResponse, VitalsSyncRequest

router = APIRouter(prefix="/vitals", tags=["Patient Vitals"])


# ---------------------------------------------------------------------------
# POST /vitals/sync
# ---------------------------------------------------------------------------
@router.post("/sync", response_model=VitalsResponse, status_code=status.HTTP_201_CREATED)
async def sync_vitals(body: VitalsSyncRequest, current_user: dict = Depends(get_current_user)):
    """
    Receive and store a single vitals reading from the smartwatch.
    """
    vitals = get_vitals_collection()

    now = datetime.now(timezone.utc)
    timestamp = body.client_timestamp or now
    vital_id = str(uuid.uuid4())

    # Encrypt sensitive field
    encrypted_bp = encrypt_field(body.blood_pressure) if body.blood_pressure else None

    doc = {
        "vital_id": vital_id,
        "user_id": current_user["sub"],
        "timestamp": timestamp,
        "heart_rate": body.heart_rate,
        "steps": body.steps,
        "spo2": body.spo2,
        "blood_pressure_enc": encrypted_bp,
        "device_id": body.device_id,
        "synced_at": now,
    }

    await vitals.insert_one(doc)

    return VitalsResponse(
        id=vital_id,
        user_id=current_user["sub"],
        heart_rate=body.heart_rate,
        steps=body.steps,
        spo2=body.spo2,
        blood_pressure=body.blood_pressure,
        device_id=body.device_id,
        timestamp=timestamp,
    )


# ---------------------------------------------------------------------------
# GET /vitals/latest
# ---------------------------------------------------------------------------
@router.get("/latest", response_model=VitalsResponse)
async def get_latest_vitals(current_user: dict = Depends(get_current_user)):
    """Return the most recent vitals reading for the authenticated user."""
    vitals = get_vitals_collection()

    doc = await vitals.find_one(
        {"user_id": current_user["sub"]},
        sort=[("timestamp", -1)],
    )

    if not doc:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No vitals data found.",
        )

    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# GET /vitals/history
# ---------------------------------------------------------------------------
@router.get("/history", response_model=List[VitalsResponse])
async def get_vitals_history(
    limit: int = Query(default=50, ge=1, le=500),
    skip: int = Query(default=0, ge=0),
    current_user: dict = Depends(get_current_user),
):
    """Return paginated vitals history (newest first)."""
    vitals = get_vitals_collection()

    cursor = (
        vitals.find({"user_id": current_user["sub"]})
        .sort("timestamp", -1)
        .skip(skip)
        .limit(limit)
    )

    results = []
    async for doc in cursor:
        results.append(_doc_to_response(doc))

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
