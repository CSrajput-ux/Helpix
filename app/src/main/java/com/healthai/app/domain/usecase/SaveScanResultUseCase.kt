package com.healthai.app.domain.usecase

import com.healthai.app.data.repository.HealthRepository
import com.healthai.app.domain.model.DiseaseResult
import com.healthai.app.domain.model.HealthMetric
import javax.inject.Inject

class SaveScanResultUseCase @Inject constructor(
    private val repository: HealthRepository
) {
    suspend operator fun invoke(result: DiseaseResult) {
        // Logic to save the scan result to local DB or cloud
        // For now, we update the health metrics as a simulation of post-scan update
        repository.saveMetric(
            HealthMetric(
                heartRate = 75, // Simulated post-scan vitals
                steps = 100,
                bloodOxygen = 98
            )
        )
    }
}