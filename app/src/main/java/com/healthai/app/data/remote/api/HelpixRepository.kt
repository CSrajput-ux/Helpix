package com.healthai.app.data.remote.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.healthai.app.BuildConfig
import com.healthai.app.data.remote.dto.ScanResultDto
import com.healthai.app.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

// ---------------------------------------------------------------------------
// DataStore extension for JWT token persistence
// ---------------------------------------------------------------------------
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "helpix_prefs")

object TokenStore {
    private val KEY_TOKEN = stringPreferencesKey("jwt_token")

    suspend fun save(context: Context, token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun get(context: Context): String? {
        return context.dataStore.data.map { it[KEY_TOKEN] }.first()
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit { it.remove(KEY_TOKEN) }
    }

    // FIX #6: Synchronous token access backed by an in-memory cache.
    // The cache is populated once on first network call (inside a coroutine)
    // and updated whenever a new token is saved — eliminates runBlocking on the
    // OkHttp network thread which could cause ANR.
    @Volatile
    var cachedToken: String? = null

    /** Call this from a coroutine at app startup or after login. */
    suspend fun warmCache(context: Context) {
        cachedToken = get(context)
    }
}

// ---------------------------------------------------------------------------
// FIX #6: JWT Header Interceptor — uses in-memory cache (no runBlocking)
// ---------------------------------------------------------------------------
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        // Use the in-memory cached token — zero blocking IO on network thread
        val token = TokenStore.cachedToken
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

// ---------------------------------------------------------------------------
// FIX #5: Token Refresh Interceptor — automatically refreshes expired JWT
// ---------------------------------------------------------------------------
class TokenRefreshInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // Skip refresh for auth endpoints (avoid infinite loop)
        val url = originalRequest.url.toString()
        if (response.code == 401 &&
            !url.contains("/auth/login") &&
            !url.contains("/auth/signup") &&
            !url.contains("/auth/refresh") &&
            !url.contains("/auth/google")
        ) {
            response.close()

            // Try to refresh the token synchronously
            val newToken = runBlocking {
                try {
                    val refreshApi = HelpixRetrofitClient.createUnauthenticated(Constants.BASE_URL)
                    val currentToken = TokenStore.cachedToken ?: TokenStore.get(context)
                    if (currentToken.isNullOrBlank()) return@runBlocking null

                    // Call refresh with the current token in the header
                    val refreshResponse = refreshApi.refreshToken("Bearer $currentToken")
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body()
                        if (body != null) {
                            TokenStore.save(context, body.access_token)
                            TokenStore.cachedToken = body.access_token
                            body.access_token
                        } else null
                    } else null
                } catch (e: Exception) {
                    null
                }
            }

            if (!newToken.isNullOrBlank()) {
                // Retry original request with the new token
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return response
    }
}

// ---------------------------------------------------------------------------
// Retry Interceptor (network failures only, not auth errors)
// ---------------------------------------------------------------------------
class RetryInterceptor(private val maxRetry: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        var response: okhttp3.Response? = null
        var exception: IOException? = null

        var tryCount = 0
        while (tryCount < maxRetry) {
            try {
                response = chain.proceed(request)
                if (response.isSuccessful) return response
                // Don't retry auth errors
                if (response.code in 400..499) return response
            } catch (e: IOException) {
                exception = e
            }
            tryCount++
        }

        return response ?: throw exception ?: IOException("Unknown network error")
    }
}

// ---------------------------------------------------------------------------
// Retrofit Client Builder
// ---------------------------------------------------------------------------
object HelpixRetrofitClient {

    /** Full client with auth interceptor + token refresh. */
    fun create(baseUrl: String, context: Context): HelpixApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(TokenRefreshInterceptor(context))
            .addInterceptor(RetryInterceptor(maxRetry = 3))
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HelpixApi::class.java)
    }

    /** Minimal unauthenticated client for the refresh call itself. */
    fun createUnauthenticated(baseUrl: String): HelpixApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HelpixApi::class.java)
    }
}

// ---------------------------------------------------------------------------
// HelpixRepository
// ---------------------------------------------------------------------------
class HelpixRepository(private val context: Context) {

    private val BASE_URL = Constants.BASE_URL

    private val api: HelpixApi by lazy {
        HelpixRetrofitClient.create(BASE_URL, context)
    }

    // ---- Authentication & Profile ----
    suspend fun signup(req: SignupRequest) = api.signup(req)

    suspend fun login(email: String, psw: String): Response<TokenResponse> {
        val res = api.login(LoginRequest(email, psw))
        if (res.isSuccessful) {
            res.body()?.let {
                TokenStore.save(context, it.access_token)
                TokenStore.cachedToken = it.access_token   // FIX #6: warm cache
            }
        }
        return res
    }

    suspend fun googleLogin(idToken: String, role: String): Response<TokenResponse> {
        val res = api.googleLogin(GoogleLoginRequest(idToken, role))
        if (res.isSuccessful) {
            res.body()?.let {
                TokenStore.save(context, it.access_token)
                TokenStore.cachedToken = it.access_token   // FIX #6: warm cache
            }
        }
        return res
    }

    suspend fun logout() {
        TokenStore.clear(context)
        TokenStore.cachedToken = null
    }

    suspend fun refreshToken() = api.refreshToken(
        "Bearer ${TokenStore.cachedToken ?: TokenStore.get(context) ?: ""}"
    )

    suspend fun forgotPassword(email: String) = api.forgotPassword(mapOf("email" to email))

    suspend fun resetPassword(token: String, newPassword: String) =
        api.resetPassword(mapOf("token" to token, "new_password" to newPassword))

    suspend fun profile() = api.getProfile()

    suspend fun updateProfile(req: UpdateProfileRequest) = api.updateProfile(req)

    suspend fun uploadProfileImage(body: MultipartBody.Part) = api.uploadProfileImage(body)

    // ---- Vitals & Health Score ----
    suspend fun syncVitals(req: VitalsSyncRequest) = api.syncVitals(req)

    suspend fun getLatestVitals() = api.getLatestVitals()

    suspend fun getVitalsHistory() = api.getVitalsHistory()

    suspend fun getHealthScore() = api.getTodayHealthScore()

    suspend fun getHealthScoreHistory(days: Int = 7) = api.getHealthScoreHistory(days)

    suspend fun computeHealthScore() = api.computeHealthScore()

    // ---- Appointments ----
    suspend fun bookAppointment(req: AppointmentRequest) = api.bookAppointment(req)

    suspend fun getAppointments(status: String? = null) = api.getAppointments(status)

    suspend fun getAppointmentById(id: String) = api.getAppointmentById(id)

    suspend fun updateAppointmentStatus(id: String, status: String) =
        api.updateAppointmentStatus(id, AppointmentStatusUpdate(status))

    suspend fun cancelAppointment(id: String) = api.cancelAppointment(id)

    // ---- Doctor Management ----
    suspend fun getDoctors() = api.getDoctors()

    suspend fun followPatient(patientId: String) = api.followPatient(FollowPatientRequest(patientId))

    suspend fun getDoctorPatients() = api.getDoctorPatients()

    suspend fun unfollowPatient(patientId: String) = api.unfollowPatient(patientId)

    suspend fun getPatientVitals(patientId: String) = api.getPatientVitals(patientId)

    suspend fun setAvailability(req: AvailabilityRequest) = api.setAvailability(req)

    suspend fun getAvailability(doctorId: String? = null) = api.getAvailability(doctorId)

    // ---- Medical Records ----
    suspend fun createMedicalRecord(req: MedicalRecordRequest) = api.createMedicalRecord(req)

    suspend fun getMedicalRecords() = api.getMedicalRecords()

    // ---- Medicine Reminders ----
    suspend fun createReminder(req: ReminderRequest) = api.createReminder(req)

    suspend fun getReminders() = api.getReminders()

    suspend fun updateReminder(id: String, req: UpdateReminderRequest) = api.updateReminder(id, req)

    suspend fun deleteReminder(id: String) = api.deleteReminder(id)

    suspend fun markDoseTaken(id: String) = api.markDoseTaken(id)

    // ---- Smart Tools (AI) ----
    suspend fun checkSymptoms(req: SymptomRequest) = api.checkSymptoms(req)

    suspend fun chatWithDoctor(msg: String, sid: String? = null) = api.chatWithDoctor(ChatRequest(msg, sid))

    suspend fun triggerSOS(lat: Double, lon: Double, message: String? = null) =
        api.triggerSOS(SOSRequest(lat, lon, message = message))

    suspend fun scanSkin(imageFile: File, bodyArea: String? = null): Response<SkinScanResponse> {
        val body = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", imageFile.name, body)
        val areaPart = bodyArea?.toRequestBody("text/plain".toMediaTypeOrNull())
        return api.scanSkin(part, areaPart)
    }

    suspend fun analyzeScan(imageFile: File, type: String = "skin"): Response<ScanResultDto> {
        val body = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", imageFile.name, body)
        val typePart = type.toRequestBody("text/plain".toMediaTypeOrNull())
        return api.analyzeScan(part, typePart)
    }

    // ---- Prescription ----
    suspend fun processPrescription(file: MultipartBody.Part) = api.processPrescription(file)

    suspend fun getPrescriptions() = api.getPrescriptions()

    // ---- Notifications ----
    suspend fun getNotifications(unread: Boolean = false) = api.getNotifications(unread)

    suspend fun markNotificationRead(id: String) = api.markNotificationRead(id)

    suspend fun deleteNotification(id: String) = api.deleteNotification(id)

    // ---- Health Vault ----
    suspend fun listVaultFiles() = api.listVaultFiles()

    suspend fun deleteVaultFile(fileId: String) = api.deleteVaultFile(fileId)

    // ---- Diet & Fitness ----
    suspend fun getDietPlan(goal: String = "balanced") = api.getDietPlan(goal)

    suspend fun getDietHistory() = api.getDietHistory()

    suspend fun logFitness(req: FitnessLogRequest) = api.logFitness(req)

    suspend fun getFitnessHistory(limit: Int = 20) = api.getFitnessHistory(limit)

    suspend fun getToolStats() = api.getToolStats()

    // ---- AI Direct ----
    suspend fun askAiDoctor(prompt: String) = api.askAiDoctor(mapOf("prompt" to prompt))
}
