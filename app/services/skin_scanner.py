"""Server-side client for the configured skin-lesion model.

The model used by Helpix is ``kokulan123/skin-lesion-classifier``. It is
served by the model author's public Space, whose API accepts an image URL.
The photo is uploaded to a random Cloudinary object only for inference and is
always deleted afterwards. No simulated medical result is ever returned.
"""

import logging
import uuid
from typing import Any

import cloudinary.uploader
import requests

from app.core.config import settings

logger = logging.getLogger(__name__)

CLASS_NAMES = {
    "Actinic Keratosis", "Basal Cell Carcinoma", "Benign Keratosis-like Lesion",
    "Dermatofibroma", "Melanoma", "Melanocytic Nevi", "Vascular Lesion",
}


def _severity_for(label: str) -> str:
    """A triage hint only; this is not a clinical diagnosis or severity score."""
    if label in {"Melanoma", "Basal Cell Carcinoma", "Actinic Keratosis"}:
        return "urgent_review"
    return "review_recommended"


def _recommendation_for(label: str) -> str:
    if label in {"Melanoma", "Basal Cell Carcinoma", "Actinic Keratosis"}:
        return "This research model found a pattern that needs prompt review by a dermatologist. It is not a diagnosis."
    return "This result is for informational screening only. Please arrange a dermatologist review if the lesion is new, changing, painful, bleeding, or concerning."


def _parse_prediction(payload: Any) -> dict[str, Any]:
    """Validate and normalize the response from the model Space."""
    if not isinstance(payload, dict):
        raise ValueError("Skin model returned an invalid response.")
    label = payload.get("class")
    confidence = payload.get("confidence")
    all_predictions = payload.get("all_predictions")
    if label not in CLASS_NAMES or not isinstance(confidence, (int, float)):
        raise ValueError("Skin model returned an incomplete response.")
    if not isinstance(all_predictions, dict):
        raise ValueError("Skin model returned no class probabilities.")

    top_3 = [
        {"label": str(name), "confidence": float(score)}
        for name, score in all_predictions.items()
        if name in CLASS_NAMES and isinstance(score, (int, float))
    ]
    top_3.sort(key=lambda item: item["confidence"], reverse=True)
    if not top_3:
        raise ValueError("Skin model returned no usable class probabilities.")
    return {
        "condition": label,
        "confidence": max(0.0, min(float(confidence), 1.0)),
        "severity": _severity_for(label),
        "recommendation": _recommendation_for(label),
        "top_3": top_3[:3],
    }


def predict_skin_disease(image_bytes: bytes) -> tuple[dict[str, Any] | None, str | None]:
    """Run the classifier and delete its temporary image in every code path."""
    if not image_bytes:
        return None, "The uploaded image is empty."
    if not settings.SKIN_INFERENCE_URL:
        return None, "Skin inference is not configured."
    if not all((settings.CLOUDINARY_CLOUD_NAME, settings.CLOUDINARY_API_KEY, settings.CLOUDINARY_API_SECRET)):
        return None, "Temporary image storage for skin inference is not configured."

    public_id: str | None = None
    try:
        upload = cloudinary.uploader.upload(
            image_bytes,
            public_id=f"helpix-tmp-skin/{uuid.uuid4().hex}",
            resource_type="image",
            overwrite=False,
        )
        public_id = upload.get("public_id")
        image_url = upload.get("secure_url")
        if not public_id or not image_url:
            raise ValueError("Temporary image upload did not return a URL.")
        response = requests.post(
            settings.SKIN_INFERENCE_URL,
            json={"url": image_url},
            timeout=settings.SKIN_INFERENCE_TIMEOUT_SECONDS,
        )
        if response.status_code != 200:
            logger.warning("Skin inference returned HTTP %s", response.status_code)
            return None, "The skin analysis service is temporarily unavailable."
        return _parse_prediction(response.json()), None
    except (requests.RequestException, ValueError) as exc:
        logger.warning("Skin inference failed: %s", exc)
        return None, "The skin analysis service is temporarily unavailable."
    except Exception:
        logger.exception("Unexpected skin inference failure")
        return None, "The skin analysis service is temporarily unavailable."
    finally:
        if public_id:
            try:
                cloudinary.uploader.destroy(public_id, resource_type="image", invalidate=True)
            except Exception:
                logger.exception("Could not delete temporary skin image %s", public_id)
