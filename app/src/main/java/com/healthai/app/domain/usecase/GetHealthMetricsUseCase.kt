package com.healthai.app.domain.usecase

import com.healthai.app.data.repository.HealthRepository
import com.healthai.app.domain.model.HealthMetric
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHealthMetricsUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    operator fun invoke(): Flow<HealthMetric> {
        return repository.getLatestMetrics()
    }
}