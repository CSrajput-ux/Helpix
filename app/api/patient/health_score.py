"""
app/api/health_score.py
------------------------
Daily Health Score — powers the "Today's Health Score" widget on the Health screen.

  GET  /health-score/today       – Get today's computed health score
  GET  /health-score/history     – Weekly score history for trend chart
  POST /health-score/compute     – Trigger recalculation (called after vitals sync)

Score Algorithm (0–100):
  - Heart Rate normal (60-100 bpm)  → +25 pts
  - SPO2 >= 97%                     → +25 pts
  - Steps >= 8000/day               → +25 pts
  - BP normal (systolic 90-120)     → +25 pts
  Labels:
    90-100 → Excellent
    70-89  → Good
    50-69  → Fair
    < 50   → Needs Attention
"""

import uuid
from datetime import datetime, timezone, timedelta
from typing import List

from fastapi import APIRouter, Depends, status
from pydantic import BaseModel

from app.core.security import get_current_user

router = APIRouter(prefix="/health-score", tags=["Health Score"])


# ---------------------------------------------------------------------------
# Schemas
# ---------------------------------------------------------------------------

class HealthScoreResponse(BaseModel):
    score_id: str
    user_id: str
    score: int                    # 0–100
    label: str                    # Excellent / Good / Fair / Needs Attention
    heart_rate_pts: int
    spo2_pts: int
    steps_pts: int
    bp_pts: int
    date: str                     # YYYY-MM-DD
    computed_at: datetime
    is_syncing: bool = False


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def get_score_collection():
    from app.core.db import db
    return db["health_scores"]


def get_vitals_collection():
    from app.core.db import db
    try:
        return db["vitals_ts"]
    except Exception:
        return db["vitals"]


def _label(score: int) -> str:
    if score >= 90: return "Excellent"
    if score >= 70: return "Good"
    if score >= 50: return "Fair"
    return "Needs Attention"


async def _compute_score(user_id: str) -> dict:
    """Pull today's latest vitals and compute a score."""
    vitals_col = get_vitals_collection()
    today_start = datetime.now(timezone.utc).replace(hour=0, minute=0, second=0, microsecond=0)

    latest = await vitals_col.find_one(
        {"user_id": user_id, "timestamp": {"$gte": today_start}},
        sort=[("timestamp", -1)],
    )

    hr_pts, spo2_pts, steps_pts, bp_pts = 0, 0, 0, 0
    is_syncing = True

    if latest:
        is_syncing = False
        hr = latest.get("heart_rate", 0)
        spo2 = latest.get("spo2", 0)
        steps = latest.get("steps", 0)
        bp_enc = latest.get("blood_pressure_enc")

        if 60 <= hr <= 100:
            hr_pts = 25
        elif 50 <= hr <= 110:
            hr_pts = 15

        if spo2 >= 97:
            spo2_pts = 25
        elif spo2 >= 94:
            spo2_pts = 15

        if steps >= 8000:
            steps_pts = 25
        elif steps >= 4000:
            steps_pts = 15

        if bp_enc:
            from app.core.security import decrypt_field
            bp_str = decrypt_field(bp_enc)
            try:
                systolic = int(bp_str.split("/")[0])
                if 90 <= systolic <= 120:
                    bp_pts = 25
                elif 80 <= systolic <= 135:
                    bp_pts = 15
            except Exception:
                pass

    total = hr_pts + spo2_pts + steps_pts + bp_pts
    return {
        "score": total,
        "label": _label(total),
        "heart_rate_pts": hr_pts,
        "spo2_pts": spo2_pts,
        "steps_pts": steps_pts,
        "bp_pts": bp_pts,
        "is_syncing": is_syncing,
    }


# ---------------------------------------------------------------------------
# GET /health-score/today
# ---------------------------------------------------------------------------
@router.get("/today", response_model=HealthScoreResponse)
async def get_today_score(
    current_user: dict = Depends(get_current_user),
):
    """Return today's health score. Computes on-the-fly from latest vitals."""
    col = get_score_collection()
    today_str = datetime.now(timezone.utc).strftime("%Y-%m-%d")

    existing = await col.find_one({"user_id": current_user["sub"], "date": today_str})
    if existing:
        return HealthScoreResponse(**{k: v for k, v in existing.items() if k != "_id"})

    result = await _compute_score(current_user["sub"])
    score_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    doc = {
        "score_id": score_id,
        "user_id": current_user["sub"],
        "date": today_str,
        "computed_at": now,
        **result,
    }
    await col.insert_one(doc)
    return HealthScoreResponse(**{k: v for k, v in doc.items() if k != "_id"})


# ---------------------------------------------------------------------------
# POST /health-score/compute  – Force recompute (call after vitals sync)
# ---------------------------------------------------------------------------
@router.post("/compute", response_model=HealthScoreResponse, status_code=status.HTTP_200_OK)
async def recompute_score(
    current_user: dict = Depends(get_current_user),
):
    """Recompute and overwrite today's health score from latest vitals."""
    col = get_score_collection()
    today_str = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    result = await _compute_score(current_user["sub"])
    score_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    doc = {
        "score_id": score_id,
        "user_id": current_user["sub"],
        "date": today_str,
        "computed_at": now,
        **result,
    }
    await col.update_one(
        {"user_id": current_user["sub"], "date": today_str},
        {"$set": doc},
        upsert=True,
    )
    return HealthScoreResponse(**{k: v for k, v in doc.items() if k != "_id"})


# ---------------------------------------------------------------------------
# GET /health-score/history  – Last N days
# ---------------------------------------------------------------------------
@router.get("/history", response_model=List[HealthScoreResponse])
async def get_score_history(
    days: int = 7,
    current_user: dict = Depends(get_current_user),
):
    """Return last N days of health scores for the trend chart."""
    col = get_score_collection()
    since = (datetime.now(timezone.utc) - timedelta(days=days)).strftime("%Y-%m-%d")

    cursor = col.find(
        {"user_id": current_user["sub"], "date": {"$gte": since}},
    ).sort("date", -1)

    results = []
    async for doc in cursor:
        results.append(HealthScoreResponse(**{k: v for k, v in doc.items() if k != "_id"}))
    return results
