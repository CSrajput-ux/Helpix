package com.healthai.app.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Appointment(
    val id: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val appointmentDate: Date? = null,
    val status: String = "SCHEDULED", // e.g., SCHEDULED, COMPLETED, CANCELED
    @ServerTimestamp val createdAt: Date? = null
)