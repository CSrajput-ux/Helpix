package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorListViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val remoteDoctors = userRepository.getDoctors()
                if (remoteDoctors.isEmpty()) {
                    // Database empty hai, isliye testing ke liye sample data add kar rahe hain
                    _doctors.value = listOf(
                        User(id = "1", name = "Dr. Lavkush Jadoun", email = "lav@example.com", userType = "DOCTOR", specialization = "Cardiologist"),
                        User(id = "2", name = "Dr. Sharma", email = "sharma@example.com", userType = "DOCTOR", specialization = "Dermatologist"),
                        User(id = "3", name = "Dr. Verma", email = "verma@example.com", userType = "DOCTOR", specialization = "Neurologist"),
                        User(id = "4", name = "Dr. Khan", email = "khan@example.com", userType = "DOCTOR", specialization = "Pulmonologist"),
                        User(id = "5", name = "Dr. Gupta", email = "gupta@example.com", userType = "DOCTOR", specialization = "Cardiologist")
                    )
                } else {
                    _doctors.value = remoteDoctors
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}