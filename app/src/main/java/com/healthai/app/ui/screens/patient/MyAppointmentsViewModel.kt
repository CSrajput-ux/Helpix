package com.healthai.app.ui.screens.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.domain.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyAppointmentsViewModel : ViewModel() {

    private val appointmentRepository = AppointmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            val patientId = auth.currentUser?.uid ?: return@launch
            _appointments.value = appointmentRepository.getAppointmentsForPatient(patientId)
            _isLoading.value = false
        }
    }
}