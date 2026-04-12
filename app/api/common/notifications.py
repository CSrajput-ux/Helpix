"""
app/api/notifications.py
-------------------------
Health Notifications & Alerts:
  GET  /notifications           – List user's notifications (newest first)
  POST /notifications           – Create a notification (system/doctor use)
  PATCH /notifications/{id}/read – Mark a notification as read
  DELETE /notifications/{id}    – Delete a notification

Notification Types (maps to Home Health Feed screen):
  - MEDICINE_REMINDER  : "Time to take your Vitamin C"
  - MEDICAL_EMERGENCY  : "Your heartbeat is slightly above normal"
  - DISEASE_AREA_WARNING: "You are entering a High Disease Outbreak Zone"
  - GENERAL            : Generic health tip
"""

import uuid
from datetime import datetime, timezone
from typing import List, Optional

from fastapi import APIRouter, Depends, Query, HTTPException, status
from pydantic import BaseModel

router = APIRouter(prefix="/notifications", tags=["Health Notifications"])


# ---------------------------------------------------------------------------
# Schemas
# ---------------------------------------------------------------------------

class NotificationType(str):
    MEDICINE_REMINDER   = "MEDICINE_REMINDER"
    MEDICAL_EMERGENCY   = "MEDICAL_EMERGENCY"
    DISEASE_AREA_WARNING= "DISEASE_AREA_WARNING"
    GENERAL             = "GENERAL"


class CreateNotificationRequest(BaseModel):
    notification_type: str   # See NotificationType
    title: str
    message: str
    target_user_id: Optional[str] = None   # None = send to calling user
    severity: str = "INFO"                  # INFO | WARNING | CRITICAL


class NotificationResponse(BaseModel):
    notification_id: str
    user_id: str
    notification_type: str
    title: str
    message: str
    severity: str
    is_read: bool
    created_at: datetime


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def get_notif_collection():
    from app.core.db import db
    return db["notifications"]


def _doc_to_response(doc: dict) -> NotificationResponse:
    return NotificationResponse(
        notification_id=doc["notification_id"],
        user_id=doc["user_id"],
        notification_type=doc["notification_type"],
        title=doc["title"],
        message=doc["message"],
        severity=doc.get("severity", "INFO"),
        is_read=doc.get("is_read", False),
        created_at=doc["created_at"],
    )


# ---------------------------------------------------------------------------
# GET /notifications
# ---------------------------------------------------------------------------
@router.get("", response_model=List[NotificationResponse])
async def list_notifications(
    unread_only: bool = Query(default=False),
    limit: int = Query(default=30, ge=1, le=100),
    current_user: dict = Depends(__import__("app.core.security", fromlist=["get_current_user"]).get_current_user),
):
    """Fetch all notifications for the authenticated user."""
    col = get_notif_collection()
    query: dict = {"user_id": current_user["sub"]}
    if unread_only:
        query["is_read"] = False

    cursor = col.find(query).sort("created_at", -1).limit(limit)
    results = []
    async for doc in cursor:
        results.append(_doc_to_response(doc))
    return results


# ---------------------------------------------------------------------------
# POST /notifications  – Create / push a notification
# ---------------------------------------------------------------------------
@router.post("", response_model=NotificationResponse, status_code=status.HTTP_201_CREATED)
async def create_notification(
    body: CreateNotificationRequest,
    current_user: dict = Depends(__import__("app.core.security", fromlist=["get_current_user"]).get_current_user),
):
    """
    Create a health notification.
    Doctors can target specific patients via `target_user_id`.
    """
    col = get_notif_collection()
    notif_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    target = body.target_user_id or current_user["sub"]

    doc = {
        "notification_id": notif_id,
        "user_id": target,
        "notification_type": body.notification_type,
        "title": body.title,
        "message": body.message,
        "severity": body.severity,
        "is_read": False,
        "created_at": now,
        "created_by": current_user["sub"],
    }
    await col.insert_one(doc)
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# PATCH /notifications/{notification_id}/read
# ---------------------------------------------------------------------------
@router.patch("/{notification_id}/read", response_model=NotificationResponse)
async def mark_as_read(
    notification_id: str,
    current_user: dict = Depends(__import__("app.core.security", fromlist=["get_current_user"]).get_current_user),
):
    col = get_notif_collection()
    doc = await col.find_one({"notification_id": notification_id})
    if not doc:
        raise HTTPException(status_code=404, detail="Notification not found.")
    if doc["user_id"] != current_user["sub"]:
        raise HTTPException(status_code=403, detail="Access denied.")

    await col.update_one(
        {"notification_id": notification_id},
        {"$set": {"is_read": True}},
    )
    doc["is_read"] = True
    return _doc_to_response(doc)


# ---------------------------------------------------------------------------
# DELETE /notifications/{notification_id}
# ---------------------------------------------------------------------------
@router.delete("/{notification_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_notification(
    notification_id: str,
    current_user: dict = Depends(__import__("app.core.security", fromlist=["get_current_user"]).get_current_user),
):
    col = get_notif_collection()
    doc = await col.find_one({"notification_id": notification_id})
    if not doc:
        raise HTTPException(status_code=404, detail="Notification not found.")
    if doc["user_id"] != current_user["sub"]:
        raise HTTPException(status_code=403, detail="Access denied.")
    await col.delete_one({"notification_id": notification_id})
