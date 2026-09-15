"""Keep every Retrofit endpoint mapped to an actual FastAPI route."""

import re
from pathlib import Path

from app.main import app


def test_every_android_retrofit_endpoint_exists_in_backend():
    api_file = (
        Path(__file__).resolve().parents[2]
        / "Helpix"
        / "app"
        / "src"
        / "main"
        / "java"
        / "com"
        / "healthai"
        / "app"
        / "data"
        / "remote"
        / "api"
        / "HelpixApi.kt"
    )
    retrofit_calls = {
        (method, f"/{path}")
        for method, path in re.findall(r'@(GET|POST|PUT|PATCH|DELETE)\("([^\"]+)"\)', api_file.read_text(encoding="utf-8"))
    }
    backend_calls = {
        (method, route.path)
        for route in app.routes
        for method in getattr(route, "methods", set()) or set()
        if method in {"GET", "POST", "PUT", "PATCH", "DELETE"}
    }

    assert not (retrofit_calls - backend_calls), (
        "Android endpoint(s) missing from FastAPI: "
        + ", ".join(f"{method} {path}" for method, path in sorted(retrofit_calls - backend_calls))
    )
