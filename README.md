# Helpix backend: direct run

The backend runs directly with Python; Docker is not required.

## Local setup

```powershell
cd helpix_backend
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
Copy-Item .env.production.example .env
# Fill the values in .env. For local development set APP_ENV=development.
.\.venv\Scripts\python.exe run.py
```

The local server listens on `http://127.0.0.1:8000` by default. Set `HOST` and `PORT` only when needed.

## Direct production run

1. Copy `.env.production.example` to `.env` and replace every placeholder with a managed secret.
2. Use a TLS-terminating reverse proxy or hosting platform in front of the app; do not expose plaintext HTTP publicly.
3. Set `HOST=127.0.0.1`, `PORT=8000`, and `WEB_CONCURRENCY` to the number of workers appropriate for the server.
4. Start it with `.\.venv\Scripts\python.exe run.py` on Windows, or `python run.py` on Linux.

MongoDB must be reachable over TLS and backed up independently. Redis/Celery are optional; the app falls back to synchronous background work when they are not configured.
