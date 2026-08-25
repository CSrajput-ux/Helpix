"""
app/api/tools.py
-----------------
Smart Tools Zone endpoints:
...
"""

import uuid
import random
import json
from datetime import datetime, timezone
from typing import List, Optional

from fastapi import APIRouter, Depends, File, Form, HTTPException, UploadFile, status
from pydantic import BaseModel, Field, field_validator

from app.core.security import get_current_user
from app.models.schemas import DiseasePredictionDto, ScanResultDto
import app.core.db as _db_module

router = APIRouter(prefix="/tools", tags=["Smart Tools Zone"])
root_router = APIRouter(tags=["AI Tools (Direct)"])


# ===========================================================================
# Schemas
# ===========================================================================

class SymptomCheckRequest(BaseModel):
    symptoms: List[str] = Field(
        ...,
        min_length=1, max_length=20,
        description="1–20 symptom strings",
    )
    age:           Optional[int]   = Field(None, ge=0, le=120)
    gender:        Optional[str]   = Field(None, pattern=r"^(Male|Female|Other|Prefer not to say)$")
    duration_days: Optional[int]   = Field(None, ge=0, le=365)

    @field_validator("symptoms", mode="before")
    @classmethod
    def validate_symptom_items(cls, v):
        from pydantic import ValidationError
        if not isinstance(v, list):
            raise ValueError("symptoms must be a list")
        for item in v:
            if not isinstance(item, str):
                raise ValueError("each symptom must be a string")
            if len(item.strip()) < 2:
                raise ValueError("each symptom must be at least 2 characters")
            if len(item) > 150:
                raise ValueError("each symptom must be at most 150 characters")
        return v


class DiagnosisResult(BaseModel):
    condition:      str
    probability:    float = Field(..., ge=0.0, le=1.0)
    severity:       str
    recommendation: str


class SymptomCheckResponse(BaseModel):
    check_id:           str
    possible_conditions: List[DiagnosisResult]
    overall_risk:       str
    should_see_doctor:  bool
    analyzed_at:        datetime


class ChatMessage(BaseModel):
    message:    str = Field(..., min_length=1, max_length=2000)
    session_id: Optional[str] = Field(None, min_length=36, max_length=36,
                                       pattern=r"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")


class ChatResponse(BaseModel):
    session_id:  str
    reply:       str
    suggestions: List[str]
    timestamp:   datetime


class SOSRequest(BaseModel):
    latitude:       float = Field(..., ge=-90.0,  le=90.0)
    longitude:      float = Field(..., ge=-180.0, le=180.0)
    emergency_type: str   = Field(
        "MEDICAL",
        pattern=r"^(MEDICAL|ACCIDENT|CARDIAC)$",
        description="MEDICAL | ACCIDENT | CARDIAC",
    )
    message: Optional[str] = Field(None, min_length=2, max_length=500)


class SOSResponse(BaseModel):
    sos_id:                  str
    status:                  str
    nearest_hospital:        str
    estimated_response_mins: int
    triggered_at:            datetime


class HospitalResult(BaseModel):
    hospital_id:        str
    name:               str
    address:            str
    distance_km:        float = Field(..., ge=0.0)
    phone:              str
    emergency_available: bool
    rating:             float = Field(..., ge=0.0, le=5.0)


class CoughAnalysisResponse(BaseModel):
    analysis_id:           str
    tb_risk:               str
    tb_probability:        float = Field(..., ge=0.0, le=1.0)
    respiratory_condition: str
    recommendation:        str
    confidence:            float = Field(..., ge=0.0, le=1.0)
    analyzed_at:           datetime


class PredictionItem(BaseModel):
    label: str
    confidence: float


class SkinScanResponse(BaseModel):
    scan_id:            str
    detected_condition: str
    severity:           str
    confidence:         float = Field(..., ge=0.0, le=1.0)
    recommendation:     str
    scanned_at:         datetime
    top_predictions:    Optional[List[PredictionItem]] = None
    image_url:          Optional[str] = None


class FitnessLogRequest(BaseModel):
    activity_type:   str = Field(
        ...,
        pattern=r"^(Walking|Running|Cycling|Swimming|Yoga|Gym|Other)$",
        description="Walking | Running | Cycling | Swimming | Yoga | Gym | Other",
    )
    duration_mins:   int           = Field(..., ge=1, le=1440)     # 1 min – 24 h
    calories_burned: Optional[int] = Field(None, ge=0, le=10_000)
    distance_km:     Optional[float] = Field(None, ge=0.0, le=1000.0)
    notes:           Optional[str] = Field(None, min_length=2, max_length=500)


class FitnessLogResponse(BaseModel):
    log_id:          str
    user_id:         str
    activity_type:   str
    duration_mins:   int
    calories_burned: Optional[int]
    distance_km:     Optional[float]
    notes:           Optional[str]
    logged_at:       datetime


class DietPlanResponse(BaseModel):
    goal:         str
    plan:         dict
    generated_at: datetime


class ToolStatsResponse(BaseModel):
    active_tools_count: int
    scans_done_count:   int
    ai_readiness_pct:   int


# ===========================================================================
# 1. SYMPTOM CHECKER
# ===========================================================================
@router.post("/symptom-check", response_model=SymptomCheckResponse)
async def check_symptoms(
    body: SymptomCheckRequest,
    current_user: dict = Depends(get_current_user),
):
    """
    AI Symptom Doctor — analyzes symptoms and returns possible conditions.
    Simulated response. Replace with a medical NLP model (e.g., MedPaLM, BioBERT) in production.
    """
    symptom_map = {
        "fever": [("Viral Infection", 0.75, "mild", "Rest and drink fluids"), ("Malaria", 0.30, "moderate", "Get a blood test")],
        "cough": [("Upper Respiratory Infection", 0.65, "mild", "Steam inhalation"), ("Tuberculosis", 0.15, "severe", "Consult a pulmonologist")],
        "headache": [("Tension Headache", 0.80, "mild", "Rest in a quiet room"), ("Migraine", 0.40, "moderate", "Avoid bright lights")],
        "chest pain": [("Muscle Strain", 0.50, "mild", "Apply heat pack"), ("Cardiac Issue", 0.20, "severe", "Go to ER immediately")],
        "default": [("General Illness", 0.50, "mild", "Consult a general physician")],
    }

    conditions_seen = set()
    results = []
    for symptom in body.symptoms:
        for s_key, diagnoses in symptom_map.items():
            if s_key in symptom.lower():
                for name, prob, sev, rec in diagnoses:
                    if name not in conditions_seen:
                        conditions_seen.add(name)
                        results.append(DiagnosisResult(condition=name, probability=prob, severity=sev, recommendation=rec))

    if not results:
        c, p, s, r = symptom_map["default"][0]
        results.append(DiagnosisResult(condition=c, probability=p, severity=s, recommendation=r))

    risk = "high" if any(r.severity == "severe" for r in results) else "medium" if any(r.severity == "moderate" for r in results) else "low"
    see_doctor = risk in ("medium", "high")

    check_id = str(uuid.uuid4())
    doc = {
        "check_id": check_id,
        "user_id": current_user["sub"],
        "symptoms": body.symptoms,
        "overall_risk": risk,
        "analyzed_at": datetime.now(timezone.utc),
    }
    await _db_module.db["symptom_checks"].insert_one(doc)

    return SymptomCheckResponse(
        check_id=check_id,
        possible_conditions=results,
        overall_risk=risk,
        should_see_doctor=see_doctor,
        analyzed_at=doc["analyzed_at"],
    )


# ===========================================================================
# 2. AI CHAT DOCTOR
# ===========================================================================
@router.post("/chat", response_model=ChatResponse)
async def chat_with_doctor(
    body: ChatMessage,
    current_user: dict = Depends(get_current_user),
):
    """
    24/7 AI Chat Doctor.
    Simulated. Replace with GPT-4 / MedPaLM / Gemini integration for production.
    """
    session_id = body.session_id or str(uuid.uuid4())

    response_map = {
        "headache": ("Try resting in a quiet, dark room and stay hydrated. If persistent for more than 72 hours, consult a doctor.", ["Take paracetamol 500mg", "Avoid screen time", "Drink more water"]),
        "fever": ("Monitor your temperature every 4 hours. Paracetamol can help reduce fever. Seek medical attention if above 104°F.", ["Stay hydrated", "Rest", "Take paracetamol"]),
        "cough": ("Warm water with honey can soothe cough. If cough persists over 2 weeks, get a chest X-ray.", ["Steam inhalation", "Warm fluids", "Avoid cold drinks"]),
        "default": ("I'm your AI health assistant. I can help with general health queries. For emergencies, please call 112.", ["Describe your symptoms", "Check your vitals", "Book an appointment"]),
    }

    user_msg = body.message.lower()
    reply, suggestions = response_map["default"]
    for key, (r, s) in response_map.items():
        if key in user_msg:
            reply, suggestions = r, s
            break

    await _db_module.db["chat_sessions"].insert_one({
        "session_id": session_id,
        "user_id": current_user["sub"],
        "message": body.message,
        "reply": reply,
        "timestamp": datetime.now(timezone.utc),
    })

    return ChatResponse(
        session_id=session_id,
        reply=reply,
        suggestions=suggestions,
        timestamp=datetime.now(timezone.utc),
    )


# ===========================================================================
# 3. EMERGENCY SOS
# ===========================================================================
@router.post("/sos", response_model=SOSResponse)
async def trigger_sos(
    body: SOSRequest,
    current_user: dict = Depends(get_current_user),
):
    """
    Emergency SOS — logs GPS location and triggers emergency alert.
    In production: send push notification to emergency contacts + nearest hospital.
    """
    sos_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    doc = {
        "sos_id": sos_id,
        "user_id": current_user["sub"],
        "latitude": body.latitude,
        "longitude": body.longitude,
        "emergency_type": body.emergency_type,
        "message": body.message,
        "status": "ACTIVE",
        "triggered_at": now,
    }
    await _db_module.db["sos_events"].insert_one(doc)

    return SOSResponse(
        sos_id=sos_id,
        status="ACTIVE",
        nearest_hospital="City General Hospital (2.3 km away)",
        estimated_response_mins=8,
        triggered_at=now,
    )


# ===========================================================================
# 4. NEARBY HOSPITALS
# ===========================================================================
@router.get("/nearby-hospitals", response_model=List[HospitalResult])
async def get_nearby_hospitals(
    latitude: float = 28.6139,
    longitude: float = 77.2090,
    radius_km: float = 10.0,
    current_user: dict = Depends(get_current_user),
):
    """
    Find nearby hospitals by GPS coordinates.
    Simulated response. Replace with Google Places API or similar in production.
    """
    simulated = [
        HospitalResult(hospital_id="H001", name="City General Hospital", address="123 Main Street", distance_km=2.3, phone="+91-9876543210", emergency_available=True, rating=4.5),
        HospitalResult(hospital_id="H002", name="Apollo Medical Center", address="456 Health Ave", distance_km=4.1, phone="+91-9876543211", emergency_available=True, rating=4.8),
        HospitalResult(hospital_id="H003", name="Primary Health Clinic", address="789 Care Road", distance_km=6.7, phone="+91-9876543212", emergency_available=False, rating=4.0),
    ]
    return [h for h in simulated if h.distance_km <= radius_km]


# ===========================================================================
# 5. COUGH TB ANALYZER
# ===========================================================================
@router.post("/cough-analyze", response_model=CoughAnalysisResponse)
async def analyze_cough(
    audio_file: UploadFile = File(..., description="Cough audio file (WAV/MP3)"),
    current_user: dict = Depends(get_current_user),
):
    """
    Cough TB & Respiratory Analyzer.
    Accepts cough audio and returns TB risk assessment.
    Simulated. Production: use a trained CNN/ResNet on mel-spectrograms of cough audio.
    """
    from app.core.file_safety import validate_file_safety
    await validate_file_safety(audio_file, max_size_mb=10, allow_pdf=False, allow_image=False, allow_audio=True)

    tb_prob = random.uniform(0.02, 0.25)
    risk = "high" if tb_prob > 0.20 else "medium" if tb_prob > 0.10 else "low"
    conditions = {
        "low": "Normal respiratory pattern",
        "medium": "Possible Upper Respiratory Infection",
        "high": "TB Suspected – Please consult a pulmonologist",
    }

    analysis_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    await _db_module.db["cough_analyses"].insert_one({
        "analysis_id": analysis_id,
        "user_id": current_user["sub"],
        "tb_risk": risk,
        "tb_probability": tb_prob,
        "analyzed_at": now,
    })

    return CoughAnalysisResponse(
        analysis_id=analysis_id,
        tb_risk=risk,
        tb_probability=round(tb_prob, 4),
        respiratory_condition=conditions[risk],
        recommendation="Consult a doctor for confirmation." if risk != "low" else "No action needed. Monitor symptoms.",
        confidence=round(random.uniform(0.78, 0.95), 4),
        analyzed_at=now,
    )


# ===========================================================================
# 6. SKIN SCANNER
# ===========================================================================
@router.post("/skin-scan", response_model=SkinScanResponse)
async def scan_skin(
    image: UploadFile = File(..., description="Skin area image (JPG/PNG)"),
    body_area: Optional[str] = Form(default="unknown", description="e.g. 'arm', 'face', 'leg'"),
    current_user: dict = Depends(get_current_user),
):
    """
    Skin Condition Scanner.
    Uses ML logic if available; falls back to simulated data if model is not loaded.
    """
    from app.core.file_safety import validate_file_safety
    contents = await validate_file_safety(image, max_size_mb=10, allow_pdf=False, allow_image=True, allow_audio=False)

    try:
        from app.services.skin_scanner import predict_skin_disease
        prediction, error = predict_skin_disease(contents)
    except Exception as e:
        prediction = None
        error = str(e)

    if prediction:
        detected = prediction["condition"]
        severity = prediction["severity"]
        confidence = prediction["confidence"]
        top_preds = prediction.get("top_3", [])
        recommendation = f"AI Detected {detected}. Please consult a dermatologist."
    else:
        import logging
        logging.getLogger(__name__).warning("Skin scan ML fallback triggered: %s", error)
        conditions = [
            ("Normal Skin", "none", "No condition detected. Skin looks healthy."),
            ("Acne", "mild", "Use gentle cleanser and avoid squeezing. Consult dermatologist if severe."),
            ("Eczema", "moderate", "Apply moisturizer and avoid irritants. Consult dermatologist."),
            ("Psoriasis", "moderate", "Use prescribed topical cream. Avoid triggers like stress."),
        ]
        detected, severity, recommendation = random.choice(conditions)
        confidence = round(random.uniform(0.75, 0.95), 4)
        top_preds = [{"label": detected, "confidence": confidence}]

    # Upload to Cloudinary
    from app.core.storage import upload_image_cloudinary
    image_url = await upload_image_cloudinary(contents, f"skin_scan_{current_user['sub']}.jpg")

    scan_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    await _db_module.db["skin_scans"].insert_one({
        "scan_id": scan_id,
        "user_id": current_user["sub"],
        "detected_condition": detected,
        "confidence": confidence,
        "severity": severity,
        "body_area": body_area,
        "scanned_at": now,
        "top_predictions": top_preds,
        "image_url": image_url,
        "is_ai_prediction": bool(prediction)
    })

    return SkinScanResponse(
        scan_id=scan_id,
        detected_condition=detected,
        severity=severity,
        confidence=confidence,
        recommendation=recommendation,
        scanned_at=now,
        top_predictions=top_preds,
        image_url=image_url
    )


# ---------------------------------------------------------------------------
# 6b. RECORD SKIN SCAN (Sync from device)
# ---------------------------------------------------------------------------
@router.post("/record-skin-scan", response_model=SkinScanResponse)
async def record_skin_scan(
    image: UploadFile = File(..., description="Skin area image"),
    detected_condition: str = Form(...),
    confidence: float = Form(...),
    severity: str = Form(default="unknown"),
    top_predictions_json: str = Form(default="[]"),
    body_area: Optional[str] = Form(default="unknown"),
    current_user: dict = Depends(get_current_user),
):
    """
    Record a skin scan result that was already analyzed on the device.
    Useful for syncing TFLite results to the backend.
    """
    from app.core.file_safety import validate_file_safety
    from app.core.storage import upload_image_cloudinary

    contents = await validate_file_safety(image, max_size_mb=10, allow_image=True)
    image_url = await upload_image_cloudinary(contents, f"skin_scan_sync_{current_user['sub']}.jpg")

    try:
        top_preds = json.loads(top_predictions_json)
    except:
        top_preds = []

    scan_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    recommendation = f"You recorded {detected_condition} with {round(confidence*100, 1)}% confidence. Please consult a dermatologist for confirmation."

    await _db_module.db["skin_scans"].insert_one({
        "scan_id": scan_id,
        "user_id": current_user["sub"],
        "detected_condition": detected_condition,
        "confidence": confidence,
        "severity": severity,
        "body_area": body_area,
        "scanned_at": now,
        "top_predictions": top_preds,
        "image_url": image_url,
        "is_ai_prediction": True,
        "analysis_source": "on-device"
    })

    return SkinScanResponse(
        scan_id=scan_id,
        detected_condition=detected_condition,
        severity=severity,
        confidence=confidence,
        recommendation=recommendation,
        scanned_at=now,
        top_predictions=top_preds,
        image_url=image_url
    )


# ---------------------------------------------------------------------------
# 6c. GET SKIN SCAN HISTORY
# ---------------------------------------------------------------------------
@router.get("/skin-scans", response_model=List[SkinScanResponse])
async def get_skin_scans(
    limit: int = 20,
    current_user: dict = Depends(get_current_user),
):
    """Get history of skin scans for the current user."""
    cursor = _db_module.db["skin_scans"].find({"user_id": current_user["sub"]}).sort("scanned_at", -1).limit(limit)
    scans = []
    async for doc in cursor:
        scans.append(SkinScanResponse(
            scan_id=doc["scan_id"],
            detected_condition=doc["detected_condition"],
            severity=doc.get("severity", "unknown"),
            confidence=doc.get("confidence", 0.0),
            recommendation=f"Analysis from {doc['scanned_at'].strftime('%Y-%m-%d')}",
            scanned_at=doc["scanned_at"],
            top_predictions=doc.get("top_predictions"),
            image_url=doc.get("image_url")
        ))
    return scans


# ===========================================================================
# 7. DIET PLANNER
# ===========================================================================
@router.get("/diet-plan", response_model=DietPlanResponse)
async def get_diet_plan(
    goal: str = "balanced",
    current_user: dict = Depends(get_current_user),
):
    """
    Personalized meal plan based on health goal. Saves results to database.
    Production: replace `plans` dict with real nutrition API.
    """
    plans = {
        "weight_loss": {
            "breakfast": "Oats with berries + Green tea",
            "lunch": "Grilled chicken salad + Lemonade",
            "dinner": "Steamed fish + Vegetables",
            "snacks": "Almonds, Apple",
            "daily_calories": 1500,
        },
        "muscle_gain": {
            "breakfast": "6 Egg whites + Whole wheat toast + Banana",
            "lunch": "Brown rice + Grilled chicken + Dal",
            "dinner": "Paneer curry + Chapati + Salad",
            "snacks": "Protein shake, Peanut butter toast",
            "daily_calories": 2800,
        },
        "diabetic": {
            "breakfast": "Vegetable upma + Herbal tea (no sugar)",
            "lunch": "Brown rice (small) + Dal + Green vegetables",
            "dinner": "Chapati + Sabzi + Curd",
            "snacks": "Nuts, Cucumber",
            "daily_calories": 1800,
        },
        "balanced": {
            "breakfast": "Poha + Buttermilk",
            "lunch": "Dal rice + Mixed veg curry",
            "dinner": "Chapati + Paneer + Salad",
            "snacks": "Fruits, Roasted chana",
            "daily_calories": 2000,
        },
    }
    plan_data = plans.get(goal, plans["balanced"])
    now = datetime.now(timezone.utc)

    await _db_module.db["diet_plans"].insert_one({
        "user_id": current_user["sub"],
        "goal": goal,
        "plan": plan_data,
        "generated_at": now,
    })

    return DietPlanResponse(goal=goal, plan=plan_data, generated_at=now)


@router.get("/diet-history", response_model=List[DietPlanResponse])
async def get_diet_history(
    current_user: dict = Depends(get_current_user),
):
    """Retrieve user's diet plan history."""
    cursor = _db_module.db["diet_plans"].find({"user_id": current_user["sub"]}).sort("generated_at", -1)
    results = []
    async for doc in cursor:
        results.append(DietPlanResponse(goal=doc["goal"], plan=doc["plan"], generated_at=doc["generated_at"]))
    return results


# ===========================================================================
# 8. FITNESS TRACKER
# ===========================================================================
@router.post("/fitness/log", response_model=FitnessLogResponse, status_code=status.HTTP_201_CREATED)
async def log_fitness(
    body: FitnessLogRequest,
    current_user: dict = Depends(get_current_user),
):
    """Log a workout session."""
    log_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)

    calories = body.calories_burned
    if calories is None:
        cal_map = {"Walking": 4, "Running": 8, "Yoga": 3, "Cycling": 6}
        rate = cal_map.get(body.activity_type, 5)
        calories = body.duration_mins * rate

    doc = {
        "log_id": log_id,
        "user_id": current_user["sub"],
        "activity_type": body.activity_type,
        "duration_mins": body.duration_mins,
        "calories_burned": calories,
        "distance_km": body.distance_km,
        "notes": body.notes,
        "logged_at": now,
    }
    await _db_module.db["fitness_logs"].insert_one(doc)
    return FitnessLogResponse(**{k: v for k, v in doc.items() if k != "_id"})


@router.get("/fitness/history", response_model=List[FitnessLogResponse])
async def get_fitness_history(
    limit: int = 20,
    current_user: dict = Depends(get_current_user),
):
    """Get recent fitness workout history."""
    cursor = _db_module.db["fitness_logs"].find({"user_id": current_user["sub"]}).sort("logged_at", -1).limit(limit)
    results = []
    async for doc in cursor:
        results.append(FitnessLogResponse(**{k: v for k, v in doc.items() if k != "_id"}))
    return results


# ===========================================================================
# 9. TOOLS DASHBOARD STATS
# ===========================================================================
@router.get("/stats", response_model=ToolStatsResponse)
async def get_tool_stats(
    current_user: dict = Depends(get_current_user),
):
    """Get statistics for the Smart Tools Zone dashboard."""
    uid = current_user["sub"]

    skin_count = await _db_module.db["skin_scans"].count_documents({"user_id": uid})
    cough_count = await _db_module.db["cough_analyses"].count_documents({"user_id": uid})

    active_tools = set()
    collections_to_check = {
        "symptom_checks",
        "chat_sessions",
        "sos_events",
        "skin_scans",
        "cough_analyses",
        "diet_plans",
        "fitness_logs",
    }

    for col_name in collections_to_check:
        count = await _db_module.db[col_name].count_documents({"user_id": uid})
        if count > 0:
            active_tools.add(col_name)

    rem_count = await _db_module.db["medicine_reminders"].count_documents({"user_id": uid})
    if rem_count > 0:
        active_tools.add("medicine_reminders")

    fs = _db_module.get_fs_bucket()
    vault_cursor = fs.find({"metadata.uploaded_by": uid}).limit(1)
    has_vault = False
    async for _ in vault_cursor:
        has_vault = True
        break
    if has_vault:
        active_tools.add("vault")

    return ToolStatsResponse(
        active_tools_count=len(active_tools),
        scans_done_count=(skin_count + cough_count),
        ai_readiness_pct=100 if len(active_tools) >= 3 else 50 if len(active_tools) > 0 else 0
    )


# ===========================================================================
# 11. DIRECT SCAN ANALYZE (/scans/analyze)
# ===========================================================================
@root_router.post("/scans/analyze", response_model=ScanResultDto)
@router.post("/scans/analyze", response_model=ScanResultDto)
async def analyze_scan(
    image: UploadFile = File(...),
    type: str = Form("skin"),
    current_user: dict = Depends(get_current_user),
):
    """
    Direct route for /scans/analyze called by Android app.
    Supports scan types: skin, cough, face, vitals, general.
    """
    from app.core.file_safety import validate_file_safety
    await validate_file_safety(image, max_size_mb=15, allow_pdf=False, allow_image=True, allow_audio=True)

    scan_id = str(uuid.uuid4())
    now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)

    if type == "skin":
        disease_name = "Acne Vulgaris / Mild Dermatitis"
        confidence = 0.92
        severity = "LOW"
        advice = "Keep the affected area clean and dry. Apply gentle moisturizer."
    elif type in ("cough", "respiratory", "tb"):
        disease_name = "Mild Upper Respiratory Tract Irritation"
        confidence = 0.88
        severity = "LOW"
        advice = "Drink warm fluids and steam inhale twice daily."
    else:
        disease_name = "Healthy / No Anomalies Detected"
        confidence = 0.95
        severity = "NORMAL"
        advice = "No immediate medical concerns detected. Maintain regular health habits."

    prediction = DiseasePredictionDto(
        disease_name=disease_name,
        confidence=confidence,
        severity_level=severity,
        medical_advice=advice,
    )

    return ScanResultDto(
        scanId=scan_id,
        timestamp=now_ms,
        prediction=prediction,
    )


# ===========================================================================
# 12. DIRECT AI DOCTOR CHAT (/chat/ask)
# ===========================================================================
@root_router.post("/chat/ask")
@router.post("/chat/ask")
async def ask_ai_doctor_direct(
    body: dict,
    current_user: dict = Depends(get_current_user),
):
    """Direct route for /chat/ask called by AiChatViewModel.kt."""
    prompt = body.get("prompt") or body.get("message") or ""
    session_id = str(uuid.uuid4())
    msg_obj = ChatMessage(message=prompt, session_id=session_id)
    res = await chat_with_doctor(msg_obj, current_user)
    return {"reply": res.reply, "session_id": res.session_id}

