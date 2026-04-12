"""
app/main.py is the entrypoint but uvicorn needs to find it.
This file lets you run: python run.py
"""
import uvicorn

if __name__ == "__main__":
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,       # Auto-reload on code changes during development
        log_level="info",
    )
