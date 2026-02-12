package com.healthai.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val dosage: String,
    val time: String, // We'll store time as String for simplicity for now
    val schedule: String,
    val startDate: String, // Dates as String, can be improved with type converters
    val endDate: String,
    var isTaken: Boolean = false
)
