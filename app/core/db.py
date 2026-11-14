"""PostgreSQL JSONB storage with a compatibility layer for existing queries."""

import logging
import re
import uuid
from datetime import date, datetime
from typing import Any

from sqlalchemy import (
    JSON,
    Column,
    Float,
    MetaData,
    String,
    Table,
    and_,
    cast,
    delete,
    false,
    func,
    insert,
    make_url,
    or_,
    select,
    text,
    update,
)
from sqlalchemy.dialects.postgresql import JSONB, insert as postgres_insert
from sqlalchemy.ext.asyncio import AsyncEngine, create_async_engine

from app.core.config import settings

logger = logging.getLogger(__name__)

metadata = MetaData()
documents = Table(
    "app_documents",
    metadata,
    Column("collection", String(100), primary_key=True),
    Column("document_id", String(255), primary_key=True),
    Column("data", JSON().with_variant(JSONB, "postgresql"), nullable=False),
)

QUERY_INDEXES = {
    "vitals": (("user_id", "timestamp"),),
    "vitals_ts": (("user_id", "timestamp"),),
    "rate_limit_logs": (("key", "timestamp"), ("timestamp",)),
    "rate_limit_lockouts": (("blocked_until",),),
    "appointments": (
        ("patient_id", "appointment_datetime"),
        ("doctor_id", "appointment_datetime"),
    ),
    "prescriptions": (("user_id", "processed_at"),),
    "medicine_reminders": (("user_id", "is_active"),),
    "notifications": (("user_id", "created_at"),),
    "health_scores": (("user_id", "date"),),
    "symptom_checks": (("user_id",),),
    "chat_sessions": (("user_id", "session_id"),),
    "fitness_logs": (("user_id", "logged_at"),),
    "diet_plans": (("user_id", "generated_at"),),
    "dose_logs": (("user_id", "taken_at"),),
    "sos_events": (("user_id", "triggered_at"),),
    "medical_records": (("user_id", "created_at"),),
    "transactions": (("doctor_id", "created_at"),),
    "vault_files": (("uploaded_by", "uploaded_at"),),
    "skin_scans": (("user_id", "scanned_at"),),
    "cough_analyses": (("user_id",),),
}

engine: AsyncEngine | None = None
db: "PostgresDatabase | None" = None

_DATETIME_FIELD = re.compile(r"(?:_at|_datetime|_until|timestamp)$")


def _json_safe(value: Any) -> Any:
    if isinstance(value, datetime):
        return value.isoformat()
    if isinstance(value, date):
        return value.isoformat()
    if isinstance(value, uuid.UUID):
        return str(value)
    if isinstance(value, dict):
        return {str(key): _json_safe(item) for key, item in value.items()}
    if isinstance(value, (list, tuple)):
        return [_json_safe(item) for item in value]
    if value.__class__.__name__ == "ObjectId":
        return str(value)
    return value


def _decode(value: Any, field: str = "") -> Any:
    if isinstance(value, dict):
        return {key: _decode(item, key) for key, item in value.items()}
    if isinstance(value, list):
        return [_decode(item, field) for item in value]
    if isinstance(value, str) and _DATETIME_FIELD.search(field):
        try:
            return datetime.fromisoformat(value.replace("Z", "+00:00"))
        except ValueError:
            return value
    return value


def _set_path(document: dict, path: str, value: Any) -> None:
    parts = path.split(".")
    current = document
    for part in parts[:-1]:
        child = current.get(part)
        if not isinstance(child, dict):
            child = {}
            current[part] = child
        current = child
    current[parts[-1]] = _json_safe(value)


def _field_json_expr(field: str):
    expression = documents.c.data
    for part in field.split("."):
        expression = expression[part]
    return expression


def _field_expr(field: str):
    if field == "_id":
        return documents.c.document_id
    return _field_json_expr(field).as_string()


def _comparison(field: str, operator: str, value: Any):
    expression = _field_expr(field)
    normalized = _json_safe(value)
    if normalized is None:
        if operator == "$eq":
            return expression.is_(None)
        return expression.is_not(None)
    if isinstance(normalized, bool):
        expression = _field_json_expr(field).as_boolean()
    elif isinstance(normalized, (int, float)):
        expression = cast(expression, Float)

    operators = {
        "$eq": lambda: expression == normalized,
        "$ne": lambda: expression != normalized,
        "$gt": lambda: expression > normalized,
        "$gte": lambda: expression >= normalized,
        "$lt": lambda: expression < normalized,
        "$lte": lambda: expression <= normalized,
    }
    if operator not in operators:
        raise ValueError(f"Unsupported query operator: {operator}")
    return operators[operator]()


def _filter_clause(query: dict):
    clauses = []
    for field, expected in (query or {}).items():
        if field in {"$or", "$and"}:
            nested = [_filter_clause(item) for item in expected]
            clauses.append(or_(*nested) if field == "$or" else and_(*nested))
        elif field.startswith("$"):
            raise ValueError(f"Unsupported query operator: {field}")
        elif isinstance(expected, dict) and any(
            key.startswith("$") for key in expected
        ):
            for operator, value in expected.items():
                if operator == "$in":
                    clauses.append(
                        or_(*[
                            _comparison(field, "$eq", item) for item in value
                        ])
                        if value else false()
                    )
                elif operator == "$nin":
                    clauses.append(
                        and_(*[
                            _comparison(field, "$ne", item) for item in value
                        ])
                        if value else text("true")
                    )
                elif operator == "$exists":
                    expression = _field_expr(field)
                    clauses.append(
                        expression.is_not(None)
                        if value else expression.is_(None)
                    )
                else:
                    clauses.append(_comparison(field, operator, value))
        else:
            clauses.append(_comparison(field, "$eq", expected))
    return and_(*clauses) if clauses else text("true")


class InsertOneResult:
    def __init__(self, inserted_id: str):
        self.inserted_id = inserted_id


class InsertManyResult:
    def __init__(self, inserted_ids: list[str]):
        self.inserted_ids = inserted_ids


class UpdateResult:
    def __init__(
        self, matched_count: int, modified_count: int, upserted_id=None
    ):
        self.matched_count = matched_count
        self.modified_count = modified_count
        self.upserted_id = upserted_id


class DeleteResult:
    def __init__(self, deleted_count: int):
        self.deleted_count = deleted_count


class PostgresCursor:
    def __init__(self, collection: "PostgresCollection", query: dict):
        self.collection = collection
        self.query = query
        self.sort_fields: list[tuple[str, int]] = []
        self.max_results: int | None = None

    def sort(self, key_or_list, direction: int | None = None):
        if isinstance(key_or_list, str):
            self.sort_fields = [(key_or_list, direction or 1)]
        else:
            self.sort_fields = [(field, order) for field, order in key_or_list]
        return self

    def limit(self, count: int):
        self.max_results = count
        return self

    async def _fetch(self) -> list[dict]:
        statement = select(documents.c.document_id, documents.c.data).where(
            documents.c.collection == self.collection.name,
            _filter_clause(self.query),
        )
        for field, direction in self.sort_fields:
            expression = _field_expr(field)
            statement = statement.order_by(
                expression.desc() if direction < 0 else expression.asc()
            )
        if self.max_results is not None:
            statement = statement.limit(self.max_results)
        async with self.collection.database.engine.connect() as connection:
            rows = (await connection.execute(statement)).mappings().all()
        results = []
        for row in rows:
            document = _decode(row["data"])
            document.setdefault("_id", row["document_id"])
            results.append(document)
        return results

    def __aiter__(self):
        self._iterator = None
        return self

    async def __anext__(self):
        if self._iterator is None:
            self._iterator = iter(await self._fetch())
        try:
            return next(self._iterator)
        except StopIteration:
            raise StopAsyncIteration

    async def to_list(self, length: int | None = None):
        if length is None:
            return await self._fetch()
        original_limit = self.max_results
        self.max_results = (
            length if original_limit is None
            else min(length, original_limit)
        )
        try:
            return await self._fetch()
        finally:
            self.max_results = original_limit


class PostgresCollection:
    def __init__(self, database: "PostgresDatabase", name: str):
        self.database = database
        self.name = name

    async def insert_one(self, document: dict) -> InsertOneResult:
        stored = _json_safe(dict(document))
        document_id = str(stored.get("_id") or uuid.uuid4().hex[:24])
        stored["_id"] = document_id
        async with self.database.engine.begin() as connection:
            await connection.execute(insert(documents).values(
                collection=self.name, document_id=document_id, data=stored
            ))
        return InsertOneResult(document_id)

    async def insert_many(self, items: list[dict]) -> InsertManyResult:
        rows = []
        inserted_ids = []
        for item in items:
            stored = _json_safe(dict(item))
            document_id = str(stored.get("_id") or uuid.uuid4().hex[:24])
            stored["_id"] = document_id
            rows.append({
                "collection": self.name,
                "document_id": document_id,
                "data": stored,
            })
            inserted_ids.append(document_id)
        if rows:
            async with self.database.engine.begin() as connection:
                dialect = connection.dialect.name
                if dialect == "postgresql":
                    statement = postgres_insert(documents).values(rows)
                elif dialect == "sqlite":
                    from sqlalchemy.dialects.sqlite import (
                        insert as sqlite_insert,
                    )
                    statement = sqlite_insert(documents).values(rows)
                else:
                    await connection.execute(insert(documents), rows)
                    return InsertManyResult(inserted_ids)
                await connection.execute(statement.on_conflict_do_update(
                    index_elements=["collection", "document_id"],
                    set_={"data": statement.excluded.data},
                ))
        return InsertManyResult(inserted_ids)

    def find(self, query: dict | None = None) -> PostgresCursor:
        return PostgresCursor(self, query or {})

    async def find_one(
        self, query: dict | None = None, sort=None
    ) -> dict | None:
        cursor = self.find(query)
        if sort:
            cursor.sort(sort)
        cursor.limit(1)
        results = await cursor.to_list(1)
        return results[0] if results else None

    async def count_documents(self, query: dict | None = None) -> int:
        statement = select(func.count()).select_from(documents).where(
            documents.c.collection == self.name,
            _filter_clause(query or {}),
        )
        async with self.database.engine.connect() as connection:
            return int((await connection.scalar(statement)) or 0)

    async def update_one(
        self, query: dict, changes: dict, upsert: bool = False
    ) -> UpdateResult:
        async with self.database.engine.begin() as connection:
            statement = (
                select(documents.c.document_id, documents.c.data)
                .where(
                    documents.c.collection == self.name,
                    _filter_clause(query),
                )
                .limit(1)
                .with_for_update()
            )
            row = (await connection.execute(statement)).mappings().first()
            is_new = row is None
            if is_new and not upsert:
                return UpdateResult(0, 0)

            if is_new:
                document = {
                    key: value for key, value in query.items()
                    if not key.startswith("$") and not isinstance(value, dict)
                }
                document_id = str(document.get("_id") or uuid.uuid4().hex[:24])
                document["_id"] = document_id
            else:
                document = _decode(row["data"])
                document_id = row["document_id"]

            if any(key.startswith("$") for key in changes):
                for field, value in changes.get("$set", {}).items():
                    _set_path(document, field, value)
                if is_new:
                    for field, value in changes.get(
                        "$setOnInsert", {}
                    ).items():
                        _set_path(document, field, value)
                for field, value in changes.get("$inc", {}).items():
                    _set_path(document, field, document.get(field, 0) + value)
                for field, value in changes.get("$push", {}).items():
                    current = document.get(field)
                    if not isinstance(current, list):
                        current = []
                    current.append(_json_safe(value))
                    _set_path(document, field, current)
                for field in changes.get("$unset", {}):
                    document.pop(field, None)
            else:
                document.update(_json_safe(changes))

            stored = _json_safe(document)
            if is_new:
                await connection.execute(insert(documents).values(
                    collection=self.name, document_id=document_id, data=stored
                ))
            else:
                await connection.execute(
                    update(documents)
                    .where(
                        documents.c.collection == self.name,
                        documents.c.document_id == document_id,
                    )
                    .values(data=stored)
                )
            upserted_id = document_id if is_new else None
            return UpdateResult(0 if is_new else 1, 1, upserted_id)

    async def delete_one(self, query: dict) -> DeleteResult:
        async with self.database.engine.begin() as connection:
            statement = select(documents.c.document_id).where(
                documents.c.collection == self.name,
                _filter_clause(query),
            ).limit(1)
            document_id = await connection.scalar(statement)
            if document_id is None:
                return DeleteResult(0)
            result = await connection.execute(delete(documents).where(
                documents.c.collection == self.name,
                documents.c.document_id == document_id,
            ))
            return DeleteResult(result.rowcount or 0)

    async def delete_many(self, query: dict) -> DeleteResult:
        async with self.database.engine.begin() as connection:
            result = await connection.execute(delete(documents).where(
                documents.c.collection == self.name,
                _filter_clause(query),
            ))
            return DeleteResult(result.rowcount or 0)


class PostgresDatabase:
    def __init__(self, database_engine: AsyncEngine):
        self.engine = database_engine

    def __getitem__(self, name: str) -> PostgresCollection:
        return PostgresCollection(self, name)


def _database_url() -> str:
    url = make_url(settings.DATABASE_URL)
    if url.drivername in {"postgres", "postgresql"}:
        url = url.set(drivername="postgresql+asyncpg")
    if url.drivername == "postgresql+asyncpg":
        query = dict(url.query)
        if "sslmode" in query:
            query["ssl"] = query.pop("sslmode")
        query.pop("channel_binding", None)
        url = url.set(query=query)
    return url.render_as_string(hide_password=False)


async def initialize_database(database_engine: AsyncEngine) -> None:
    async with database_engine.begin() as connection:
        await connection.run_sync(metadata.create_all)
        if connection.dialect.name == "postgresql":
            await connection.execute(text(
                "CREATE INDEX IF NOT EXISTS ix_app_documents_collection "
                "ON app_documents (collection)"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_user_email "
                "ON app_documents ((data ->> 'email')) "
                "WHERE collection = 'users' AND data ? 'email'"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS uq_app_documents_user_id "
                "ON app_documents ((data ->> 'user_id')) "
                "WHERE collection = 'users' AND data ? 'user_id'"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_rate_limit_key "
                "ON app_documents ((data ->> 'key')) "
                "WHERE collection = 'rate_limit_lockouts' AND data ? 'key'"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_doctor_patient "
                "ON app_documents ((data ->> 'doctor_id'), "
                "(data ->> 'patient_id')) "
                "WHERE collection = 'doctor_links'"
            ))
            for collection, indexes in QUERY_INDEXES.items():
                for fields in indexes:
                    suffix = "_".join(fields)
                    expressions = ", ".join(
                        f"(data ->> '{field}')" for field in fields
                    )
                    index_name = f"ix_ad_{collection}_{suffix}"
                    await connection.execute(text(
                        f"CREATE INDEX IF NOT EXISTS {index_name} "
                        f"ON app_documents (collection, {expressions}) "
                        f"WHERE collection = '{collection}'"
                    ))
        elif connection.dialect.name == "sqlite":
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_user_email "
                "ON app_documents (json_extract(data, '$.email')) "
                "WHERE collection = 'users' "
                "AND json_type(data, '$.email') IS NOT NULL"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS uq_app_documents_user_id "
                "ON app_documents (json_extract(data, '$.user_id')) "
                "WHERE collection = 'users' "
                "AND json_type(data, '$.user_id') IS NOT NULL"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_rate_limit_key "
                "ON app_documents (json_extract(data, '$.key')) "
                "WHERE collection = 'rate_limit_lockouts' "
                "AND json_type(data, '$.key') IS NOT NULL"
            ))
            await connection.execute(text(
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                "uq_app_documents_doctor_patient "
                "ON app_documents (json_extract(data, '$.doctor_id'), "
                "json_extract(data, '$.patient_id')) "
                "WHERE collection = 'doctor_links'"
            ))


async def connect_db() -> None:
    """Create the PostgreSQL JSONB table and indexes, then connect."""
    global engine, db
    engine = create_async_engine(_database_url(), pool_pre_ping=True)
    try:
        await initialize_database(engine)
        async with engine.connect() as connection:
            await connection.execute(text("SELECT 1"))
    except Exception:
        await engine.dispose()
        engine = None
        logger.exception(
            "PostgreSQL connection or schema initialization failed"
        )
        raise
    db = PostgresDatabase(engine)
    logger.info("PostgreSQL ready.")


async def is_db_healthy() -> bool:
    if engine is None:
        return False
    try:
        async with engine.connect() as connection:
            await connection.execute(text("SELECT 1"))
        return True
    except Exception:
        logger.exception("PostgreSQL health check failed")
        return False


async def close_db() -> None:
    global engine, db
    if engine is not None:
        await engine.dispose()
        engine = None
        db = None
        logger.info("PostgreSQL connection pool closed.")


def get_users_collection():
    return db["users"]


def get_vitals_collection():
    return db["vitals_ts"]


def get_prescriptions_collection():
    return db["prescriptions"]


def get_medical_records_collection():
    return db["medical_records"]


def get_doctor_links_collection():
    return db["doctor_links"]


def get_transactions_collection():
    return db["transactions"]


def get_vault_files_collection():
    return db["vault_files"]
