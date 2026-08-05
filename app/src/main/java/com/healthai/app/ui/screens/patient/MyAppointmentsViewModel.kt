package com.healthai.app.ui.screens.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyAppointmentsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun fetchAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = appointmentRepository.getAppointmentsForPatient()
                if (response.isSuccessful) {
                    _appointments.value = response.body()?.map {
                        Appointment(
                            id = it.appointment_id,
                            doctorId = it.doctor_id,
                            patientId = it.patient_id,
                            appointmentDate = try { dateFormat.parse(it.appointment_datetime) } catch (e: Exception) { null },
                            status = it.status
                        )
                    } ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
