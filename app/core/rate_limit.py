"""
app/core/rate_limit.py
----------------------
Three-tier rate limiting with per-IP and per-account keys, exponential backoff
lockouts, and fully configurable thresholds via environment variables.

Tier        Who uses it          Keys checked          Backoff
──────────  ───────────────────  ────────────────────  ──────────────────
AUTH        login/signup/google  per-IP + per-account  yes (configurable)
PUBLIC      unauthenticated GET  per-IP only           no  (sliding window)
USER        JWT-protected        per-IP + per-user-ID  yes (softer config)

Key format:  "<tier>:<kind>:<value>"
  e.g.  "auth:ip:1.2.3.4"   "auth:account:user@example.com"
        "public:ip:1.2.3.4"
        "user:ip:1.2.3.4"   "user:account:uuid-..."
"""

import logging
from datetime import datetime, timezone, timedelta

from fastapi import Depends, HTTPException, Request, status

import app.core.db as _db
from app.core.config import settings
from app.core.security import get_current_user

logger = logging.getLogger(__name__)


# ─────────────────────────────────────────────────────────────────────────────
# Helpers
# ─────────────────────────────────────────────────────────────────────────────

def _get_client_ip(request: Request) -> str:
    """Return the real client IP, honouring X-Forwarded-For for proxied setups."""
    forwarded = request.headers.get("X-Forwarded-For")
    if forwarded:
        return forwarded.split(",")[0].strip()
    return request.client.host if request.client else "127.0.0.1"


# ─────────────────────────────────────────────────────────────────────────────
# Core engine
# ─────────────────────────────────────────────────────────────────────────────

async def _check_key(
    key: str,
    limit: int,
    window: int,
    backoff_base: int | None,
    backoff_max: int | None,
) -> None:
    """
    Enforce the rate limit for a single composite key.

    - If backoff_base is None, no lockout is applied (sliding-window only).
    - If the key is currently locked out, the lockout is extended exponentially
      and HTTP 429 is raised.
    - If the request count in the current window meets or exceeds `limit`, a
      lockout is created and HTTP 429 is raised.
    - Otherwise the request is logged and the function returns normally.
    """
    db = _db.db
    if db is None:
        # Fail-open: never block requests because of a DB outage
        logger.error("rate_limit: DB not ready, skipping check for key=%s", key)
        return

    now = datetime.now(timezone.utc)

    # ── 1. Check active lockout ───────────────────────────────────────────────
    if backoff_base is not None:
        lockout = await db["rate_limit_lockouts"].find_one({"key": key})
        if lockout:
            blocked_until = lockout["blocked_until"]
            if blocked_until.tzinfo is None:
                blocked_until = blocked_until.replace(tzinfo=timezone.utc)

            if blocked_until > now:
                # Still locked — extend with next backoff step
                consecutive = lockout.get("consecutive_violations", 1) + 1
                new_duration = min(
                    backoff_base * (2 ** (consecutive - 1)),
                    backoff_max,
                )
                new_blocked_until = now + timedelta(seconds=new_duration)

                await db["rate_limit_lockouts"].update_one(
                    {"key": key},
                    {"$set": {
                        "blocked_until": new_blocked_until,
                        "consecutive_violations": consecutive,
                    }},
                )
                retry_after = int((new_blocked_until - now).total_seconds())
                logger.warning(
                    "rate_limit: lockout extended key=%s consecutive=%d retry_after=%ds",
                    key, consecutive, retry_after,
                )
                raise HTTPException(
                    status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                    detail={
                        "message": "Too many requests. Please wait before retrying.",
                        "retry_after_seconds": retry_after,
                    },
                    headers={"Retry-After": str(retry_after)},
                )
    else:
        lockout = None

    # ── 2. Count requests in the sliding window ───────────────────────────────
    window_start = now - timedelta(seconds=window)
    count = await db["rate_limit_logs"].count_documents({
        "key": key,
        "timestamp": {"$gte": window_start},
    })

    if count >= limit:
        if backoff_base is not None:
            # Create / update lockout
            consecutive = 1
            if lockout:
                consecutive = lockout.get("consecutive_violations", 0) + 1

            duration = min(
                backoff_base * (2 ** (consecutive - 1)),
                backoff_max,
            )
            blocked_until = now + timedelta(seconds=duration)

            await db["rate_limit_lockouts"].update_one(
                {"key": key},
                {"$set": {
                    "blocked_until": blocked_until,
                    "consecutive_violations": consecutive,
                }},
                upsert=True,
            )
            # Clear logs so the window resets cleanly after the block expires
            await db["rate_limit_logs"].delete_many({"key": key})

            logger.warning(
                "rate_limit: limit exceeded key=%s limit=%d/%ds → blocked %ds",
                key, limit, window, duration,
            )
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail={
                    "message": "Rate limit exceeded. Temporary lockout applied.",
                    "retry_after_seconds": duration,
                },
                headers={"Retry-After": str(duration)},
            )
        else:
            # No backoff — sliding-window reject only
            logger.warning(
                "rate_limit: limit exceeded key=%s limit=%d/%ds (no lockout)",
                key, limit, window,
            )
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail={
                    "message": "Too many requests. Please slow down.",
                    "retry_after_seconds": window,
                },
                headers={"Retry-After": str(window)},
            )

    # ── 3. Record this request ────────────────────────────────────────────────
    await db["rate_limit_logs"].insert_one({"key": key, "timestamp": now})


# ─────────────────────────────────────────────────────────────────────────────
# Public API — three dependency functions + one helper
# ─────────────────────────────────────────────────────────────────────────────

async def auth_rate_limit(request: Request, email: str) -> None:
    """
    Call this at the start of every auth handler (login, signup, google).

    Enforces two independent limits:
      • Per-IP  : prevents distributed brute-force from many accounts
      • Per-account: prevents targeted brute-force against one email
    Both use exponential backoff on repeated violations.
    """
    ip = _get_client_ip(request)

    await _check_key(
        key=f"auth:ip:{ip}",
        limit=settings.RL_AUTH_IP_LIMIT,
        window=settings.RL_AUTH_IP_WINDOW,
        backoff_base=settings.RL_AUTH_BACKOFF_BASE,
        backoff_max=settings.RL_AUTH_BACKOFF_MAX,
    )
    await _check_key(
        key=f"auth:account:{email.strip().lower()}",
        limit=settings.RL_AUTH_ACCOUNT_LIMIT,
        window=settings.RL_AUTH_ACCOUNT_WINDOW,
        backoff_base=settings.RL_AUTH_BACKOFF_BASE,
        backoff_max=settings.RL_AUTH_BACKOFF_MAX,
    )


async def public_rate_limit(request: Request) -> None:
    """
    FastAPI dependency for unauthenticated public endpoints (/, /health, etc.).

    Per-IP sliding window only — no lockout so legitimate health checks are
    never permanently blocked.
    """
    ip = _get_client_ip(request)
    await _check_key(
        key=f"public:ip:{ip}",
        limit=settings.RL_PUBLIC_IP_LIMIT,
        window=settings.RL_PUBLIC_IP_WINDOW,
        backoff_base=None,   # no lockout
        backoff_max=None,
    )


async def user_rate_limit(
    request: Request,
    current_user: dict = Depends(get_current_user),
) -> dict:
    """
    FastAPI dependency for JWT-protected endpoints.

    Enforces two independent limits:
      • Per-IP      : guards against a single IP using many stolen tokens
      • Per-user-ID : guards against one account sending excessive requests
    Uses softer backoff than the AUTH tier.

    Returns the decoded JWT payload so routes can use it without a second
    Depends(get_current_user) call.
    """
    ip = _get_client_ip(request)
    user_id = current_user["sub"]

    await _check_key(
        key=f"user:ip:{ip}",
        limit=settings.RL_USER_IP_LIMIT,
        window=settings.RL_USER_IP_WINDOW,
        backoff_base=settings.RL_USER_BACKOFF_BASE,
        backoff_max=settings.RL_USER_BACKOFF_MAX,
    )
    await _check_key(
        key=f"user:account:{user_id}",
        limit=settings.RL_USER_ACCOUNT_LIMIT,
        window=settings.RL_USER_ACCOUNT_WINDOW,
        backoff_base=settings.RL_USER_BACKOFF_BASE,
        backoff_max=settings.RL_USER_BACKOFF_MAX,
    )
    return current_user


# ── Backward-compatible aliases (used by auth.py and main.py) ─────────────────
apply_auth_rate_limit = auth_rate_limit    # auth.py calls apply_auth_rate_limit(req, email)
moderate_rate_limit   = public_rate_limit  # main.py uses moderate_rate_limit on / and /health
loose_rate_limit      = user_rate_limit    # main.py and auth.py use loose_rate_limit on profile
