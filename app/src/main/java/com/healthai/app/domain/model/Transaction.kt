package com.healthai.app.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Transaction(
    val id: String = "",
    val userId: String = "", // Doctor ID
    val appointmentId: String = "",
    val type: String = "EARNING", // EARNING, WITHDRAWAL
    val amount: Double = 0.0,
    val platformFee: Double = 0.0,
    val netAmount: Double = 0.0,
    val status: String = "SUCCESS", // SUCCESS, PENDING, FAILED
    @ServerTimestamp val createdAt: Date? = null
)
