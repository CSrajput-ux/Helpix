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
            _doctors.value = userRepository.getDoctors()
            _isLoading.value = false
        }
    }
}