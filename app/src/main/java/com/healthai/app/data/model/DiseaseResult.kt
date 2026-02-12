package com.healthai.app.data.model

data class DiseaseResult(
    val diseaseName: String,
    val confidenceScore: Float,
    val severity: String, // e.g., "Low", "Moderate", "High"
    val recommendation: String
)