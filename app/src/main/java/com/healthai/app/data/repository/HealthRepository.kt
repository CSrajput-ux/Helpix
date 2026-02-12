package com.healthai.app.data.repository

import com.healthai.app.domain.model.HealthMetric
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getLatestMetrics(): Flow<HealthMetric>
    suspend fun saveMetric(metric: HealthMetric)
}