package com.healthai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HealthState(
    val currentDate: String = "",
    val healthScore: Int = 0,
    val heartRate: Int = 0,
    val steps: Int = 0,
    val sleepQuality: String = "--",
    val sleepScore: Float = 0f,
    val lungHealth: Int = 0,
    val skinHealth: Int = 0,
    val isWatchConnected: Boolean = false,
    val hasHealthPermissions: Boolean = false
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthState())
    val uiState: StateFlow<HealthState> = _uiState.asStateFlow()

    init {
        checkPermissions()
        startRealTimeUpdates()
    }

    fun checkPermissions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                hasHealthPermissions = healthConnectManager.hasAllPermissions()
            )
        }
    }

    private fun startRealTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy • HH:mm:ss", Locale.getDefault())
                val nowStr = sdf.format(Date())

                val endTime = Instant.now()
                val startTime = endTime.minus(24, ChronoUnit.HOURS)

                val (steps, hr) = if (_uiState.value.hasHealthPermissions) {
                    val s = healthConnectManager.readSteps(startTime, endTime)
                    val h = healthConnectManager.readHeartRate(startTime, endTime)
                    Pair(s.toInt(), h)
                } else {
                    Pair(0, 0)
                }

                _uiState.value = _uiState.value.copy(
                    currentDate = nowStr,
                    steps = if (steps > 0) steps else _uiState.value.steps,
                    heartRate = if (hr > 0) hr else _uiState.value.heartRate,
                    isWatchConnected = _uiState.value.hasHealthPermissions,
                    healthScore = 87 // Example static score
                )

                delay(5000) // Sync every 5 seconds
            }
        }
    }
}