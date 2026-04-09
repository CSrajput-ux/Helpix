package com.healthai.app.data.remote.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
}

// ---------------------------------------------------------------------------
// JWT Header Interceptor
// ---------------------------------------------------------------------------
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        
        // Skip token fetch for auth endpoints to reduce delay
        val path = request.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/signup")) {
            return chain.proceed(request)
        }

        val token = runBlocking { TokenStore.get(context) }
        val authenticatedRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(authenticatedRequest)
    }
}

// ---------------------------------------------------------------------------
// Retry Interceptor
// ---------------------------------------------------------------------------
class RetryInterceptor(private val maxRetry: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        var response: okhttp3.Response? = null
        var exception: IOException? = null
        
        var tryCount = 0
        while (tryCount < maxRetry) {
            try {
                response?.close() // Close previous response to avoid leaks
                response = chain.proceed(request)
                
                // If successful or a client error (4xx like Wrong Password), DO NOT RETRY
                if (response.isSuccessful || response.code in 400..499) {
                    return response
                }
            } catch (e: IOException) {
                exception = e
            }
            tryCount++
        }
        
        return response ?: throw exception ?: IOException("Network error after $maxRetry retries")
    }
}

// ---------------------------------------------------------------------------
// Retrofit Client Builder
// ---------------------------------------------------------------------------
object HelpixRetrofitClient {
    fun create(baseUrl: String, context: Context): HelpixApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(RetryInterceptor(maxRetry = 2)) // Reduced retries
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS) // Reduced from 60s to 15s
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HelpixApi::class.java)
    }
}

// ---------------------------------------------------------------------------
// HelpixRepository
// ---------------------------------------------------------------------------
class HelpixRepository(private val context: Context) {

    // ✅ NOTE: Use "10.0.2.2" for Emulator, but your COMPUTER'S IP (e.g. 192.168.x.x) for Physical Device
    private val BASE_URL = "http://10.0.2.2:8000"

    private val api: HelpixApi by lazy {
        HelpixRetrofitClient.create(BASE_URL, context)
    }

    // ---- Authentication & Profile ----
    suspend fun signup(req: SignupRequest) = api.signup(req)

    suspend fun login(email: String, psw: String): Response<TokenResponse> {
        val res = api.login(LoginRequest(email, psw))
        if (res.isSuccessful) {
            res.body()?.let { TokenStore.save(context, it.access_token) }
        }
        return res
    }

    suspend fun logout() = TokenStore.clear(context)

    suspend fun profile() = api.getProfile()

    suspend fun updateProfile(req: UpdateProfileRequest) = api.updateProfile(req)
    
    suspend fun uploadProfileImage(body: MultipartBody.Part) = api.uploadProfileImage(body)

    // ---- Vitals & Health Score ----
    suspend fun syncVitals(req: VitalsSyncRequest) = api.syncVitals(req)

    suspend fun i_getLatestVitals() = api.getLatestVitals()

    suspend fun getHealthScore() = api.getTodayHealthScore()

    // ---- Appointments ----
    suspend fun bookAppointment(req: AppointmentRequest) = api.bookAppointment(req)

    suspend fun getAppointments(status: String? = null) = api.getAppointments(status)

    // ---- Medicine Reminders ----
    suspend fun createReminder(req: ReminderRequest) = api.createReminder(req)

    suspend fun getReminders() = api.getReminders()

    suspend fun markDoseTaken(id: String) = api.markDoseTaken(id)

    // ---- Smart Tools (AI) ----
    suspend fun checkSymptoms(req: SymptomRequest) = api.checkSymptoms(req)

    suspend fun chatWithDoctor(msg: String, sid: String? = null) = api.chatWithDoctor(ChatRequest(msg, sid))

    suspend fun triggerSOS(lat: Double, lon: Double) = api.triggerSOS(SOSRequest(lat, lon))

    suspend fun scanSkin(imageFile: File, bodyArea: String? = null): Response<Unit> {
        val body = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", imageFile.name, body)
        val areaPart = bodyArea?.toRequestBody("text/plain".toMediaTypeOrNull())
        return api.scanSkin(part, areaPart)
    }

    // ---- Notifications ----
    suspend fun getNotifications(unread: Boolean = false) = api.getNotifications(unread)
}
