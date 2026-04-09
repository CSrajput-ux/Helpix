package com.healthai.app.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Response

// Data classes for NVIDIA API
data class NvidiaMessage(val role: String, val content: String)
data class NvidiaChatRequest(
    val model: String = "meta/llama-3.1-70b-instruct",
    val messages: List<NvidiaMessage>,
    val temperature: Double = 0.2,
    val top_p: Double = 0.7,
    val max_tokens: Int = 1024,
    val stream: Boolean = false
)
data class NvidiaChatResponse(val choices: List<NvidiaChoice>)
data class NvidiaChoice(val message: NvidiaMessage)

interface NvidiaApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun askAi(
        @Header("Authorization") auth: String,
        @Body request: NvidiaChatRequest
    ): Response<NvidiaChatResponse>
}

class AiChatViewModel : ViewModel() {

    private val nvidiaApi = Retrofit.Builder()
        .baseUrl("https://integrate.api.nvidia.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NvidiaApiService::class.java)

    // Using the provided NVIDIA API Key
    private val apiKey = "Bearer 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"

    private val _messages = mutableStateListOf(
        ChatMessage("Hello! I am Helpix, your personal AI Health Copilot. How can I assist you today?", false)
    )
    val messages: List<ChatMessage> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        _messages.add(ChatMessage(userText, true))
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val nvidiaMessages = listOf(
                    NvidiaMessage("system", "You are Helpix, a professional AI Medical Assistant. Give helpful health advice but always add a disclaimer to consult a doctor."),
                    NvidiaMessage("user", userText)
                )
                val response = nvidiaApi.askAi(apiKey, NvidiaChatRequest(messages = nvidiaMessages))
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.choices?.firstOrNull()?.message?.content 
                        ?: "I'm sorry, I couldn't process that."
                    _messages.add(ChatMessage(responseText, false))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AiChatViewModel", "Error: ${response.code()} $errorBody")
                    _messages.add(ChatMessage("Error: AI service is currently unavailable. (${response.code()})", false))
                }
            } catch (e: Exception) {
                Log.e("AiChatViewModel", "Error: ${e.message}", e)
                _messages.add(ChatMessage("Error: Connection to AI server failed. Please check your internet.", false))
            } finally {
                _isLoading.value = false
            }
        }
    }
}