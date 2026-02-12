
package com.healthai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

// Data State for the Screen
data class HealthState(
    val currentDate: String = "",
    val healthScore: Int = 0,
    val heartRate: Int = 0,
    val steps: Int = 0,
    val sleepQuality: String = "--",
    val sleepScore: Float = 0f,
    val lungHealth: Int = 0,
    val skinHealth: Int = 0,
    val isWatchConnected: Boolean = false
)

@HiltViewModel
class HealthViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HealthState())
    val uiState: StateFlow<HealthState> = _uiState.asStateFlow()

    init {
        startRealTimeUpdates()
    }

    private fun startRealTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                // 1. Update Date & Time (Real-time)
                val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy • HH:mm:ss", Locale.getDefault())
                val now = sdf.format(Date())

                // 2. Simulate Smart Watch Data Sync (Health Connect Integration Point)
                // Asal mein yahan hum "Health Connect API" call karte hain
                // Abhi ke liye hum random variation dikha rahe hain taaki "Live" lage
                val liveHeartRate = Random.nextInt(70, 75) // Simulating live pulse
                val liveScore = 87 // Base score

                _uiState.value = _uiState.value.copy(
                    currentDate = now,
                    healthScore = liveScore,
                    heartRate = liveHeartRate,
                    steps = 6540, // Example step count from watch
                    sleepQuality = "Fair",
                    sleepScore = 0.78f,
                    lungHealth = 92,
                    skinHealth = 85,
                    isWatchConnected = true
                )

                delay(1000) // Update every second
            }
        }
    }
}
