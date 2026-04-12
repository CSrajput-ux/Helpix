"""
app/main.py
-----------
FastAPI application entry point.

Features:
  - CORS configured for Android app connectivity
  - Startup / Shutdown events for MongoDB lifecycle
  - Swagger UI auto-docs at /docs
  - ReDoc at /redoc
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.db import connect_db, close_db
from app.api.common import auth, appointments, tools, notifications
from app.api.patient import vitals, prescription, vault, health_score, medicine_reminder
from app.api.doctor import management as doctor_management


# ---------------------------------------------------------------------------
# Lifespan context (replaces deprecated @app.on_event)
# ---------------------------------------------------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    await connect_db()
    yield
    # Shutdown
    await close_db()


# ---------------------------------------------------------------------------
# FastAPI Application
# ---------------------------------------------------------------------------
app = FastAPI(
    title="Helpix AI – Healthcare Backend",
    description=(
        "A secure, scalable FastAPI backend for the Helpix AI healthcare Android app.\n\n"
        "**Features**: JWT Auth, AES Encryption, Smartwatch Vitals Sync, "
        "Prescription AI (LayoutLMv3), Health Vault (GridFS), Doctor-Patient Linking, "
        "Appointments, Health Score, Medicine Reminders, Symptom AI, SOS, Skin Scanner, "
        "Cough TB Analyzer, Diet Planner, Fitness Tracker."
    ),
    version="2.0.0",
    contact={
        "name": "Helpix AI Team",
        "url": "https://helpix.ai",
    },
    license_info={
        "name": "MIT",
    },
    lifespan=lifespan,
)


# ---------------------------------------------------------------------------
# CORS Middleware
# IMPORTANT: Required for Android to connect to this server.
# ---------------------------------------------------------------------------
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ---------------------------------------------------------------------------
# Include Routers
# ---------------------------------------------------------------------------
# Auth & Core (Common/Patient/Doctor)
app.include_router(auth.router)
app.include_router(vitals.router)
app.include_router(prescription.router)
app.include_router(vault.router)
app.include_router(doctor_management.router)

# Common & Features
app.include_router(appointments.router)
app.include_router(notifications.router)
app.include_router(health_score.router)
app.include_router(medicine_reminder.router)
app.include_router(tools.router)


# ---------------------------------------------------------------------------
# Root health check
# ---------------------------------------------------------------------------
@app.get("/", tags=["Health Check"])
async def root():
    """Health check endpoint."""
    return {
        "status": "online",
        "service": "Helpix AI Backend",
        "version": "2.0.0",
        "docs": "/docs",
        "total_endpoints": len([r for r in app.routes if hasattr(r, "methods")]),
    }


@app.get("/health", tags=["Health Check"])
async def health():
    """Detailed health status."""
    return {"status": "healthy", "database": "connected"}
