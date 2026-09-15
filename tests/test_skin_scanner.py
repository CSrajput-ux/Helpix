"""Tests for the real skin-model integration and its no-fake-result policy."""

import io
from types import SimpleNamespace

import pytest
from httpx import ASGITransport, AsyncClient
from PIL import Image

from app.core.security import create_access_token
from app.services import skin_scanner


def _configure_skin(monkeypatch):
    monkeypatch.setattr(skin_scanner.settings, "SKIN_INFERENCE_URL", "https://model.test/api/predict")
    monkeypatch.setattr(skin_scanner.settings, "SKIN_INFERENCE_TIMEOUT_SECONDS", 5)
    monkeypatch.setattr(skin_scanner.settings, "CLOUDINARY_CLOUD_NAME", "test-cloud")
    monkeypatch.setattr(skin_scanner.settings, "CLOUDINARY_API_KEY", "test-key")
    monkeypatch.setattr(skin_scanner.settings, "CLOUDINARY_API_SECRET", "test-secret")


def test_skin_model_response_is_normalized_and_temporary_image_is_deleted(monkeypatch):
    _configure_skin(monkeypatch)
    deleted = []
    monkeypatch.setattr(
        skin_scanner.cloudinary.uploader,
        "upload",
        lambda *_args, **_kwargs: {
            "public_id": "helpix-tmp-skin/one-time-image",
            "secure_url": "https://temporary.example/image.jpg",
        },
    )
    monkeypatch.setattr(
        skin_scanner.cloudinary.uploader,
        "destroy",
        lambda public_id, **_kwargs: deleted.append(public_id),
    )
    monkeypatch.setattr(
        skin_scanner.requests,
        "post",
        lambda *_args, **_kwargs: SimpleNamespace(
            status_code=200,
            json=lambda: {
                "class": "Melanoma",
                "confidence": 0.91,
                "all_predictions": {
                    "Melanocytic Nevi": 0.06,
                    "Melanoma": 0.91,
                    "Basal Cell Carcinoma": 0.03,
                },
            },
        ),
    )

    prediction, error = skin_scanner.predict_skin_disease(b"valid-image-bytes")

    assert error is None
    assert prediction["condition"] == "Melanoma"
    assert prediction["severity"] == "urgent_review"
    assert prediction["top_3"][0] == {"label": "Melanoma", "confidence": 0.91}
    assert deleted == ["helpix-tmp-skin/one-time-image"]


def test_skin_model_failure_never_becomes_a_random_diagnosis(monkeypatch):
    _configure_skin(monkeypatch)
    monkeypatch.setattr(
        skin_scanner.cloudinary.uploader,
        "upload",
        lambda *_args, **_kwargs: {
            "public_id": "helpix-tmp-skin/one-time-image",
            "secure_url": "https://temporary.example/image.jpg",
        },
    )
    monkeypatch.setattr(skin_scanner.cloudinary.uploader, "destroy", lambda *_args, **_kwargs: None)
    monkeypatch.setattr(
        skin_scanner.requests,
        "post",
        lambda *_args, **_kwargs: SimpleNamespace(status_code=503),
    )

    prediction, error = skin_scanner.predict_skin_disease(b"valid-image-bytes")

    assert prediction is None
    assert error == "The skin analysis service is temporarily unavailable."


@pytest.mark.asyncio
async def test_skin_route_returns_503_when_inference_is_unavailable(monkeypatch):
    from app.main import app

    # Patch the function at its import site because the route imports it lazily.
    monkeypatch.setattr(skin_scanner, "predict_skin_disease", lambda _image: (None, "Skin inference is unavailable."))
    token = create_access_token({"sub": "test-skin-user", "email": "skin@helpix.local", "role": "PATIENT"})
    image = Image.new("RGB", (16, 16), (180, 130, 100))
    content = io.BytesIO()
    image.save(content, format="JPEG")
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as client:
        response = await client.post(
            "/tools/skin-scan",
            files={"image": ("skin.jpg", content.getvalue(), "image/jpeg")},
            headers={"Authorization": f"Bearer {token}"},
        )

    assert response.status_code == 503
    assert response.json()["detail"] == "Skin inference is unavailable."
