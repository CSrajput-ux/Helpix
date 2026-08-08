"""Direct backend entry point: run with ``python run.py``."""

import os

import uvicorn

from app.core.config import settings


if __name__ == "__main__":
    is_development = settings.APP_ENV == "development"
    uvicorn.run(
        "app.main:app",
        host=os.getenv("HOST", "0.0.0.0"),
        port=int(os.getenv("PORT", "8000")),
        reload=is_development,
        workers=1 if is_development else int(os.getenv("WEB_CONCURRENCY", "1")),
        log_level="debug" if is_development else "info",
    )
