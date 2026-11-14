package com.healthai.app.api

import com.healthai.app.data.remote.api.HelpixApi
import com.healthai.app.data.remote.api.LoginRequest
import com.healthai.app.data.remote.api.SignupRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

class AuthApiTest {

    private val baseUrl = "https://helpix-2.onrender.com/"

    @Volatile
    private var token: String? = null

    private val authInterceptor = Interceptor { chain ->
        val req = token?.let {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $it").build()
        } ?: chain.request()
        chain.proceed(req)
    }

    private val api: HelpixApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HelpixApi::class.java)
    }

    @Test
    fun testSignupAndLoginIntegration() = runBlocking {
        val randomEmail = "testuser_${UUID.randomUUID().toString().take(8)}@example.com"
        val password = "TestPassword123!"
        val fullName = "Test User"

        // 1. SIGNUP
        val signupReq = SignupRequest(
            full_name = fullName,
            email = randomEmail,
            password = password,
            role = "PATIENT"
        )
        val signupRes = api.signup(signupReq)
        assertTrue("Signup should return 200 OK", signupRes.isSuccessful)
        val profile = signupRes.body()
        assertNotNull(profile)
        assertEquals(randomEmail, profile?.email)

        // 2. LOGIN
        val loginReq = LoginRequest(email = randomEmail, password = password)
        val loginRes = api.login(loginReq)
        assertTrue("Login should return 200 OK", loginRes.isSuccessful)
        val tokenResponse = loginRes.body()
        assertNotNull(tokenResponse)
        assertFalse(tokenResponse?.access_token.isNullOrBlank())

        // 3. FETCH PROFILE WITH AUTH HEADER
        token = tokenResponse?.access_token
        val authProfileRes = api.getProfile()
        assertTrue("Authenticated profile request should return 200 OK", authProfileRes.isSuccessful)
        val authProfile = authProfileRes.body()
        assertNotNull(authProfile)
        assertEquals(randomEmail, authProfile?.email)
        assertEquals(fullName, authProfile?.full_name)
    }
}
