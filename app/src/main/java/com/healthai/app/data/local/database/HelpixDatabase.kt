package com.healthai.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.dao.ReminderDao
import com.healthai.app.data.local.dao.UserDao
import com.healthai.app.data.local.dao.SkinScanDao
import com.healthai.app.data.local.entity.HealthMetricEntity
import com.healthai.app.data.local.entity.Reminder
import com.healthai.app.data.local.entity.SkinScanEntity
import com.healthai.app.data.local.entity.UserProfile

@Database(
    entities = [Reminder::class, HealthMetricEntity::class, UserProfile::class, SkinScanEntity::class],
    version = 2,
    exportSchema = true
)
abstract class HelpixDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun healthMetricDao(): HealthMetricDao
    abstract fun userDao(): UserDao
    abstract fun skinScanDao(): SkinScanDao

    companion object {
        @Volatile
        private var INSTANCE: HelpixDatabase? = null

        fun getDatabase(context: Context): HelpixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HelpixDatabase::class.java,
                    "helpix_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
