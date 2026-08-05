"""
app/core/db.py
--------------
MongoDB connection using Motor (async driver).
Handles connection lifecycle, index creation, and collection accessors.
"""

import logging
import certifi
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase, AsyncIOMotorGridFSBucket
from app.core.config import settings

logger = logging.getLogger(__name__)

# ── Global state (populated at startup) ──────────────────────────────────────
client: AsyncIOMotorClient = None
db: AsyncIOMotorDatabase = None
fs_bucket: AsyncIOMotorGridFSBucket = None


# ── Lifecycle ─────────────────────────────────────────────────────────────────

async def connect_db():
    """Open MongoDB connection, create indexes, and set up GridFS bucket."""
    global client, db, fs_bucket

    # Mask credentials in log output
    safe_url = settings.MONGO_URL.split("@")[-1] if "@" in settings.MONGO_URL else settings.MONGO_URL
    logger.info("Connecting to MongoDB → %s / %s", safe_url, settings.MONGO_DB_NAME)

    kwargs = {
        "serverSelectionTimeoutMS": 5000,
        "connectTimeoutMS": 10000,
        "minPoolSize": 5,
        "maxPoolSize": 50,
    }
    if "mongodb+srv://" in settings.MONGO_URL or "tls=true" in settings.MONGO_URL.lower():
        kwargs["tlsCAFile"] = certifi.where()
        if settings.APP_ENV == "development":
            # Prevents local antivirus/firewalls from causing TLSV1_ALERT_INTERNAL_ERROR
            kwargs["tlsAllowInvalidCertificates"] = True

    client = AsyncIOMotorClient(settings.MONGO_URL, **kwargs)
    try:
        await client.admin.command("ping")
    except Exception as exc:
        logger.error(
            "\n"
            "════════════════════════════════════════════════════════════════════════════════\n"
            "❌ MONGODB ATLAS CONNECTION FAILED (SSL Handshake / Timeout)\n"
            "1. Check MongoDB Atlas -> Security -> Network Access -> Add IP Address (0.0.0.0/0 or your current IP).\n"
            "2. Ensure your Wi-Fi / VPN is not blocking MongoDB port 27017.\n"
            "════════════════════════════════════════════════════════════════════════════════\n"
        )
        raise exc

    db = client[settings.MONGO_DB_NAME]
    fs_bucket = AsyncIOMotorGridFSBucket(db, bucket_name="health_vault")

    # ── Indexes ───────────────────────────────────────────────────────────────
    await db["users"].create_index("email", unique=True)
    await db["users"].create_index("user_id", unique=True)
    await db["vitals"].create_index([("user_id", 1), ("timestamp", -1)])
    await db["doctor_links"].create_index([("doctor_id", 1), ("patient_id", 1)], unique=True)

    # Rate-limiting indexes (TTL auto-cleanup after 24h)
    await db["rate_limit_logs"].create_index([("key", 1), ("timestamp", -1)])
    await db["rate_limit_logs"].create_index("timestamp", expireAfterSeconds=86400)
    await db["rate_limit_lockouts"].create_index("key", unique=True)
    await db["rate_limit_lockouts"].create_index("blocked_until", expireAfterSeconds=0)

    # ── FIX #9: Missing indexes ────────────────────────────────────────────────
    await db["appointments"].create_index([("patient_id", 1), ("appointment_datetime", 1)])
    await db["appointments"].create_index([("doctor_id", 1), ("appointment_datetime", 1)])
    await db["prescriptions"].create_index([("user_id", 1), ("processed_at", -1)])
    await db["medicine_reminders"].create_index([("user_id", 1), ("is_active", 1)])
    await db["notifications"].create_index([("user_id", 1), ("created_at", -1)])
    await db["health_scores"].create_index([("user_id", 1), ("date", -1)])
    await db["symptom_checks"].create_index([("user_id", 1)])
    await db["chat_sessions"].create_index([("user_id", 1), ("session_id", 1)])
    await db["fitness_logs"].create_index([("user_id", 1), ("logged_at", -1)])
    await db["diet_plans"].create_index([("user_id", 1), ("generated_at", -1)])
    await db["dose_logs"].create_index([("user_id", 1), ("taken_at", -1)])
    await db["sos_events"].create_index([("user_id", 1), ("triggered_at", -1)])
    await db["medical_records"].create_index([("user_id", 1), ("created_at", -1)])
    await db["transactions"].create_index([("doctor_id", 1), ("created_at", -1)])


    # ── Time-series collection (MongoDB 5.0+, optional) ───────────────────────
    try:
        if "vitals_ts" not in await db.list_collection_names():
            await db.create_collection(
                "vitals_ts",
                timeseries={"timeField": "timestamp", "metaField": "user_id", "granularity": "seconds"},
            )
            logger.info("Created time-series collection 'vitals_ts'.")
    except Exception as exc:
        logger.warning("Time-series collection unavailable (MongoDB 5.0+ required): %s", exc)

    logger.info("MongoDB ready. Database: '%s'", settings.MONGO_DB_NAME)


async def is_db_healthy() -> bool:
    """Return whether the configured MongoDB server is reachable."""
    if client is None:
        return False
    try:
        await client.admin.command("ping")
    except Exception:
        logger.exception("MongoDB health check failed")
        return False
    return True


async def close_db():
    """Close the MongoDB connection pool gracefully."""
    if client:
        client.close()
        logger.info("MongoDB connection closed.")


# ── Collection accessors (use inside route handlers) ─────────────────────────

def get_users_collection():
    return db["users"]

def get_vitals_collection():
    """Returns the time-series collection if available, else standard collection."""
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

def get_transactions_collection():
    return db["transactions"]

def get_fs_bucket() -> AsyncIOMotorGridFSBucket:
    return fs_bucket
