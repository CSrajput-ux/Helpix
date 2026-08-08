"""
app/core/supabase_client.py
---------------------------
Initializes and provides the Supabase client for file storage operations.
"""

import logging
from supabase import create_client, Client
from app.core.config import settings

logger = logging.getLogger(__name__)

supabase: Client | None = None

if settings.SUPABASE_URL and settings.SUPABASE_KEY:
    try:
        supabase = create_client(settings.SUPABASE_URL, settings.SUPABASE_KEY)
        logger.info("Supabase client initialized successfully.")
    except Exception as e:
        logger.error(f"Failed to initialize Supabase client: {e}")
else:
    logger.warning("Supabase credentials missing. File uploads to Supabase will fail.")

def get_supabase_client() -> Client | None:
    """Returns the initialized Supabase client."""
    return supabase
