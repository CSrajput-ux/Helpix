# ⚙️ Helpix AI — FastAPI Backend

> **Secure, scalable REST API for the Helpix AI healthcare platform.**
> Part of the [Helpix AI Platform](../README.md).

[![FastAPI](https://img.shields.io/badge/FastAPI-0.135-009688?style=flat-square&logo=fastapi)](https://fastapi.tiangolo.com)
[![Python](https://img.shields.io/badge/Python-3.11+-3776AB?style=flat-square&logo=python)](https://python.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-JSONB-4169E1?style=flat-square&logo=postgresql)](https://postgresql.org)
[![Redis](https://img.shields.io/badge/Redis-8.1-DC382D?style=flat-square&logo=redis)](https://redis.io)
[![Pydantic](https://img.shields.io/badge/Pydantic-v2-E92063?style=flat-square)](https://docs.pydantic.dev)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](../LICENSE)

---

## 🌟 Overview

The Helpix backend is a production-grade **async FastAPI** service that powers the Helpix AI Android app.
It handles authentication, patient/doctor management, AI diagnostics, file storage, rate limiting, and more.

**Stack:** FastAPI · PostgreSQL (async SQLAlchemy + JSONB) · Redis · JWT · AES-Fernet · Cloudinary · Supabase · Celery · scikit-learn · Google Gemini

---

## 📁 Structure

```
helpix_backend/
├── app/
│   ├── main.py                   # FastAPI app factory + router wiring
│   ├── api/
│   │   ├── common/
│   │   │   ├── auth.py           # POST /auth/signup|login|google|refresh...
│   │   │   ├── appointments.py   # Appointment booking & management
│   │   │   ├── notifications.py  # Push notification management
│   │   │   └── tools.py          # All AI tools (symptom, chat, SOS, skin...)
│   │   ├── patient/
│   │   │   ├── vitals.py         # Vitals sync + history
│   │   │   ├── prescription.py   # AI prescription analysis
│   │   │   ├── vault.py          # Health vault (upload/list/download/delete)
│   │   │   ├── health_score.py   # Personalised health score calculation
│   │   │   └── medicine_reminder.py
│   │   └── doctor/
│   │       ├── management.py     # Doctor profile & patient management
│   │       └── wallet.py         # Earnings, transactions, withdrawals
│   ├── core/
│   │   ├── config.py             # Pydantic BaseSettings (loaded from .env)
│   │   ├── db.py                 # PostgreSQL async connection + JSONB document store
│   │   ├── security.py           # JWT create/verify + bcrypt + AES-256 Fernet
│   │   ├── rate_limit.py         # 3-tier Redis-based rate limiting
│   │   ├── file_safety.py        # File validation, MIME check, sanitisation
│   │   └── storage.py            # Cloudinary + Supabase upload/delete wrappers
│   ├── models/
│   │   └── schemas.py            # All Pydantic v2 request/response models
│   └── services/
│       ├── symptom_ml.py         # Decision Tree ML service (132 symptoms, 41 diseases)
│       └── skin_scanner.py       # Skin lesion HuggingFace inference client
├── tests/
│   ├── conftest.py               # pytest fixtures (in-memory SQLite, test app)
│   ├── test_api_integration.py   # Full API route integration tests
│   ├── test_android_api_contract.py # Android client contract tests
│   ├── test_security.py          # Auth & security tests
│   └── test_skin_scanner.py      # ML service tests
├── requirements.txt
├── run.py                        # Dev runner
└── .env.example                  # Template for environment variables
```

---

## 🚀 Quick Start

```bash
# 1. Virtual environment
python -m venv .venv
.venv\Scripts\activate          # Windows
source .venv/bin/activate        # macOS/Linux

# 2. Dependencies
pip install -r requirements.txt

# 3. Environment
cp .env.example .env
# Edit .env — set DATABASE_URL, JWT_SECRET_KEY, ENCRYPTION_KEY, etc.

# 4. Start PostgreSQL + Redis
docker run -d --name helpix-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=helpix_ai -p 5432:5432 postgres:16
docker run -d -p 6379:6379 redis:7

# 5. Run (development)
uvicorn app.main:app --reload --port 8000

# 5. Run (production)
uvicorn app.main:app --host 0.0.0.0 --port 8000 --workers 4
```

> 📚 Interactive Swagger docs: **http://localhost:8000/docs** (development only)
> 📚 ReDoc: **http://localhost:8000/redoc** (development only)

---

## 🔐 Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `APP_ENV` | `development` or `production` | ✅ |
| `DATABASE_URL` | PostgreSQL connection string (`postgresql+asyncpg://...`) | ✅ |
| `JWT_SECRET_KEY` | HS256 signing secret (32+ bytes hex) | ✅ |
| `ENCRYPTION_KEY` | AES Fernet key (base64-encoded 32 bytes) | ✅ |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | For Google login |
| `GEMINI_API_KEY` | Google AI Gemini API key | For AI chat + symptom explanations |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name | For image upload |
| `CLOUDINARY_API_KEY` | Cloudinary API key | For image upload |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret | For image upload |
| `SUPABASE_URL` | Supabase project URL | For PDF storage |
| `SUPABASE_KEY` | Supabase anon key | For PDF storage |
| `SUPABASE_BUCKET` | Supabase storage bucket name | For PDF storage |
| `ALLOWED_ORIGINS` | Comma-separated CORS origins | ✅ |

**Generating secrets:**
```bash
# JWT secret
python -c "import secrets; print(secrets.token_hex(32))"

# Fernet encryption key
python -c "from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())"
```

---

## 📡 API Overview

| Group | Prefix | Endpoints |
|-------|--------|-----------|
| Auth | `/auth` | signup, login, google OAuth, profile CRUD, password reset, token refresh |
| Vitals | `/vitals` | sync, history, latest reading |
| Health Vault | `/vault` | upload (PDF/image), list, stream download, delete |
| Prescription | `/prescription` | AI analysis |
| Health Score | `/health-score` | personalised score |
| Medicine | `/medicine-reminders` | CRUD for reminders |
| AI Tools | `/tools` | symptom check, chat, SOS, hospitals, cough, skin scan, diet, fitness |
| Appointments | `/appointments` | book, list, status update |
| Notifications | `/notifications` | list, mark read |
| Doctor | `/doctors`, `/doctor/wallet` | doctor list/profile, earnings, withdrawals |
| Health | `/`, `/health` | liveness + readiness probes |

> Full endpoint reference with request/response schemas: see [main README](../README.md#-api-reference) or `/docs`.

---

## 🔒 Security Architecture

### 3-Tier Rate Limiting (Redis)

```
AUTH tier (strictest)
  Per-IP:      20 requests / 5 min
  Per-account: 5 attempts  / 5 min
  Backoff:     30s → 60s → ... → 1 hour (exponential)

PUBLIC tier (unauthenticated endpoints)
  Per-IP:      60 requests / 1 min  (sliding window, no lockout)

USER tier (all JWT-protected routes)
  Per-IP:      300 requests / 1 min
  Per-user-ID: 120 requests / 1 min
  Backoff:     5s → 10s → ... → 5 min
```

### Other Security Layers
- **JWT** — HS256, 7-day expiry, `type` claim for password reset tokens
- **bcrypt** — via passlib, secure cost factor
- **AES-256 Fernet** — symmetric encryption for medical records
- **Security headers** — `X-Content-Type-Options`, `X-Frame-Options`, `X-XSS-Protection`, `Referrer-Policy` on every response
- **Request ID** — `X-Request-ID` tracing header on every response
- **File safety** — MIME type + magic byte verification, filename sanitisation, 10–20 MB size limits
- **Error isolation** — no stack traces in API responses; all errors logged server-side
- **Production startup guard** — refuses to boot if `JWT_SECRET_KEY` or `ENCRYPTION_KEY` are still `CHANGE_ME`

---

## 🤖 AI Services

### Symptom ML (`services/symptom_ml.py`)
- **Model**: Decision Tree Classifier
- **Training data**: 4,920 patient records × 132 symptom features → 41 disease classes
- **Output**: predicted condition, confidence score, triage level (LOW/MEDIUM/HIGH/EMERGENCY), recommended specialist
- **Enhancement**: Google Gemini 2.5 Flash generates a plain-language clinical explanation alongside the ML result

### Skin Lesion Scanner (`services/skin_scanner.py`)
- **Primary**: HuggingFace Space inference API (cloud)
- **Fallback**: Configurable via `SKIN_INFERENCE_URL` env var
- **Timeout**: 55 seconds (configurable)
- **Output**: top-3 predictions with confidence scores, severity, recommendation

---

## 🗃 PostgreSQL Storage

Application records are stored in the `app_documents` table using PostgreSQL
JSONB, preserving the existing collection/document shapes while moving all
runtime persistence off MongoDB. PostgreSQL adds unique indexes for user emails,
user IDs, and doctor-patient links. File bytes remain in Cloudinary/Supabase;
Redis remains a separate cache/service.

### Transfer existing MongoDB data

Back up both databases first and pause API writes during the final transfer.
The importer is safe to rerun: matching collection/document IDs are updated,
and source records are not deleted.

```bash
pip install -r requirements-migration.txt
# Set MONGO_SOURCE_URL (or use existing MONGO_URL), optional MONGO_SOURCE_DB,
# and DATABASE_URL
python -m scripts.migrate_mongodb_to_postgres
```

The migration-only `pymongo` dependency is not installed by the production
requirements. After verifying the PostgreSQL data and application, update the
deployment's `DATABASE_URL` to the PostgreSQL connection string.

### Deploy on Render

1. Push this repository to GitHub and create a Render Blueprint from
   `render.yaml`.
2. Set the Blueprint's `DATABASE_URL` to the Neon connection string, plus
   `JWT_SECRET_KEY`, `ENCRYPTION_KEY`, and `ALLOWED_ORIGINS` (the exact
   comma-separated HTTPS origins allowed to call the API).
3. Set Cloudinary and Supabase credentials for image/document uploads. Add
   `GOOGLE_CLIENT_ID` and `GEMINI_API_KEY` if those integrations are enabled.
4. Deploy and wait for the `/health` check to pass. The app creates its
   PostgreSQL schema during startup.

Generate production secrets with the commands above. Keep the MongoDB source
URL out of Render; it is only needed for the one-time transfer. Never commit
`.env` or paste database URLs into chat.

### Stored collections

| Collection | Purpose |
|------------|---------|
| `users` | User accounts (patient + doctor) |
| `vitals_ts` | Vitals readings per user |
| `appointments` | Doctor-patient appointments |
| `prescriptions` | Prescription scan records |
| `vault_files` | Health vault file metadata |
| `doctor_links` | Doctor-patient relationships |
| `medical_records` | Patient medical records |
| `health_scores` | Calculated health scores |
| `medicine_reminders` | Medicine reminder schedules |
| `dose_logs` | Medicine dose history |
| `symptom_checks` | ML diagnosis history |
| `skin_scans` | Skin scan results |
| `cough_analyses` | Cough TB analysis records |
| `chat_sessions` | AI chat conversation history |
| `sos_events` | Emergency SOS log |
| `diet_plans` | Generated diet plans per user |
| `fitness_logs` | Workout session logs |
| `notifications` | User notification inbox |
| `transactions` | Doctor earnings + withdrawals |
| `rate_limit_logs`, `rate_limit_lockouts` | Rate limit windows and lockouts |

---

## 🧪 Testing

```bash
# Run all tests
pytest tests/ -v

# With coverage report
pytest tests/ --cov=app --cov-report=html
open htmlcov/index.html

# Specific test file
pytest tests/test_api_integration.py -v
pytest tests/test_security.py -v

# Check Python syntax (no runtime)
python -m py_compile app/main.py
python -m compileall app/
```

---

## 🐳 Docker

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8000
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--workers", "4"]
```

```bash
# Build
docker build -t helpix-backend .

# Run
docker run -d \
  -p 8000:8000 \
  --env-file .env \
  --name helpix-api \
  helpix-backend

# Health check
curl http://localhost:8000/health
```

---

## 📄 License

**MIT License** — see [LICENSE](../LICENSE) for details.

---

> *Helpix AI Backend — Powering intelligent healthcare* 🏥
