"""
app/core/db.py
--------------
MongoDB database connection and collection setup using Motor (async driver).

Collections:
  - users          : Store patient and doctor profiles.
  - vitals         : Time-series collection for smartwatch data.
  - prescriptions  : Digitized medicine data from prescriptions.
  - medical_records: Encrypted sensitive health records.
  - doctor_links   : Maps which doctors are following which patients.
"""

import logging
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase, AsyncIOMotorGridFSBucket
from app.core.config import settings

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Global database state
# ---------------------------------------------------------------------------
client: AsyncIOMotorClient = None
db: AsyncIOMotorDatabase = None
fs_bucket: AsyncIOMotorGridFSBucket = None  # For Health Vault file storage


# ---------------------------------------------------------------------------
# Lifecycle helpers (called from main.py startup/shutdown events)
# ---------------------------------------------------------------------------

async def connect_db():
    """Create MongoDB connection and initialise collections."""
    global client, db, fs_bucket

    logger.info("Connecting to MongoDB at: %s", settings.MONGO_URL.split('@')[-1])  # Log host only for safety
    client = AsyncIOMotorClient(
        settings.MONGO_URL,
        serverSelectionTimeoutMS=5000,
        connectTimeoutMS=10000,
        minPoolSize=10,
        maxPoolSize=100
    )
    db = client[settings.MONGO_DB_NAME]

    logger.info("MongoDB client initialized. Starting index creation...")

    # GridFS bucket for large file storage (Health Vault)
    fs_bucket = AsyncIOMotorGridFSBucket(db, bucket_name="health_vault")

    # -----------------------------------------------------------------------
    # Ensure indexes exist for efficient querying
    # -----------------------------------------------------------------------
    await db["users"].create_index("email", unique=True)
    logger.info("Index 'email' on 'users' ready.")
    await db["users"].create_index("user_id", unique=True)
    logger.info("Index 'user_id' on 'users' ready.")
    await db["vitals"].create_index([("user_id", 1), ("timestamp", -1)])
    logger.info("Index on 'vitals' ready.")
    await db["doctor_links"].create_index([("doctor_id", 1), ("patient_id", 1)], unique=True)
    logger.info("Index on 'doctor_links' ready.")

    # -----------------------------------------------------------------------
    # Time-series collection setup
    # -----------------------------------------------------------------------
    logger.info("Checking for time-series collection support...")

    # Try to create a time-series collection for vitals (requires MongoDB 5.0+)
    try:
        existing = await db.list_collection_names()
        if "vitals_ts" not in existing:
            await db.create_collection(
                "vitals_ts",
                timeseries={
                    "timeField": "timestamp",
                    "metaField": "user_id",
                    "granularity": "seconds",
                },
            )
            logger.info("Created time-series collection 'vitals_ts'.")
        else:
            logger.info("Time-series collection 'vitals_ts' already exists.")
    except Exception as exc:
        # MongoDB < 5.0 or Atlas tier that doesn't support time-series
        logger.warning(
            "Could not create time-series collection (requires MongoDB 5.0+). "
            "Falling back to standard 'vitals' collection. Reason: %s", exc
        )

    logger.info("MongoDB connected. Database: '%s'", settings.MONGO_DB_NAME)


async def close_db():
    """Close the MongoDB connection gracefully."""
    global client
    if client:
        client.close()
        logger.info("MongoDB connection closed.")


# ---------------------------------------------------------------------------
# Collection accessors (call these inside route functions)
# ---------------------------------------------------------------------------

def get_users_collection():
    return db["users"]

def get_vitals_collection():
    """Returns the time-series collection if available, else fallback."""
    try:
        return db["vitals_ts"]
    except Exception:
        return db["vitals"]

def get_prescriptions_collection():
    return db["prescriptions"]

def get_medical_records_collection():
    return db["medical_records"]

def get_doctor_links_collection():
    return db["doctor_links"]

def get_fs_bucket() -> AsyncIOMotorGridFSBucket:
    return fs_bucket
