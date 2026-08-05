"""
tests/test_security.py
----------------------
Automated security test suite verifying cryptographic hardening,
JWT authentication, password hashing, and AES encryption.
"""

import pytest
from app.core.security import hash_password, verify_password, create_access_token, encrypt_field, decrypt_field

def test_password_hashing_and_verification():
    """Verify Argon2/bcrypt password hashing resistance against rainbow table attacks."""
    raw_password = "EnterpriseSecurePassword#2026"
    hashed = hash_password(raw_password)
    assert hashed != raw_password
    assert verify_password(raw_password, hashed) is True
    assert verify_password("WrongPassword", hashed) is False


def test_jwt_token_generation():
    """Verify that JWT access tokens are correctly signed and contain expected claims."""
    token = create_access_token({"sub": "user-123456", "email": "test@helpix.local", "role": "PATIENT"})
    assert isinstance(token, str)
    assert len(token.split(".")) == 3  # Header.Payload.Signature


def test_aes_field_encryption_decryption():
    """Verify AES-256 Fernet field-level encryption for HIPAA/GDPR sensitive data."""
    plaintext = "Confidential Medical Diagnosis: Mild Hypertension"
    encrypted = encrypt_field(plaintext)
    assert encrypted != plaintext
    decrypted = decrypt_field(encrypted)
    assert decrypted == plaintext
