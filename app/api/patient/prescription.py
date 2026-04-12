"""
app/api/prescription.py
------------------------
Prescription AI routes:
  POST /process-prescription – Accept an image, simulate LayoutLMv3, return JSON

Simulation Note
---------------
Running the actual LayoutLMv3 model requires ~8 GB VRAM and complex setup.
This endpoint simulates the OCR + NLP pipeline with a realistic response.
To integrate the real model:
  1. pip install transformers torch Pillow
  2. Load model: processor = LayoutLMv3Processor.from_pretrained(...)
  3. Run inference and extract entities for MEDICINE_NAME, DOSAGE, FREQUENCY.
"""

import uuid
import random
from datetime import datetime, timezone
from typing import Optional

from fastapi import APIRouter, Depends, File, Form, HTTPException, UploadFile, status
from PIL import Image
import io

from app.core.db import get_prescriptions_collection
from app.core.security import get_current_user
from app.models.schemas import MedicineEntry, PrescriptionResponse

router = APIRouter(prefix="", tags=["Prescription AI"])


# ---------------------------------------------------------------------------
# POST /process-prescription
# ---------------------------------------------------------------------------
@router.post("/process-prescription", response_model=PrescriptionResponse)
async def process_prescription(
    file: UploadFile = File(..., description="Prescription image (JPG/PNG/PDF)"),
    patient_notes: Optional[str] = Form(None),
    current_user: dict = Depends(get_current_user),
):
    """
    Process a prescription image and extract medicine details.

    **Accepts**: JPG, PNG images or PDF files.
    **Returns**: Structured list of medicines with dosage and frequency.

    In production, replace the simulation block with:
    ```python
    from transformers import LayoutLMv3Processor, LayoutLMv3ForTokenClassification
    # Run inference → extract NER labels → parse entities
    ```
    """
    # Basic file type validation
    allowed_types = {"image/jpeg", "image/png", "image/jpg", "application/pdf"}
    if file.content_type not in allowed_types:
        raise HTTPException(
            status_code=status.HTTP_415_UNSUPPORTED_MEDIA_TYPE,
            detail=f"Unsupported file type '{file.content_type}'. Allowed: JPG, PNG, PDF.",
        )

    # Read the file
    contents = await file.read()

    # Validate image can be opened (skip for PDFs)
    if file.content_type != "application/pdf":
        try:
            img = Image.open(io.BytesIO(contents))
            img.verify()
        except Exception:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid or corrupted image file.",
            )

    # -----------------------------------------------------------------------
    # SIMULATION: Replace this block with real LayoutLMv3 inference
    # -----------------------------------------------------------------------
    simulated_medicines = _simulate_layoutlmv3_extraction(file.filename)
    confidence = round(random.uniform(0.82, 0.97), 4)
    # -----------------------------------------------------------------------

    # Store the result in MongoDB
    prescriptions = get_prescriptions_collection()
    prescription_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    doc = {
        "prescription_id": prescription_id,
        "user_id": current_user["sub"],
        "filename": file.filename,
        "content_type": file.content_type,
        "patient_notes": patient_notes,
        "extracted_medicines": [m.model_dump() for m in simulated_medicines],
        "confidence": confidence,
        "processed_at": now,
        "file_size_bytes": len(contents),
    }
    await prescriptions.insert_one(doc)

    return PrescriptionResponse(
        prescription_id=prescription_id,
        extracted_medicines=simulated_medicines,
        confidence=confidence,
        processed_at=now,
        raw_text="[Simulated OCR text — integrate real LayoutLMv3 for production]",
    )


# ---------------------------------------------------------------------------
# GET /prescriptions – List user's prescription history
# ---------------------------------------------------------------------------
@router.get("/prescriptions", response_model=list[PrescriptionResponse])
async def list_prescriptions(current_user: dict = Depends(get_current_user)):
    """Return all processed prescriptions for the authenticated user."""
    prescriptions = get_prescriptions_collection()
    cursor = prescriptions.find(
        {"user_id": current_user["sub"]},
        sort=[("processed_at", -1)],
    )

    results = []
    async for doc in cursor:
        meds = [MedicineEntry(**m) for m in doc.get("extracted_medicines", [])]
        results.append(
            PrescriptionResponse(
                prescription_id=doc["prescription_id"],
                extracted_medicines=meds,
                confidence=doc.get("confidence", 0.0),
                processed_at=doc["processed_at"],
            )
        )
    return results


# ---------------------------------------------------------------------------
# Helper: Simulate LayoutLMv3 output
# ---------------------------------------------------------------------------
def _simulate_layoutlmv3_extraction(filename: str) -> list[MedicineEntry]:
    """
    Simulates what a fine-tuned LayoutLMv3 model would return after
    processing a prescription image.

    Real implementation would:
    1. Convert image → pixel array
    2. Tokenize with word coordinates (bounding boxes)
    3. Run forward pass → NER labels per token
    4. Group consecutive tokens by entity type
    """
    sample_medicines = [
        MedicineEntry(
            medicine_name="Metformin",
            dosage="500mg",
            frequency="Twice daily",
            duration="3 months",
            instructions="Take after meals",
        ),
        MedicineEntry(
            medicine_name="Amlodipine",
            dosage="5mg",
            frequency="Once daily",
            duration="Ongoing",
            instructions="Take in the morning",
        ),
        MedicineEntry(
            medicine_name="Atorvastatin",
            dosage="10mg",
            frequency="Once daily at night",
            duration="6 months",
            instructions="Avoid grapefruit juice",
        ),
    ]

    # Return 1–3 medicines randomly to simulate variability
    count = random.randint(1, 3)
    return random.sample(sample_medicines, count)
