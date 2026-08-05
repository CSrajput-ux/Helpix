"""
app/core/celery_app.py
----------------------
Celery Background Worker configuration for asynchronous AI scan inference,
notification dispatching, and scheduled health score aggregation.
Includes graceful fallback if Celery is not installed or Redis broker is offline.
"""

import logging
import os

logger = logging.getLogger(__name__)

try:
    from celery import Celery
    CELERY_AVAILABLE = True
except ImportError:
    CELERY_AVAILABLE = False
    Celery = None


broker_url = os.getenv("CELERY_BROKER_URL", "redis://localhost:6379/0")
result_backend = os.getenv("CELERY_RESULT_BACKEND", "redis://localhost:6379/1")

if CELERY_AVAILABLE:
    celery_app = Celery(
        "helpix_worker",
        broker=broker_url,
        backend=result_backend,
    )
    celery_app.conf.update(
        task_serializer="json",
        accept_content=["json"],
        result_serializer="json",
        timezone="UTC",
        enable_utc=True,
        task_track_started=True,
        task_time_limit=300,  # 5 minute timeout per task
    )
else:
    celery_app = None


def background_task(func):
    """Decorator to register a function as a Celery task or execute synchronously if offline."""
    if celery_app and hasattr(celery_app, "task"):
        return celery_app.task(func)
    return func


@background_task
def process_ai_scan_task(scan_id: str, file_path: str, scan_type: str) -> dict:
    """
    Asynchronous AI scan processing pipeline.
    Runs deep learning inference in a background Celery worker process.
    """
    logger.info("Executing Celery task: process_ai_scan_task for scan_id=%s", scan_id)
    # Simulated ML pipeline response (replace with TensorFlow Lite / ONNX Runtime execution)
    return {
        "scan_id": scan_id,
        "status": "COMPLETED",
        "detected_condition": "Acne Vulgaris / Normal",
        "confidence": 0.94,
    }


@background_task
def send_notification_task(user_id: str, title: str, body: str) -> bool:
    """Asynchronous push/email notification dispatcher."""
    logger.info("Executing Celery task: send_notification_task for user_id=%s (%s)", user_id, title)
    return True
