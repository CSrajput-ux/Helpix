"""
tests/conftest.py
-----------------
Pytest configuration and autouse fixtures for HELPix integration tests.
Automatically initializes an in-memory SQLite database for PostgreSQL storage tests.
"""

import pytest_asyncio
from sqlalchemy.ext.asyncio import create_async_engine
from sqlalchemy.pool import StaticPool

import app.core.db as db_module

@pytest_asyncio.fixture(autouse=True)
async def initialize_database():
    """Create an isolated SQLAlchemy SQLite database for each test."""
    engine = create_async_engine(
        "sqlite+aiosqlite:///:memory:",
        poolclass=StaticPool,
    )
    await db_module.initialize_database(engine)
    db_module.engine = engine
    db_module.db = db_module.PostgresDatabase(engine)
    yield
    db_module.db = None
    db_module.engine = None
    await engine.dispose()
