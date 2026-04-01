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
class AuthInterceptor(private val context: Context) : okhttp3.Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val token = runBlocking { TokenStore.get(context) }
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
// Retrofit Client Builder
// ---------------------------------------------------------------------------
object HelpixRetrofitClient {
    fun create(baseUrl: String, context: Context): HelpixApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
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

    // ✅ UPDATED with your local IP from ipconfig
    private val BASE_URL = "http://10.147.96.92:8000"

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
