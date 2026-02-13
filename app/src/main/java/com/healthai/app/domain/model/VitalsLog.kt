package com.healthai.app.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class VitalsLog(
    @ServerTimestamp val timestamp: Date? = null,
    val heartRate: Int = 0,
    val spo2: Int = 0,
    val steps: Int = 0
)