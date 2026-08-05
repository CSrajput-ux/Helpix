package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _doctors = MutableStateFlow<List<User>>(emptyList())
    val doctors = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.getDoctors()
                if (response.isSuccessful) {
                    _doctors.value = response.body()?.map {
                        User(
                            id = it.user_id,
                            name = it.full_name,
                            specialization = it.specialization,
                            clinicAddress = it.clinic_address,
                            userType = "DOCTOR"
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
