package com.healthai.app.data.remote.api

import com.healthai.app.data.remote.dto.ScanResultDto
import com.healthai.app.domain.model.VitalsLog
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
    val license_number: String? = null,
    val clinic_address: String? = null,
    val consultation_fee: Double? = null,
    val experience_years: Int? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    val id_token: String,
    val role: String = "PATIENT"
)

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val role: String,
    val user_id: String
)

data class UserProfile(
    val user_id: String? = null,
    val full_name: String? = null,
    val email: String? = null,
    val role: String? = null,
    val specialization: String? = null,
    val license_number: String? = null,
    val clinic_address: String? = null,
    val consultation_fee: Double? = null,
    val experience_years: Int? = null,
    val age: Int? = null,
    val blood_group: String? = null,
    val gender: String? = null,
    val emergency_contact: String? = null,
    val location: String? = null,
    val allergies: String? = null,
    val profile_image_url: String? = null,
    val created_at: String? = null
)

data class UpdateProfileRequest(
    val full_name: String? = null,
    val age: Int? = null,
    val blood_group: String? = null,
    val gender: String? = null,
    val emergency_contact: String? = null,
    val location: String? = null,
    val allergies: String? = null,
    val specialization: String? = null,
    val license_number: String? = null,
    val clinic_address: String? = null,
    val consultation_fee: Double? = null,
    val experience_years: Int? = null,
    val role: String? = null
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
// Data Classes (Appointments & Schedule)
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
    val reason: String?,
    val notes: String?,
    val amount: Double = 0.0,
    val platform_fee: Double = 0.0,
    val payment_status: String = "PENDING",
    val booked_at: String
)

data class AppointmentStatusUpdate(
    val status: String // "SCHEDULED" | "CANCELLED" | "COMPLETED" | "PENDING"
)

data class AvailabilityRequest(
    val day_of_week: Int, // 0-6
    val start_time: String, // HH:mm
    val end_time: String, // HH:mm
    val slot_duration_mins: Int = 30
)

data class AvailabilityResponse(
    val availability_id: String,
    val doctor_id: String,
    val day_of_week: Int,
    val start_time: String,
    val end_time: String,
    val is_active: Boolean
)

// ---------------------------------------------------------------------------
// Data Classes (Doctor Management) — FIX #7
// ---------------------------------------------------------------------------

data class DoctorSummary(
    val user_id: String,
    val full_name: String,
    val specialization: String? = null,
    val clinic_address: String? = null,
    val consultation_fee: Double = 500.0,
    val experience_years: Int? = null
)

data class FollowPatientRequest(
    val patient_id: String
)

data class FollowPatientResponse(
    val message: String,
    val doctor_id: String,
    val patient_id: String,
    val linked_at: String
)

data class PatientSummary(
    val patient_id: String,
    val full_name: String,
    val email: String,
    val linked_at: String
)

data class DoctorPatientsResponse(
    val doctor_id: String,
    val patients: List<PatientSummary>
)

// ---------------------------------------------------------------------------
// Data Classes (Medical Records) — FIX #7
// ---------------------------------------------------------------------------

data class MedicalRecordRequest(
    val record_type: String,
    val medical_history: String,
    val notes: String? = null
)

data class MedicalRecordResponse(
    val record_id: String,
    val user_id: String,
    val record_type: String,
    val medical_history: String,
    val notes: String?,
    val created_at: String
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

data class UpdateReminderRequest(
    val medicine_name: String? = null,
    val dosage: String? = null,
    val frequency: String? = null,
    val reminder_times: List<String>? = null,
    val end_date: String? = null,
    val instructions: String? = null,
    val is_active: Boolean? = null
)

data class ReminderResponse(
    val reminder_id: String,
    val user_id: String,
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

data class DoseTakenResponse(
    val message: String,
    val reminder_id: String,
    val taken_at: String
)

// ---------------------------------------------------------------------------
// Data Classes (Tools & AI)
// ---------------------------------------------------------------------------

data class SymptomRequest(
    val symptoms: List<String>,
    val age: Int? = null,
    val gender: String? = null,
    val duration_days: Int? = null   // FIX #8: was missing
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
    val emergency_type: String = "MEDICAL",
    val message: String? = null         // FIX #8: was missing
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

// FIX #8: Properly typed SkinScanResponse (was Response<Unit>)
data class SkinScanResponse(
    val scan_id: String,
    val detected_condition: String,
    val severity: String,
    val confidence: Double,
    val recommendation: String,
    val scanned_at: String
)

// FIX #8: Properly typed PrescriptionResponse (was Response<Any>)
data class PrescriptionResponse(
    val prescription_id: String,
    val extracted_medicines: List<MedicineEntry>,
    val confidence: Double,
    val processed_at: String,
    val raw_text: String? = null
)

// FIX #7: Typed NotificationResponse (was Response<List<Any>>)
data class NotificationResponse(
    val notification_id: String,
    val user_id: String,
    val notification_type: String,
    val title: String,
    val message: String,
    val severity: String,
    val is_read: Boolean,
    val created_at: String
)

data class ToolStatsResponse(
    val active_tools_count: Int,
    val scans_done_count: Int,
    val ai_readiness_pct: Int
)

data class DietPlanResponse(
    val goal: String,
    val plan: Map<String, Any>,
    val generated_at: String
)

data class FitnessLogRequest(
    val activity_type: String,
    val duration_mins: Int,
    val calories_burned: Int? = null,
    val distance_km: Double? = null,
    val notes: String? = null
)

data class FitnessLogResponse(
    val log_id: String,
    val user_id: String,
    val activity_type: String,
    val duration_mins: Int,
    val calories_burned: Int?,
    val distance_km: Double?,
    val notes: String?,
    val logged_at: String
)

// FIX #7: VaultFileResponse typed DTO
data class VaultFileResponse(
    val file_id: String,
    val filename: String,
    val content_type: String,
    val size_bytes: Int,
    val uploaded_at: String,
    val uploaded_by: String
)

// ---------------------------------------------------------------------------
// Data Classes (Wallet & Transactions)
// ---------------------------------------------------------------------------

data class WalletTransactionResponse(
    val transaction_id: String,
    val doctor_id: String,
    val appointment_id: String?,
    val type: String,
    val amount: Double,
    val platform_fee: Double,
    val net_amount: Double,
    val status: String,
    val created_at: String
)

data class WalletResponse(
    val doctor_id: String,
    val total_balance: Double,
    val pending_clearance: Double,
    val recent_transactions: List<WalletTransactionResponse>
)

data class WithdrawalRequest(
    val amount: Double,
    val bank_account_id: String? = null
)

// ---------------------------------------------------------------------------
// Retrofit Interface — FIX #7: all missing endpoints added
// ---------------------------------------------------------------------------

interface HelpixApi {

    // ---- Auth & Profile ----
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<UserProfile>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @POST("auth/google")
    suspend fun googleLogin(@Body body: GoogleLoginRequest): Response<TokenResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<UserProfile>

    @PATCH("auth/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): Response<UserProfile>

    @Multipart
    @POST("auth/profile/image")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part
    ): Response<UserProfile>

    // FIX #5: Refresh token
    @POST("auth/refresh")
    suspend fun refreshToken(@Header("Authorization") bearer: String): Response<TokenResponse>

    // FIX #11: Forgot / reset password
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: Map<String, String>): Response<Map<String, String>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: Map<String, String>): Response<Map<String, String>>

    // ---- Vitals & Health Score ----
    @POST("vitals/sync")
    suspend fun syncVitals(@Body body: VitalsSyncRequest): Response<VitalsResponse>

    @GET("vitals/latest")
    suspend fun getLatestVitals(): Response<VitalsResponse>

    @GET("vitals/history")
    suspend fun getVitalsHistory(): Response<List<VitalsLog>>

    @GET("health-score/today")
    suspend fun getTodayHealthScore(): Response<HealthScoreResponse>

    @GET("health-score/history")
    suspend fun getHealthScoreHistory(@Query("days") days: Int = 7): Response<List<HealthScoreResponse>>

    // FIX #7: Force-recompute health score after vitals sync
    @POST("health-score/compute")
    suspend fun computeHealthScore(): Response<HealthScoreResponse>

    // ---- Appointments & Availability ----
    @POST("appointments")
    suspend fun bookAppointment(@Body body: AppointmentRequest): Response<AppointmentResponse>

    @GET("appointments")
    suspend fun getAppointments(@Query("status") status: String? = null): Response<List<AppointmentResponse>>

    // FIX #7: Get single appointment
    @GET("appointments/{appointment_id}")
    suspend fun getAppointmentById(@Path("appointment_id") id: String): Response<AppointmentResponse>

    @PATCH("appointments/{appointment_id}/status")
    suspend fun updateAppointmentStatus(
        @Path("appointment_id") id: String,
        @Body body: AppointmentStatusUpdate
    ): Response<AppointmentResponse>

    // FIX #7: Cancel appointment
    @DELETE("appointments/{appointment_id}")
    suspend fun cancelAppointment(@Path("appointment_id") id: String): Response<Unit>

    // ---- Doctor Availability ----
    @POST("doctor/availability")
    suspend fun setAvailability(@Body body: AvailabilityRequest): Response<AvailabilityResponse>

    @GET("doctor/availability")
    suspend fun getAvailability(@Query("doctor_id") doctorId: String? = null): Response<List<AvailabilityResponse>>

    // ---- Doctor Management (FIX #7) ----
    @GET("doctors")
    suspend fun getDoctors(): Response<List<DoctorSummary>>

    @POST("doctor/follow")
    suspend fun followPatient(@Body body: FollowPatientRequest): Response<FollowPatientResponse>

    @GET("doctor/patients")
    suspend fun getDoctorPatients(): Response<DoctorPatientsResponse>

    @DELETE("doctor/unfollow/{patient_id}")
    suspend fun unfollowPatient(@Path("patient_id") patientId: String): Response<Unit>

    @GET("vitals/patient/{patient_id}")
    suspend fun getPatientVitals(
        @Path("patient_id") patientId: String,
        @Query("limit") limit: Int = 20
    ): Response<List<VitalsResponse>>

    // ---- Medical Records (FIX #7) ----
    @POST("medical-records")
    suspend fun createMedicalRecord(@Body body: MedicalRecordRequest): Response<MedicalRecordResponse>

    @GET("medical-records")
    suspend fun getMedicalRecords(): Response<List<MedicalRecordResponse>>

    // ---- Medicine Reminders ----
    @POST("reminders")
    suspend fun createReminder(@Body body: ReminderRequest): Response<ReminderResponse>

    @GET("reminders")
    suspend fun getReminders(): Response<List<ReminderResponse>>

    // FIX #7: Update and delete reminders
    @PATCH("reminders/{reminder_id}")
    suspend fun updateReminder(
        @Path("reminder_id") id: String,
        @Body body: UpdateReminderRequest
    ): Response<ReminderResponse>

    @DELETE("reminders/{reminder_id}")
    suspend fun deleteReminder(@Path("reminder_id") id: String): Response<Unit>

    @POST("reminders/{reminder_id}/taken")
    suspend fun markDoseTaken(@Path("reminder_id") id: String): Response<DoseTakenResponse>

    // ---- Smart Tools (AI) ----
    @POST("tools/symptom-check")
    suspend fun checkSymptoms(@Body body: SymptomRequest): Response<SymptomResponse>

    @POST("tools/chat")
    suspend fun chatWithDoctor(@Body body: ChatRequest): Response<ChatResponse>

    @POST("tools/sos")
    suspend fun triggerSOS(@Body body: SOSRequest): Response<SOSResponse>

    // FIX #8: Properly typed (was Response<Unit> losing the result)
    @Multipart
    @POST("tools/skin-scan")
    suspend fun scanSkin(
        @Part image: MultipartBody.Part,
        @Part("body_area") bodyArea: RequestBody? = null
    ): Response<SkinScanResponse>

    @GET("tools/nearby-hospitals")
    suspend fun getNearbyHospitals(
        @Query("latitude") latitude: Double = 28.6139,
        @Query("longitude") longitude: Double = 77.2090,
        @Query("radius_km") radiusKm: Double = 10.0
    ): Response<List<Map<String, Any>>>

    // FIX #7: Diet history
    @GET("tools/diet-plan")
    suspend fun getDietPlan(@Query("goal") goal: String = "balanced"): Response<DietPlanResponse>

    @GET("tools/diet-history")
    suspend fun getDietHistory(): Response<List<DietPlanResponse>>

    // FIX #7: Fitness history
    @POST("tools/fitness/log")
    suspend fun logFitness(@Body body: FitnessLogRequest): Response<FitnessLogResponse>

    @GET("tools/fitness/history")
    suspend fun getFitnessHistory(@Query("limit") limit: Int = 20): Response<List<FitnessLogResponse>>

    // FIX #7: Tool stats
    @GET("tools/stats")
    suspend fun getToolStats(): Response<ToolStatsResponse>

    // ---- Scans & AI Direct ----
    @Multipart
    @POST("scans/analyze")
    suspend fun analyzeScan(
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): Response<ScanResultDto>

    @POST("chat/ask")
    suspend fun askAiDoctor(@Body message: Map<String, String>): Response<Map<String, String>>

    // ---- Prescription (FIX #8: typed, was Response<Any>) ----
    @Multipart
    @POST("process-prescription")
    suspend fun processPrescription(
        @Part file: MultipartBody.Part
    ): Response<PrescriptionResponse>

    // FIX #7: Prescription history
    @GET("prescriptions")
    suspend fun getPrescriptions(): Response<List<PrescriptionResponse>>

    // ---- Health Vault ----
    @GET("vault/list")
    suspend fun listVaultFiles(): Response<List<VaultFileResponse>>

    // FIX #7: Vault delete
    @DELETE("vault/{file_id}")
    suspend fun deleteVaultFile(@Path("file_id") fileId: String): Response<Unit>

    // ---- Notifications (FIX #7 & #8: typed, was Response<List<Any>>) ----
    @GET("notifications")
    suspend fun getNotifications(@Query("unread_only") unread: Boolean = false): Response<List<NotificationResponse>>

    @PATCH("notifications/{notification_id}/read")
    suspend fun markNotificationRead(@Path("notification_id") id: String): Response<NotificationResponse>

    @DELETE("notifications/{notification_id}")
    suspend fun deleteNotification(@Path("notification_id") id: String): Response<Unit>

    // ---- Wallet & Transactions ----
    @GET("doctor/wallet")
    suspend fun getWalletSummary(): Response<WalletResponse>

    @POST("doctor/wallet/withdraw")
    suspend fun requestWithdrawal(@Body body: WithdrawalRequest): Response<WalletTransactionResponse>
}
