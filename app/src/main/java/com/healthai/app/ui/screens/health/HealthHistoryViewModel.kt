package com.healthai.app.ui.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.VitalsLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthHistoryViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _vitalsHistory = MutableStateFlow<List<VitalsLog>>(emptyList())
    val vitalsHistory = _vitalsHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchVitalsHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            _vitalsHistory.value = userRepository.getVitalsHistory()
            _isLoading.value = false
        }
    }
}