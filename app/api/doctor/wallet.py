"""
app/api/doctor/wallet.py
-------------------------
Doctor Wallet API for managing earnings and withdrawals.
"""

import uuid
from datetime import datetime, timezone
from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from app.core.security import get_current_user
from app.core.db import get_transactions_collection
from app.models.schemas import WalletResponse, WalletTransactionResponse, WithdrawalRequest

router = APIRouter(prefix="/wallet", tags=["Doctor Wallet"])

@router.get("", response_model=WalletResponse)
async def get_wallet_summary(current_user: dict = Depends(get_current_user)):
    if current_user.get("role") != "DOCTOR":
        raise HTTPException(status_code=403, detail="Only doctors can access the wallet")
        
    doctor_id = current_user["sub"]
    transactions_col = get_transactions_collection()
    
    # Calculate balances
    total_balance = 0.0
    pending_clearance = 0.0
    recent_transactions = []
    
    cursor = transactions_col.find({"doctor_id": doctor_id}).sort("created_at", -1)
    async for doc in cursor:
        if doc["status"] == "SUCCESS" and doc["type"] == "EARNING":
            total_balance += doc["net_amount"]
        elif doc["status"] == "PENDING" and doc["type"] == "WITHDRAWAL":
            # This money is still in balance, but locked for withdrawal
            total_balance -= doc["amount"]
            pending_clearance += doc["amount"]
        elif doc["status"] == "SUCCESS" and doc["type"] == "WITHDRAWAL":
            total_balance -= doc["amount"]
            
        # Add first 20 to recent
        if len(recent_transactions) < 20:
            recent_transactions.append(
                WalletTransactionResponse(
                    transaction_id=doc["transaction_id"],
                    doctor_id=doc["doctor_id"],
                    appointment_id=doc.get("appointment_id"),
                    type=doc["type"],
                    amount=doc["amount"],
                    platform_fee=doc.get("platform_fee", 0.0),
                    net_amount=doc.get("net_amount", doc["amount"]),
                    status=doc["status"],
                    created_at=doc["created_at"]
                )
            )
            
    return WalletResponse(
        doctor_id=doctor_id,
        total_balance=total_balance,
        pending_clearance=pending_clearance,
        recent_transactions=recent_transactions
    )


@router.post("/withdraw", response_model=WalletTransactionResponse)
async def request_withdrawal(
    body: WithdrawalRequest,
    current_user: dict = Depends(get_current_user)
):
    if current_user.get("role") != "DOCTOR":
        raise HTTPException(status_code=403, detail="Only doctors can withdraw")
        
    doctor_id = current_user["sub"]
    transactions_col = get_transactions_collection()
    
    # Calculate current balance
    total_balance = 0.0
    cursor = transactions_col.find({"doctor_id": doctor_id})
    async for doc in cursor:
        if doc["status"] == "SUCCESS" and doc["type"] == "EARNING":
            total_balance += doc["net_amount"]
        elif doc["status"] in ["SUCCESS", "PENDING"] and doc["type"] == "WITHDRAWAL":
            total_balance -= doc["amount"]
            
    if body.amount > total_balance:
        raise HTTPException(status_code=400, detail="Insufficient funds")
        
    tx_id = str(uuid.uuid4())
    now = datetime.now(timezone.utc)
    
    doc = {
        "transaction_id": tx_id,
        "doctor_id": doctor_id,
        "type": "WITHDRAWAL",
        "amount": body.amount,
        "platform_fee": 0.0,
        "net_amount": body.amount,
        "status": "PENDING", # Needs admin approval in real system
        "bank_account_id": body.bank_account_id,
        "created_at": now
    }
    
    await transactions_col.insert_one(doc)
    
    return WalletTransactionResponse(
        transaction_id=doc["transaction_id"],
        doctor_id=doc["doctor_id"],
        type=doc["type"],
        amount=doc["amount"],
        platform_fee=doc["platform_fee"],
        net_amount=doc["net_amount"],
        status=doc["status"],
        created_at=doc["created_at"]
    )
