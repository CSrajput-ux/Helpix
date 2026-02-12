package com.healthai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.domain.model.DiseaseResult
import com.healthai.app.domain.usecase.AnalyzeCoughUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val analyzeCoughUseCase: AnalyzeCoughUseCase
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

    fun startScanning() {
        _scanState.value = ScanUiState.Scanning
        
        // Start Analysis
        viewModelScope.launch {
            _scanState.value = ScanUiState.Analyzing
            val result = analyzeCoughUseCase()
            _scanState.value = ScanUiState.Success(result)
        }
    }

    fun resetScan() {
        _scanState.value = ScanUiState.Idle
    }
}

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    object Analyzing : ScanUiState()
    data class Success(val result: DiseaseResult) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}