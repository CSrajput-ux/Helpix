"""
app/core/config.py
------------------
Centralised settings loaded from .env via Pydantic BaseSettings.
On startup, validates that critical secrets are not left at default values.

Rate Limiting Tiers
-------------------
Three tiers, each independently configurable:

  AUTH   – Login, signup, Google OAuth (strictest)
           Dual key: per-IP  +  per-account (email)
           Exponential backoff on repeated violations

  PUBLIC – Unauthenticated public endpoints (/ , /health, etc.)
           Single key: per-IP only
           No lockout — just a sliding window counter

  USER   – Any authenticated endpoint (JWT required)
           Dual key: per-IP  +  per-user-ID
           Light exponential backoff

Backoff formula: base * (2 ^ (consecutive_violations - 1)), capped at max.
"""

from pydantic import field_validator, model_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # ── App ──────────────────────────────────────────────────────────────────
    APP_ENV: str = "development"           # "development" | "production"
    APP_VERSION: str = "2.0.0"

    # ── MongoDB ───────────────────────────────────────────────────────────────
    MONGO_URL: str = "mongodb://localhost:27017"
    MONGO_DB_NAME: str = "helpix_ai"

    # ── JWT ───────────────────────────────────────────────────────────────────
    JWT_SECRET_KEY: str = "CHANGE_ME"
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRE_MINUTES: int = 10080        # 7 days

    # ── AES Encryption (Fernet – base64-url encoded 32 bytes) ────────────────
    ENCRYPTION_KEY: str = "CHANGE_ME"

    # ── File Storage ──────────────────────────────────────────────────────────
    UPLOAD_DIR: str = "uploads"

    # ── Google OAuth ──────────────────────────────────────────────────────────
    GOOGLE_CLIENT_ID: str = ""

    # ── CORS ─────────────────────────────────────────────────────────────────
    # Comma-separated list. Use "*" only in development.
    ALLOWED_ORIGINS: str = "*"

    # ═══════════════════════════════════════════════════════════════════════════
    # Rate Limiting — AUTH tier (login / signup / google-oauth)
    # Strictest: both IP and account are checked independently.
    # Exponential backoff applied on lockout.
    # ═══════════════════════════════════════════════════════════════════════════

    # Per-IP: how many auth attempts from one IP before the IP is blocked
    RL_AUTH_IP_LIMIT: int = 20           # attempts
    RL_AUTH_IP_WINDOW: int = 300         # 5-minute sliding window (seconds)

    # Per-account: how many attempts against one email before that account is blocked
    RL_AUTH_ACCOUNT_LIMIT: int = 5       # attempts
    RL_AUTH_ACCOUNT_WINDOW: int = 300    # 5-minute sliding window (seconds)

    # Exponential backoff for auth violations:  base * 2^(n-1), capped at max
    RL_AUTH_BACKOFF_BASE: int = 30       # seconds for first lockout
    RL_AUTH_BACKOFF_MAX: int = 3600      # max 1 hour

    # ═══════════════════════════════════════════════════════════════════════════
    # Rate Limiting — PUBLIC tier (unauthenticated endpoints: /, /health)
    # Per-IP sliding window only. No lockout — simply reject excess requests.
    # ═══════════════════════════════════════════════════════════════════════════
    RL_PUBLIC_IP_LIMIT: int = 60         # requests per window
    RL_PUBLIC_IP_WINDOW: int = 60        # 1-minute sliding window (seconds)

    # ═══════════════════════════════════════════════════════════════════════════
    # Rate Limiting — USER tier (any JWT-protected endpoint)
    # Per-IP  +  per-user-ID are checked independently.
    # Light backoff to handle bursty clients without hard-blocking legit users.
    # ═══════════════════════════════════════════════════════════════════════════

    # Per-IP: protects against a single IP hammering with stolen tokens
    RL_USER_IP_LIMIT: int = 300          # requests per window
    RL_USER_IP_WINDOW: int = 60          # 1-minute sliding window (seconds)

    # Per-user: limits a single authenticated account
    RL_USER_ACCOUNT_LIMIT: int = 120     # requests per window
    RL_USER_ACCOUNT_WINDOW: int = 60     # 1-minute sliding window (seconds)

    # Exponential backoff for user violations (softer than auth)
    RL_USER_BACKOFF_BASE: int = 5        # seconds for first lockout
    RL_USER_BACKOFF_MAX: int = 300       # max 5 minutes

    # ── Legacy aliases (kept so existing .env files don't break) ─────────────
    # These are mapped to the new fields inside validate_secrets().
    RATE_LIMIT_STRICT_LIMIT: int = 0
    RATE_LIMIT_STRICT_WINDOW: int = 0
    RATE_LIMIT_MODERATE_LIMIT: int = 0
    RATE_LIMIT_MODERATE_WINDOW: int = 0
    RATE_LIMIT_LOOSE_LIMIT: int = 0
    RATE_LIMIT_LOOSE_WINDOW: int = 0
    RATE_LIMIT_BACKOFF_BASE: int = 0
    RATE_LIMIT_BACKOFF_MAX: int = 0

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    @field_validator("APP_ENV")
    @classmethod
    def validate_app_environment(cls, value: str) -> str:
        environment = value.lower().strip()
        if environment not in {"development", "test", "production"}:
            raise ValueError("APP_ENV must be development, test, or production")
        return environment

    @model_validator(mode="after")
    def validate_secrets(self) -> "Settings":
        """
        1. Crash fast in production if critical secrets are not configured.
        2. Apply legacy .env overrides to new field names if set.
        """
        # ── Production secret check ───────────────────────────────────────────
        if self.APP_ENV == "production":
            if self.JWT_SECRET_KEY == "CHANGE_ME":
                raise ValueError(
                    "JWT_SECRET_KEY must be set in production. "
                    "Generate one: python -c \"import secrets; print(secrets.token_hex(32))\""
                )
            if self.ENCRYPTION_KEY == "CHANGE_ME":
                raise ValueError(
                    "ENCRYPTION_KEY must be set in production. "
                    "Generate one: python -c \"from cryptography.fernet import Fernet; print(Fernet.generate_key().decode())\""
                )
           # if not self.ALLOWED_ORIGINS or self.ALLOWED_ORIGINS.strip() == "*":
                 #raise ValueError(
                  #  "ALLOWED_ORIGINS must contain explicit HTTPS origins in production."
               # )
                if not self.ALLOWED_ORIGINS:
                   raise ValueError("ALLOWED_ORIGINS is required in production.")
                  
            if self.MONGO_URL.startswith("mongodb://localhost"):
                raise ValueError("MONGO_URL must not point to localhost in production.")

        # ── Migrate legacy RATE_LIMIT_* vars if they were explicitly set ─────
        # (non-zero means the user had them in their .env)
        if self.RATE_LIMIT_STRICT_LIMIT:
            self.RL_AUTH_ACCOUNT_LIMIT = self.RATE_LIMIT_STRICT_LIMIT
        if self.RATE_LIMIT_STRICT_WINDOW:
            self.RL_AUTH_ACCOUNT_WINDOW = self.RATE_LIMIT_STRICT_WINDOW
        if self.RATE_LIMIT_MODERATE_LIMIT:
            self.RL_PUBLIC_IP_LIMIT = self.RATE_LIMIT_MODERATE_LIMIT
        if self.RATE_LIMIT_MODERATE_WINDOW:
            self.RL_PUBLIC_IP_WINDOW = self.RATE_LIMIT_MODERATE_WINDOW
        if self.RATE_LIMIT_LOOSE_LIMIT:
            self.RL_USER_ACCOUNT_LIMIT = self.RATE_LIMIT_LOOSE_LIMIT
        if self.RATE_LIMIT_LOOSE_WINDOW:
            self.RL_USER_ACCOUNT_WINDOW = self.RATE_LIMIT_LOOSE_WINDOW
        if self.RATE_LIMIT_BACKOFF_BASE:
            self.RL_AUTH_BACKOFF_BASE = self.RATE_LIMIT_BACKOFF_BASE
            self.RL_USER_BACKOFF_BASE = self.RATE_LIMIT_BACKOFF_BASE
        if self.RATE_LIMIT_BACKOFF_MAX:
            self.RL_AUTH_BACKOFF_MAX = self.RATE_LIMIT_BACKOFF_MAX
            self.RL_USER_BACKOFF_MAX = self.RATE_LIMIT_BACKOFF_MAX

        return self


settings = Settings()
