package com.healthai.app.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ChatMessage is now defined in AiChatScreen.kt to avoid duplicate class errors

class AiChatViewModel : ViewModel() {

    // IMPORTANT: Replace with your actual API Key
    private val apiKey = "AIzaSyC_iC5tuFa45XlJOuZmxqcb9lia7rtLRIo"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        requestOptions = RequestOptions(apiVersion = "v1")
    )

    private val _messages = mutableStateListOf(
        ChatMessage("Hello! I am Helpix, your personal AI Health Copilot. How can I assist you today?", false)
    )
    val messages: List<ChatMessage> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("You are Helpix, a professional AI Medical Assistant. Give helpful health advice but always add a disclaimer to consult a doctor.") },
            content(role = "model") { text("Understood. I am Helpix, your AI Health Copilot.") }
        )
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        _messages.add(ChatMessage(userText, true))
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userText)
                val responseText = response.text ?: "I'm sorry, I couldn't process that."
                _messages.add(ChatMessage(responseText, false))
            } catch (e: Exception) {
                Log.e("AiChatViewModel", "Error: ${e.message}", e)
                _messages.add(ChatMessage("Error: ${e.localizedMessage}. Please check if your API key is valid and has Gemini API enabled.", false))
            } finally {
                _isLoading.value = false
            }
        }
    }
}