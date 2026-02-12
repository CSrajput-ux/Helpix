package com.healthai.app.data.remote.dto

data class ScanResultDto(
    val scanId: String,
    val timestamp: Long,
    val prediction: DiseasePredictionDto
)