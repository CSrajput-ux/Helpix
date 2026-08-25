package com.healthai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skin_scans")
data class SkinScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diseaseName: String,
    val confidence: Float,
    val topPredictions: String, // JSON or comma-separated
    val imagePath: String,
    val isUncertain: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
