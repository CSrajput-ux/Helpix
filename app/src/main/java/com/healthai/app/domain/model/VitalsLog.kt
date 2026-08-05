package com.healthai.app.domain.model

import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import java.util.Date

data class VitalsLog(
    @ServerTimestamp val timestamp: Date? = null,
    @SerializedName(value = "heart_rate", alternate = ["heartRate"]) val heartRate: Int = 0,
    @SerializedName(value = "spo2", alternate = ["blood_oxygen", "bloodOxygen"]) val spo2: Int = 0,
    @SerializedName("steps") val steps: Int = 0,
    val temperature: Float = 0f,
    val systolic: Double = 0.0,
    val diastolic: Double = 0.0
)