package com.healthai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicineName: String,
    val dosage: String,
    val time: String,
    val schedule: String,
    val startDate: String,
    val endDate: String,
    var isTaken: Boolean = false
)
