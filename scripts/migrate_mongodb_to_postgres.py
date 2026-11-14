"""Copy MongoDB collections into the PostgreSQL JSONB document store."""

import asyncio
import base64
from datetime import datetime, timezone
from decimal import Decimal

from bson import ObjectId
from bson.decimal128 import Decimal128
from pymongo import MongoClient
from pydantic_settings import BaseSettings, SettingsConfigDict

from app.core import db as postgres_db


class MigrationSettings(BaseSettings):
    mongo_source_url: str = ""
    mongo_url: str = ""
    mongo_source_db: str | None = None
    mongo_db_name: str = "helpix_ai"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
        case_sensitive=False,
    )


def _convert_bson(value):
    if isinstance(value, ObjectId):
        return str(value)
    if isinstance(value, datetime):
        if value.tzinfo is None:
            return value.replace(tzinfo=timezone.utc)
        return value
    if isinstance(value, Decimal128):
        return float(value.to_decimal())
    if isinstance(value, Decimal):
        return float(value)
    if isinstance(value, bytes):
        encoded = base64.b64encode(value).decode("ascii")
        return {"__bson_binary_base64__": encoded}
    if isinstance(value, dict):
        return {key: _convert_bson(item) for key, item in value.items()}
    if isinstance(value, list):
        return [_convert_bson(item) for item in value]
    return value


async def _migrate(source_database) -> None:
    await postgres_db.connect_db()
    try:
        for collection_name in source_database.list_collection_names():
            if collection_name.startswith("system."):
                continue
            destination = postgres_db.db[collection_name]
            cursor = source_database[collection_name].find().batch_size(500)
            batch = []
            total = 0
            for source_document in cursor:
                document = _convert_bson(source_document)
                batch.append(document)
                if len(batch) == 500:
                    await destination.insert_many(batch)
                    total += len(batch)
                    batch.clear()
            if batch:
                await destination.insert_many(batch)
                total += len(batch)
            print(f"{collection_name}: {total} documents copied")
    finally:
        await postgres_db.close_db()


def main() -> None:
    migration_settings = MigrationSettings()
    source_url = (
        migration_settings.mongo_source_url or migration_settings.mongo_url
    )
    if not source_url:
        raise SystemExit(
            "Set MONGO_SOURCE_URL to the MongoDB source connection string."
        )

    client = MongoClient(
        source_url,
        serverSelectionTimeoutMS=10000,
    )
    try:
        source_database_name = migration_settings.mongo_source_db
        if source_database_name is None:
            source_database_name = migration_settings.mongo_db_name
        asyncio.run(_migrate(client[source_database_name]))
    finally:
        client.close()


if __name__ == "__main__":
    main()
