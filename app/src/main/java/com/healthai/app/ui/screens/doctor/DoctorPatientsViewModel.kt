package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.DoctorPatientsResponse
import com.healthai.app.data.remote.api.HelpixApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FIX #12b: Provides real backend data for DoctorPatientsScreen.
 * Previously the screen used a hardcoded emptyList().
 */
@HiltViewModel
class DoctorPatientsViewModel @Inject constructor(
    private val api: HelpixApi
) : ViewModel() {

    private val _patientsState = MutableStateFlow<DoctorPatientsUiState>(DoctorPatientsUiState.Loading)
    val patientsState = _patientsState.asStateFlow()

    init {
        loadPatients()
    }

    fun loadPatients() {
        viewModelScope.launch {
            _patientsState.value = DoctorPatientsUiState.Loading
            try {
                val response = api.getDoctorPatients()
                if (response.isSuccessful) {
                    val body = response.body()
                    _patientsState.value = DoctorPatientsUiState.Success(
                        body?.patients ?: emptyList()
                    )
                } else {
                    _patientsState.value = DoctorPatientsUiState.Error(
                        "Failed to load patients (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                _patientsState.value = DoctorPatientsUiState.Error(
                    e.localizedMessage ?: "Network error"
                )
            }
        }
    }

    fun followPatient(patientId: String) {
        viewModelScope.launch {
            try {
                api.followPatient(
                    com.healthai.app.data.remote.api.FollowPatientRequest(patientId)
                )
                loadPatients() // Refresh list
            } catch (e: Exception) {
                _patientsState.value = DoctorPatientsUiState.Error(
                    "Failed to follow patient: ${e.localizedMessage}"
                )
            }
        }
    }

    fun unfollowPatient(patientId: String) {
        viewModelScope.launch {
            try {
                api.unfollowPatient(patientId)
                loadPatients() // Refresh list
            } catch (e: Exception) {
                _patientsState.value = DoctorPatientsUiState.Error(
                    "Failed to unfollow patient: ${e.localizedMessage}"
                )
            }
        }
    }
}

sealed class DoctorPatientsUiState {
    object Loading : DoctorPatientsUiState()
    data class Success(
        val patients: List<com.healthai.app.data.remote.api.PatientSummary>
    ) : DoctorPatientsUiState()
    data class Error(val message: String) : DoctorPatientsUiState()
}
