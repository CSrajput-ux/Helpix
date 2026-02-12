package com.healthai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.domain.model.HealthMetric
import com.healthai.app.domain.usecase.GetHealthMetricsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getHealthMetricsUseCase: GetHealthMetricsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthMetric(0, 0, 0))
    val uiState: StateFlow<HealthMetric> = _uiState.asStateFlow()

    init {
        loadMetrics()
    }

    private fun loadMetrics() {
        viewModelScope.launch {
            getHealthMetricsUseCase().collect { metric ->
                _uiState.value = metric
            }
        }
    }
}