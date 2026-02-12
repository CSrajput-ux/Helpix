package com.healthai.app.domain.model

data class DiseaseResult(
    val diseaseName: String,
    val confidenceScore: Float,
    val severity: String,
    val recommendation: String
)