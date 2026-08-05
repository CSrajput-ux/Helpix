package com.healthai.app.ui.screens.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.healthai.app.data.remote.api.UpdateProfileRequest
import com.healthai.app.data.remote.api.WalletResponse
import com.healthai.app.data.remote.api.WalletTransactionResponse
import com.healthai.app.data.remote.api.WithdrawalRequest
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.Appointment
import com.healthai.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DoctorDashboardViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _doctorProfile = MutableStateFlow<User?>(null)
    val doctorProfile = _doctorProfile.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    private val _pendingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val pendingAppointments = _pendingAppointments.asStateFlow()

    private val _todayAppointmentsCount = MutableStateFlow(0)
    val todayAppointmentsCount = _todayAppointmentsCount.asStateFlow()

    private val _patientCount = MutableStateFlow(0)
    val patientCount = _patientCount.asStateFlow()

    // Financial Vault States
    private val _vaultBalance = MutableStateFlow(0.0)
    val vaultBalance = _vaultBalance.asStateFlow()

    private val _vaultPending = MutableStateFlow(0.0)
    val vaultPending = _vaultPending.asStateFlow()

    private val _vaultTransactions = MutableStateFlow<List<WalletTransactionResponse>>(emptyList())
    val vaultTransactions = _vaultTransactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Parallel loading for faster dashboard
            val profileJob = async { loadDoctorProfile() }
            val appointmentsJob = async { fetchAppointments() }
            val vaultJob = async { loadVaultData() }

            profileJob.await()
            appointmentsJob.await()
            vaultJob.await()

            _isLoading.value = false
        }
    }

    private suspend fun loadVaultData() {
        try {
            val response = userRepository.getWalletSummary()
            if (response.isSuccessful) {
                response.body()?.let {
                    _vaultBalance.value = it.total_balance
                    _vaultPending.value = it.pending_clearance
                    _vaultTransactions.value = it.recent_transactions
                }
            }
        } catch (e: Exception) {
            // Silently fail for dashboard, but can be logged
        }
    }

    fun requestWithdrawal(amount: Double, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.requestWithdrawal(amount)
                if (response.isSuccessful) {
                    loadVaultData() // Refresh balance
                    onResult(true, "Withdrawal request submitted successfully")
                } else {
                    onResult(false, "Withdrawal failed: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadDoctorProfile() {
        try {
            val response = userRepository.getProfile()
            if (response.isSuccessful) {
                response.body()?.let {
                    _doctorProfile.value = User(
                        id = it.user_id ?: "",
                        name = it.full_name ?: "",
                        email = it.email ?: "",
                        userType = it.role ?: "PATIENT",
                        specialization = it.specialization,
                        clinicAddress = it.clinic_address,
                        licenseNumber = it.license_number,
                        consultationFee = it.consultation_fee ?: 500.0,
                        experienceYears = it.experience_years
                    )
                }
            } else {
                _error.value = "Failed to load profile (${response.code()})"
            }
        } catch (e: Exception) {
            _error.value = "Network error: ${e.localizedMessage}"
        }
    }

    fun fetchAppointments() {
        viewModelScope.launch {
            try {
                val allResponse = appointmentRepository.getAppointmentsForDoctor()
                if (allResponse.isSuccessful) {
                    val all = allResponse.body()?.map {
                        Appointment(
                            id = it.appointment_id,
                            doctorId = it.doctor_id,
                            patientId = it.patient_id,
                            appointmentDate = try { dateFormat.parse(it.appointment_datetime) } catch (e: Exception) { null },
                            status = it.status,
                            reason = it.reason,
                            notes = it.notes,
                            amount = it.amount,
                            platformFee = it.platform_fee,
                            paymentStatus = it.payment_status
                        )
                    } ?: emptyList()
                    
                    _appointments.value = all

                    val today = java.time.LocalDate.now().toString()
                    _todayAppointmentsCount.value = all.count {
                        it.appointmentDate?.toInstant()?.toString()?.startsWith(today) ?: false
                    }

                    _pendingAppointments.value = all.filter {
                        it.status.equals("PENDING", ignoreCase = true)
                    }
                } else {
                    _error.value = "Failed to load appointments (${allResponse.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    fun updateAppointmentStatus(appointmentId: String, status: String) {
        viewModelScope.launch {
            try {
                val response = appointmentRepository.updateAppointmentStatus(appointmentId, status)
                if (response.isSuccessful) {
                    fetchAppointments()
                } else {
                    _error.value = "Failed to update appointment (${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    fun updateProfessionalProfile(
        specialization: String,
        licenseNumber: String,
        clinicAddress: String,
        experienceYears: Int?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = userRepository.updateProfile(
                    specialization = specialization,
                    licenseNumber = licenseNumber,
                    clinicAddress = clinicAddress,
                    experienceYears = experienceYears,
                    role = "DOCTOR" // Explicitly upgrade role
                )
                if (response.isSuccessful) {
                    // Force local state update to avoid race condition with backend role update
                    _doctorProfile.value = _doctorProfile.value?.copy(
                        userType = "DOCTOR",
                        specialization = specialization,
                        licenseNumber = licenseNumber,
                        clinicAddress = clinicAddress,
                        experienceYears = experienceYears
                    )
                    onResult(true)
                } else {
                    _error.value = "Failed to update profile (${response.code()})"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDoctorProfile() = loadDashboard()

    fun clearError() { _error.value = null }
}
