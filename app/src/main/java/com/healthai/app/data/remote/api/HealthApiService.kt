package com.healthai.app.data.remote.api

import com.healthai.app.domain.model.HealthMetric
import com.healthai.app.domain.model.VitalsLog
import com.healthai.app.data.remote.dto.ScanResultDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface HealthApiService {

    @POST("api/health/vitals")
    suspend fun syncVitals(@Body metrics: HealthMetric): Response<Unit>

    @GET("api/health/history")
    suspend fun getVitalsHistory(): Response<List<VitalsLog>>

    @Multipart
    @POST("api/scans/analyze")
    suspend fun analyzeScan(
        @Part image: MultipartBody.Part,
        @Part("type") type: String
    ): Response<ScanResultDto>

    @POST("api/chat/ask")
    suspend fun askAiDoctor(@Body message: Map<String, String>): Response<Map<String, String>>
}