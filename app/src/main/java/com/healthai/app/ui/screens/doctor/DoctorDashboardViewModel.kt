package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.domain.model.Appointment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

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
            
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Real data if logged in
                _appointments.value = appointmentRepository.getAppointmentsForDoctor(currentUser.uid)
            } else {
                // DUMMY DATA for testing since login is bypassed
                delay(1000) // Simulating network
                _appointments.value = listOf(
                    Appointment(
                        id = "1",
                        patientId = "PAT-882193",
                        doctorId = "DOC-001",
                        appointmentDate = Date(),
                        status = "SCHEDULED"
                    ),
                    Appointment(
                        id = "2",
                        patientId = "PAT-445210",
                        doctorId = "DOC-001",
                        appointmentDate = Date(System.currentTimeMillis() + 3600000),
                        status = "SCHEDULED"
                    ),
                    Appointment(
                        id = "3",
                        patientId = "PAT-112233",
                        doctorId = "DOC-001",
                        appointmentDate = Date(System.currentTimeMillis() + 7200000),
                        status = "COMPLETED"
                    )
                )
            }

            _isLoading.value = false
        }
    }
}