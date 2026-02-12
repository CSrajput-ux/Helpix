package com.healthai.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.dao.UserDao
import com.healthai.app.data.local.entity.HealthMetricEntity
import com.healthai.app.data.local.entity.UserProfile

@Database(
    entities = [HealthMetricEntity::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun healthMetricDao(): HealthMetricDao
    abstract fun userDao(): UserDao
}