package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.domain.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorDashboardViewModel : ViewModel() {

    private val appointmentRepository = AppointmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            val doctorId = auth.currentUser?.uid ?: return@launch
            _appointments.value = appointmentRepository.getAppointmentsForDoctor(doctorId)
            _isLoading.value = false
        }
    }
}