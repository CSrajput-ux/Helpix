"""
app/main.py
-----------
Helpix AI – FastAPI application entry point.

Startup sequence:
  1. Logging is configured before anything else.
  2. Settings are validated (crashes fast if secrets are missing in production).
  3. MongoDB is connected and indexes are created.
  4. All routers are registered with rate-limiting dependencies.
"""

import logging
import logging.config
import os
import uuid
from contextlib import asynccontextmanager

import cloudinary
import cloudinary.uploader
import cloudinary.api

from fastapi import FastAPI, Depends, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles
from starlette.exceptions import HTTPException as StarletteHTTPException

from app.core.config import settings
from app.core.db import connect_db, close_db, is_db_healthy
from app.core.rate_limit import public_rate_limit, user_rate_limit
from app.api.common import auth, appointments, tools, notifications
from app.api.patient import vitals, prescription, vault, health_score, medicine_reminder
from app.api.doctor import management as doctor_management
from app.api.doctor import wallet as doctor_wallet


# ── Logging setup ─────────────────────────────────────────────────────────────

logging.config.dictConfig({
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "default": {
            "format": "%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
            "datefmt": "%Y-%m-%d %H:%M:%S",
        },
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "formatter": "default",
        },
    },
    "root": {
        "level": "DEBUG" if settings.APP_ENV == "development" else "INFO",
        "handlers": ["console"],
    },
})

logger = logging.getLogger(__name__)


# ── Lifespan ──────────────────────────────────────────────────────────────────

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("═══════════════════════════════════════")
    logger.info("  Helpix AI Backend v%s starting up   ", settings.APP_VERSION)
    logger.info("  Environment : %s", settings.APP_ENV)
    logger.info("═══════════════════════════════════════")

    # FIX #4: Ensure upload directories exist at startup
    os.makedirs(os.path.join(settings.UPLOAD_DIR, "profiles"), exist_ok=True)
    os.makedirs(os.path.join(settings.UPLOAD_DIR, "prescriptions"), exist_ok=True)

    # Configure Cloudinary
    if settings.CLOUDINARY_CLOUD_NAME and settings.CLOUDINARY_API_KEY and settings.CLOUDINARY_API_SECRET:
        cloudinary.config(
            cloud_name=settings.CLOUDINARY_CLOUD_NAME,
            api_key=settings.CLOUDINARY_API_KEY,
            api_secret=settings.CLOUDINARY_API_SECRET,
            secure=True
        )
        logger.info("Cloudinary configured successfully.")
    else:
        logger.warning("Cloudinary credentials are missing. Image uploads to Cloudinary might fail.")

    await connect_db()
    yield
    await close_db()
    logger.info("Helpix AI Backend shut down cleanly.")


# ── Application ───────────────────────────────────────────────────────────────

app = FastAPI(
    title="Helpix AI – Healthcare Backend",
    description=(
        "Secure, scalable FastAPI backend for the Helpix AI healthcare app.\n\n"
        "**Stack**: FastAPI · MongoDB (Motor) · JWT · AES-Fernet · GridFS\n\n"
        "**Features**: Auth · Vitals Sync · Prescription AI · Health Vault · "
        "Doctor–Patient Linking · Appointments · Health Score · "
        "Medicine Reminders · Symptom AI · SOS · Skin Scanner · "
        "Cough TB Analyzer · Diet Planner · Fitness Tracker"
    ),
    version=settings.APP_VERSION,
    contact={"name": "Helpix AI Team", "url": "https://helpix.ai"},
    license_info={"name": "MIT"},
    lifespan=lifespan,
    docs_url="/docs" if settings.APP_ENV == "development" else None,
    redoc_url="/redoc" if settings.APP_ENV == "development" else None,
)

# FIX #4: Serve uploaded files (profile images, etc.) at /static/...
# Creates the directory first to avoid mount errors on fresh installs
os.makedirs(settings.UPLOAD_DIR, exist_ok=True)
app.mount("/static", StaticFiles(directory=settings.UPLOAD_DIR), name="static")


# ── Middleware ────────────────────────────────────────────────────────────────

# Request ID middleware — adds X-Request-ID to every response for tracing
@app.middleware("http")
async def add_request_id(request: Request, call_next):
    request_id = request.headers.get("X-Request-ID", str(uuid.uuid4()))
    response = await call_next(request)
    response.headers["X-Request-ID"] = request_id
    return response
# Security headers on every response
@app.middleware("http")
async def add_security_headers(request: Request, call_next):
    response = await call_next(request)
    response.headers["X-Content-Type-Options"] = "nosniff"
    response.headers["X-Frame-Options"]        = "DENY"
    response.headers["X-XSS-Protection"]       = "1; mode=block"
    response.headers["Referrer-Policy"]        = "strict-origin-when-cross-origin"
    return response

# CORS — uses ALLOWED_ORIGINS from .env (set "*" in dev, explicit domains in prod)
origins = [o.strip() for o in settings.ALLOWED_ORIGINS.split(",")]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Error handlers ───────────────────────────────────────────────────────────

@app.exception_handler(StarletteHTTPException)
async def http_exception_handler(request: Request, exc: StarletteHTTPException):
    """Pass-through for intentional HTTPExceptions; never leak internal detail."""
    return JSONResponse(
        status_code=exc.status_code,
        content={"detail": exc.detail},
        headers=getattr(exc, "headers", None) or {},
    )


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    """
    Return structured 422 Unprocessable Entity without stack traces.
    Logs are written server-side for debugging.
    """
    logger.debug("Validation error on %s %s: %s", request.method, request.url.path, exc.errors())
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "detail": "Request validation failed.",
            "errors": [
                {
                    "field": " → ".join(str(loc) for loc in err["loc"]),
                    "message": err["msg"],
                }
                for err in exc.errors()
            ],
        },
    )


@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """Catch-all: log the full traceback server-side, return a generic 500."""
    logger.exception("Unhandled error on %s %s", request.method, request.url.path)
    return JSONResponse(
        status_code=500,
        content={"detail": "An unexpected error occurred. Please try again later."},
    )


# ── Routers ───────────────────────────────────────────────────────────────────

app.include_router(auth.router)

_user_rl = {"dependencies": [Depends(user_rate_limit)]}

app.include_router(vitals.router,              **_user_rl)
app.include_router(prescription.router,        **_user_rl)
app.include_router(vault.router,               **_user_rl)
app.include_router(appointments.router,        **_user_rl)
app.include_router(notifications.router,       **_user_rl)
app.include_router(health_score.router,        **_user_rl)
app.include_router(medicine_reminder.router,   **_user_rl)
app.include_router(tools.router,               **_user_rl)
app.include_router(tools.root_router,          **_user_rl)
app.include_router(doctor_management.router,   **_user_rl)
app.include_router(doctor_wallet.router,       **_user_rl)


# ── Health endpoints ──────────────────────────────────────────────────────────

@app.get("/", tags=["Health"], dependencies=[Depends(public_rate_limit)])
async def root():
    """API liveness check."""
    return {
        "status": "online",
        "service": "Helpix AI Backend",
        "version": settings.APP_VERSION,
    }


@app.get("/health", tags=["Health"], dependencies=[Depends(public_rate_limit)])
async def health():
    """Detailed health check for load balancers / uptime monitors."""
    if not await is_db_healthy():
        return JSONResponse(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            content={"status": "unhealthy", "database": "unavailable"},
        )
    return {"status": "healthy", "database": "connected", "version": settings.APP_VERSION}
