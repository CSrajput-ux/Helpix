package com.healthai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_metrics")
data class HealthMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val heartRate: Int,
    val steps: Int,
    val bloodOxygen: Int,
    val timestamp: Long
)