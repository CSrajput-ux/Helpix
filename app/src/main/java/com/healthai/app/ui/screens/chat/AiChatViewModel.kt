package com.healthai.app.ui.screens.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.HealthApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val api: HealthApiService
) : ViewModel() {

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
                // Now calling our own backend instead of direct Gemini
                val response = api.askAiDoctor(mapOf("prompt" to userText))
                val responseText = response.body()?.get("reply") ?: "I'm sorry, I couldn't process that."
                _messages.add(ChatMessage(responseText, false))
            } catch (e: Exception) {
                Log.e("AiChatViewModel", "Error: ${e.message}", e)
                _messages.add(ChatMessage("Error: Connection to Helpix server failed.", false))
            } finally {
                _isLoading.value = false
            }
        }
    }
}