package com.healthai.app.ui.screens.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.HelpixRepository
import com.healthai.app.data.remote.api.UpdateProfileRequest
import com.healthai.app.data.remote.api.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HelpixRepository(application)

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.profile()
                if (response.isSuccessful) {
                    _user.value = response.body()
                    Log.d("ProfileViewModel", "User profile fetched: ${_user.value}")
                } else {
                    Log.d("ProfileViewModel", "Failed to fetch profile: ${response.code()} ${response.message()}")
                    _user.value = null
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user: ${e.message}")
                _user.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        fullName: String, 
        age: Int?, 
        bloodGroup: String?, 
        gender: String?, 
        emergencyContact: String?, 
        location: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UpdateProfileRequest(
                    full_name = fullName,
                    age = age,
                    blood_group = bloodGroup,
                    gender = gender,
                    emergency_contact = emergencyContact,
                    location = location
                )
                val response = repository.updateProfile(request)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    Log.d("ProfileViewModel", "Profile updated successfully")
                } else {
                    Log.e("ProfileViewModel", "Update failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
        }
    }
}
