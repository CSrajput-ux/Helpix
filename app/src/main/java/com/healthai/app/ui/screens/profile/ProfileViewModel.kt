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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: HelpixRepository,
    application: Application
) : AndroidViewModel(application) {

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
                val response = repository.login(email.trim(), psw)
                if (response.isSuccessful) {
                    fetchUser()
                    onResult(true, "Login Successful")
                } else {
                    val errMsg = parseErrorMessage(response)
                    onResult(false, if (errMsg.contains("credentials", ignoreCase = true) || response.code() == 401) "Incorrect email or password." else "Login Failed: $errMsg")
                }
            } catch (e: Exception) {
                onResult(false, "Network Error: ${e.localizedMessage ?: "Could not connect to server"}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(req: SignupRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val trimmedReq = req.copy(
                    email = req.email.trim(),
                    full_name = req.full_name.trim(),
                    password = req.password.trim()
                )
                val response = repository.signup(trimmedReq)
                if (response.isSuccessful) {
                    login(trimmedReq.email, trimmedReq.password, onResult)
                } else {
                    val errMsg = parseErrorMessage(response)
                    if (response.code() == 400 || response.code() == 409 || errMsg.contains("already", ignoreCase = true) || errMsg.contains("exists", ignoreCase = true)) {
                        val loginResp = repository.login(trimmedReq.email, trimmedReq.password)
                        if (loginResp.isSuccessful) {
                            fetchUser()
                            onResult(true, "Welcome back! Logged in successfully.")
                        } else {
                            onResult(false, "Account already exists with this email. Please click 'Sign In' or check your password.")
                        }
                    } else {
                        onResult(false, "Signup Failed: $errMsg")
                    }
                }
            } catch (e: Exception) {
                onResult(false, "Network Error: ${e.localizedMessage ?: "Could not connect to server"}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errBody = response.errorBody()?.string()
            if (!errBody.isNullOrBlank()) {
                val json = JSONObject(errBody)
                if (json.has("detail")) {
                    val detail = json.get("detail")
                    if (detail is org.json.JSONArray) {
                        val msgs = mutableListOf<String>()
                        for (i in 0 until detail.length()) {
                            val obj = detail.getJSONObject(i)
                            val locArr = obj.optJSONArray("loc")
                            val fieldName = if (locArr != null && locArr.length() > 0) {
                                locArr.getString(locArr.length() - 1)
                            } else ""
                            val msg = obj.optString("msg", "Invalid value")
                            if (fieldName.isNotBlank() && fieldName != "body") {
                                msgs.add("$fieldName: $msg")
                            } else {
                                msgs.add(msg)
                            }
                        }
                        msgs.joinToString("; ")
                    } else {
                        detail.toString()
                    }
                } else if (json.has("message")) {
                    json.getString("message")
                } else {
                    errBody
                }
            } else {
                when (response.code()) {
                    400 -> "Account already exists or invalid request."
                    401 -> "Unauthorized. Incorrect credentials."
                    422 -> "Invalid details provided. Please check name, email and password."
                    else -> "HTTP Error ${response.code()}"
                }
            }
        } catch (_: Exception) {
            when (response.code()) {
                400 -> "Account already exists or invalid request."
                401 -> "Unauthorized. Incorrect credentials."
                422 -> "Invalid details provided. Please check name, email and password."
                else -> "HTTP Error ${response.code()}"
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
        discoveryRadius: Float? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        bio: String? = null,
        onResult: ((Boolean, String) -> Unit)? = null
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
                    clinic_address = clinicAddress,
                    discovery_radius = discoveryRadius,
                    latitude = latitude,
                    longitude = longitude,
                    bio = bio
                )
                val response = repository.updateProfile(request)
                if (response.isSuccessful) {
                    _user.value = response.body()
                    Log.d("ProfileViewModel", "Profile updated successfully")
                    onResult?.invoke(true, "Profile updated successfully")
                } else {
                    Log.e("ProfileViewModel", "Update failed: ${response.code()}")
                    onResult?.invoke(false, "Update failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}")
                onResult?.invoke(false, "Error: ${e.localizedMessage}")
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
