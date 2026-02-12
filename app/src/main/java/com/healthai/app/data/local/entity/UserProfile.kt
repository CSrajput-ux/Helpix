package com.healthai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user implementation for prototype
    val fullName: String,
    val email: String,
    val age: Int,
    val bloodGroup: String
)