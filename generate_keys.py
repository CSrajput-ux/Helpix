#!/usr/bin/env python3
"""
generate_keys.py
----------------
One-time key generation script.
Run this ONCE and copy the output values into your .env file.

Usage:
    python generate_keys.py
"""

import secrets
from cryptography.fernet import Fernet

print("=" * 60)
print("  Helpix AI – Secret Key Generator")
print("=" * 60)
print()

jwt_secret = secrets.token_hex(32)
print(f"JWT_SECRET_KEY={jwt_secret}")
print()

fernet_key = Fernet.generate_key().decode()
print(f"ENCRYPTION_KEY={fernet_key}")
print()
print("=" * 60)
print("  Copy these values into your .env file!")
print("  NEVER share or commit these keys.")
print("=" * 60)
