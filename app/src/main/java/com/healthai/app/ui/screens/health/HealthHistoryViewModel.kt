package com.healthai.app.ui.screens.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.VitalsLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _vitalsHistory = MutableStateFlow<List<VitalsLog>>(emptyList())
    val vitalsHistory = _vitalsHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchVitalsHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.getVitalsHistory()
                if (response.isSuccessful) {
                    _vitalsHistory.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
