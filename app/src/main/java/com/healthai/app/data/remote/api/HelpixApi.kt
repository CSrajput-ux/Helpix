package com.healthai.app.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// ---------------------------------------------------------------------------
// Data Classes (Auth & Profile)
// ---------------------------------------------------------------------------

data class SignupRequest(
    val full_name: String,
    val email: String,
    val password: String,
    val role: String = "PATIENT",
    val specialization: String? = null,
    val license_number: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val role: String,
    val user_id: String
)

data class UserProfile(
    val user_id: String,
    val full_name: String,
    val email: String,
    val role: String,
    val specialization: String?,
    val license_number: String?,
    val created_at: String
)

// ---------------------------------------------------------------------------
// Data Classes (Vitals & Health)
// ---------------------------------------------------------------------------

data class VitalsSyncRequest(
    val heart_rate: Int,
    val steps: Int,
    val spo2: Double,
    val blood_pressure: String? = null,
    val device_id: String? = null,
    val client_timestamp: String? = null
)

data class VitalsResponse(
    val id: String,
    val user_id: String,
    val heart_rate: Int,
    val steps: Int,
    val spo2: Double,
    val blood_pressure: String?,
    val device_id: String?,
    val timestamp: String
)

data class HealthScoreResponse(
    val score_id: String,
    val user_id: String,
    val score: Int,
    val label: String,
    val heart_rate_pts: Int,
    val spo2_pts: Int,
    val steps_pts: Int,
    val bp_pts: Int,
    val date: String,
    val computed_at: String,
    val is_syncing: Boolean
)

// ---------------------------------------------------------------------------
// Data Classes (Appointments)
// ---------------------------------------------------------------------------

data class AppointmentRequest(
    val doctor_id: String,
    val appointment_datetime: String,
    val reason: String
)

data class AppointmentResponse(
    val appointment_id: String,
    val patient_id: String,
    val doctor_id: String,
    val appointment_datetime: String,
    val status: String,
    val reason: String,
    val created_at: String
)

data class AppointmentStatusUpdate(
    val status: String // "CONFIRMED" | "CANCELLED" | "COMPLETED"
)

// ---------------------------------------------------------------------------
// Data Classes (Medicine & Reminders)
// ---------------------------------------------------------------------------

data class MedicineEntry(
    val medicine_name: String,
    val dosage: String,
    val frequency: String,
    val duration: String?,
    val instructions: String?
)

data class ReminderRequest(
    val medicine_name: String,
    val dosage: String,
    val frequency: String,
    val reminder_times: List<String>,
    val start_date: String,
    val end_date: String? = null,
    val instructions: String? = null
)

data class ReminderResponse(
    val reminder_id: String,
    val medicine_name: String,
    val dosage: String,
    val frequency: String,
    val reminder_times: List<String>,
    val start_date: String,
    val end_date: String?,
    val instructions: String?,
    val is_active: Boolean,
    val created_at: String
)

// ---------------------------------------------------------------------------
// Data Classes (Tools & AI)
// ---------------------------------------------------------------------------

data class SymptomRequest(
    val symptoms: List<String>,
    val age: Int? = null,
    val gender: String? = null
)

data class DiagnosisResult(
    val condition: String,
    val probability: Double,
    val severity: String,
    val recommendation: String
)

data class SymptomResponse(
    val check_id: String,
    val possible_conditions: List<DiagnosisResult>,
    val overall_risk: String,
    val should_see_doctor: Boolean,
    val analyzed_at: String
)

data class SOSRequest(
    val latitude: Double,
    val longitude: Double,
    val emergency_type: String = "MEDICAL"
)

data class SOSResponse(
    val sos_id: String,
    val status: String,
    val nearest_hospital: String,
    val estimated_response_mins: Int,
    val triggered_at: String
)

data class ChatRequest(
    val message: String,
    val session_id: String? = null
)

data class ChatResponse(
    val session_id: String,
    val reply: String,
    val suggestions: List<String>,
    val timestamp: String
)

// ---------------------------------------------------------------------------
// Retrofit Interface
// ---------------------------------------------------------------------------

interface HelpixApi {

    // ---- Auth ----
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<UserProfile>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<UserProfile>

    // ---- Vitals & Health Score ----
    @POST("vitals/sync")
    suspend fun syncVitals(@Body body: VitalsSyncRequest): Response<VitalsResponse>

    @GET("vitals/latest")
    suspend fun getLatestVitals(): Response<VitalsResponse>

    @GET("health-score/today")
    suspend fun getTodayHealthScore(): Response<HealthScoreResponse>

    @GET("health-score/history")
    suspend fun getHealthScoreHistory(@Query("days") days: Int = 7): Response<List<HealthScoreResponse>>

    // ---- Appointments ----
    @POST("appointments")
    suspend fun bookAppointment(@Body body: AppointmentRequest): Response<AppointmentResponse>

    @GET("appointments")
    suspend fun getAppointments(@Query("status") status: String? = null): Response<List<AppointmentResponse>>

    @PATCH("appointments/{appointment_id}/status")
    suspend fun updateAppointmentStatus(
        @Path("appointment_id") id: String,
        @Body body: AppointmentStatusUpdate
    ): Response<AppointmentResponse>

    // ---- Medicine Reminders ----
    @POST("reminders")
    suspend fun createReminder(@Body body: ReminderRequest): Response<ReminderResponse>

    @GET("reminders")
    suspend fun getReminders(): Response<List<ReminderResponse>>

    @POST("reminders/{reminder_id}/taken")
    suspend fun markDoseTaken(@Path("reminder_id") id: String): Response<Unit>

    // ---- Smart Tools (AI) ----
    @POST("tools/symptom-check")
    suspend fun checkSymptoms(@Body body: SymptomRequest): Response<SymptomResponse>

    @POST("tools/chat")
    suspend fun chatWithDoctor(@Body body: ChatRequest): Response<ChatResponse>

    @POST("tools/sos")
    suspend fun triggerSOS(@Body body: SOSRequest): Response<SOSResponse>

    @Multipart
    @POST("tools/skin-scan")
    suspend fun scanSkin(
        @Part image: MultipartBody.Part,
        @Part("body_area") bodyArea: RequestBody? = null
    ): Response<Unit>

    // ---- Notifications ----
    @GET("notifications")
    suspend fun getNotifications(@Query("unread_only") unread: Boolean = false): Response<List<Any>>

    // ---- Legacy / Common ----
    @Multipart
    @POST("process-prescription")
    suspend fun processPrescription(
        @Part file: MultipartBody.Part
    ): Response<Any>

    @GET("vault/list")
    suspend fun listVaultFiles(): Response<List<Any>>
}
