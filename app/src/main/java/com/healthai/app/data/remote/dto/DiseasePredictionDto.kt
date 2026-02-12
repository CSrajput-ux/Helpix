package com.healthai.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.healthai.app.domain.model.DiseaseResult

data class DiseasePredictionDto(
    @SerializedName("disease_name") val diseaseName: String,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("severity_level") val severity: String,
    @SerializedName("medical_advice") val advice: String
) {
    // Mapper function to convert DTO to Domain Model
    fun toDomainModel(): DiseaseResult {
        return DiseaseResult(
            diseaseName = diseaseName,
            confidenceScore = confidence,
            severity = severity,
            recommendation = advice
        )
    }
}