package com.healthai.app.data.repository

import com.healthai.app.data.local.dao.HealthMetricDao
import com.healthai.app.data.local.entity.HealthMetricEntity
import com.healthai.app.data.remote.api.HealthApiService
import com.healthai.app.domain.model.HealthMetric
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val dao: HealthMetricDao,
    private val api: HealthApiService
) : HealthRepository {

    override fun getLatestMetrics(): Flow<HealthMetric> {
        return dao.getLatestMetric().map { entity ->
            entity?.let {
                HealthMetric(it.heartRate, it.steps, it.bloodOxygen)
            } ?: HealthMetric(0, 0, 0)
        }
    }

    override suspend fun saveMetric(metric: HealthMetric) {
        // 1. Save to Local DB (Offline First)
        dao.insertMetric(
            HealthMetricEntity(
                heartRate = metric.heartRate,
                steps = metric.steps,
                bloodOxygen = metric.bloodOxygen,
                timestamp = System.currentTimeMillis()
            )
        )
        
        // 2. Sync with Backend
        try {
            api.syncVitals(metric)
        } catch (e: Exception) {
            // Handle sync error (e.g., log it or retry later)
        }
    }
}