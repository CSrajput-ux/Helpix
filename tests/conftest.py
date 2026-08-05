"""
tests/conftest.py
-----------------
Pytest configuration and autouse fixtures for HELPix integration tests.
Automatically initializes an in-memory mock MongoDB connection and indexes before running tests.
"""

import pytest_asyncio
import mongomock_motor
import app.core.db as db_module
from app.core.config import settings

@pytest_asyncio.fixture(autouse=True)
async def initialize_database():
    """Ensure in-memory mock MongoDB connection is established before test execution."""
    client = mongomock_motor.AsyncMongoMockClient()
    db_module.client = client
    db_module.db = client[settings.MONGO_DB_NAME]
    db_module.fs_bucket = None

    await db_module.db["users"].create_index("email", unique=True)
    await db_module.db["users"].create_index("user_id", unique=True)
    await db_module.db["vitals"].create_index([("user_id", 1), ("timestamp", -1)])
    await db_module.db["doctor_links"].create_index([("doctor_id", 1), ("patient_id", 1)], unique=True)
    yield
    client.close()
