package com.healthai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.healthai.app.data.local.entity.HealthMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMetricDao {
    @Query("SELECT * FROM health_metrics ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMetric(): Flow<HealthMetricEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: HealthMetricEntity)
}