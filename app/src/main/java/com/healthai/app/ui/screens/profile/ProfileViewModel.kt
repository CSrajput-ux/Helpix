package com.healthai.app.ui.screens.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.HelpixRepository
import com.healthai.app.data.remote.api.SignupRequest
import com.healthai.app.data.remote.api.UpdateProfileRequest
import com.healthai.app.data.remote.api.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HelpixRepository(application)

    private val _user = MutableStateFlow<UserProfile?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchUser()
    }

    fun login(email: String, psw: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(email, psw)
                if (response.isSuccessful) {
                    fetchUser()
                    onResult(true, "Login Successful")
                } else {
                    onResult(false, "Login Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(req: SignupRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.signup(req)
                if (response.isSuccessful) {
                    login(req.email, req.password, onResult)
                } else {
                    onResult(false, "Signup Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
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

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val file = uriToFile(uri)
                if (file != null) {
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    
                    val response = repository.uploadProfileImage(body)
                    if (response.isSuccessful) {
                        _user.value = response.body()
                        Log.d("ProfileViewModel", "Profile image uploaded successfully")
                    } else {
                        Log.e("ProfileViewModel", "Image upload failed: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error uploading image: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val context = getApplication<Application>().applicationContext
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_profile_image.jpg")
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (_: Exception) {
            null
        }
    }

    fun updateProfile(
        fullName: String, 
        age: Int?, 
        bloodGroup: String?, 
        gender: String?, 
        emergencyContact: String?, 
        location: String?,
        allergies: String?,
        specialization: String? = null,
        licenseNumber: String? = null,
        clinicAddress: String? = null,
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
                    location = location,
                    allergies = allergies,
                    specialization = specialization,
                    license_number = licenseNumber,
                    clinic_address = clinicAddress
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
