package com.healthai.app.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val dateOfBirth: Date? = null,
    @ServerTimestamp val createdAt: Date? = null,

    // New fields for user type and doctor details
    val userType: String = "PATIENT", // Can be "PATIENT" or "DOCTOR"
    val specialization: String? = null,
    val licenseNumber: String? = null,
    val clinicAddress: String? = null
)