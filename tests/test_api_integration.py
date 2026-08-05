"""
tests/test_api_integration.py
-------------------------------
Automated integration test suite verifying HELPix Enterprise REST APIs.
Tests user authentication, unified profile, vitals logging, AI chat, and health checks.
"""

import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

@pytest.mark.asyncio
async def test_health_endpoint():
    """Verify that the /health monitoring endpoint responds with 200 OK."""
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as client:
        response = await client.get("/health")
        assert response.status_code == 200
        data = response.json()
        assert data.get("status") == "healthy"


@pytest.mark.asyncio
async def test_ai_chat_ask_endpoint():
    """Verify that the AI Chat Doctor /chat/ask endpoint responds with a valid reply."""
    from app.core.security import create_access_token
    token = create_access_token({"sub": "test-chat-user", "email": "chat@helpix.local", "role": "PATIENT"})
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as client:
        payload = {"prompt": "I have a mild headache"}
        response = await client.post("/chat/ask", json=payload, headers={"Authorization": f"Bearer {token}"})
        assert response.status_code == 200
        data = response.json()
        assert "reply" in data
        assert "session_id" in data
        assert len(data["reply"]) > 0


@pytest.mark.asyncio
async def test_unauthorized_profile_access():
    """Verify that protected endpoints reject unauthorized requests without JWT token."""
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as client:
        response = await client.get("/auth/profile")
        assert response.status_code == 401
