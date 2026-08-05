package com.healthai.app.data.repository

import com.healthai.app.data.remote.api.HelpixApi
import com.healthai.app.data.remote.api.MedicalRecordRequest
import com.healthai.app.data.remote.api.MedicalRecordResponse
import com.healthai.app.data.remote.api.UpdateProfileRequest
import com.healthai.app.data.remote.api.UserProfile
import com.healthai.app.data.remote.api.VitalsResponse
import com.healthai.app.data.remote.api.WalletResponse
import com.healthai.app.data.remote.api.WalletTransactionResponse
import com.healthai.app.data.remote.api.WithdrawalRequest
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FIX #2: UserRepository now routes all user and vitals operations through the
 * FastAPI backend (HelpixApi / Retrofit) instead of directly using Firebase Firestore.
 *
 * Firebase Firestore was causing a split-data problem:
 *   - Registration / profile changes → Firestore (bypassed backend)
 *   - Vitals sync → backend (correct path)
 *   - Appointments → Firestore (bypassed backend)
 * All data now flows through one authoritative source: the Helpix FastAPI backend.
 */
@Singleton
class UserRepository @Inject constructor(
    private val api: HelpixApi
) {

    /** Fetch the authenticated user's profile from the backend. */
    suspend fun getProfile(): Response<UserProfile> = api.getProfile()

    /** Update profile fields on the backend. */
    suspend fun updateProfile(
        fullName: String? = null,
        age: Int? = null,
        bloodGroup: String? = null,
        gender: String? = null,
        emergencyContact: String? = null,
        location: String? = null,
        allergies: String? = null,
        specialization: String? = null,
        licenseNumber: String? = null,
        clinicAddress: String? = null,
        consultationFee: Double? = null,
        experienceYears: Int? = null,
        role: String? = null
    ): Response<UserProfile> {
        return api.updateProfile(
            UpdateProfileRequest(
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
                consultation_fee = consultationFee,
                experience_years = experienceYears,
                role = role
            )
        )
    }

    /** Retrieve vitals history for the authenticated patient. */
    suspend fun getVitalsHistory(): Response<List<com.healthai.app.domain.model.VitalsLog>> =
        api.getVitalsHistory()

    /** Get latest vitals reading (e.g. for dashboard). */
    suspend fun getLatestVitals(): Response<VitalsResponse> = api.getLatestVitals()

    /** Fetch the list of all registered doctors. */
    suspend fun getDoctors() = api.getDoctors()

    /** Get a specific doctor's profile (from the doctors list, filtered by id). */
    suspend fun getDoctorById(doctorId: String) = api.getDoctors().let { response ->
        if (response.isSuccessful) {
            response.body()?.firstOrNull { it.user_id == doctorId }
        } else null
    }

    /** Create a medical record for the current user. */
    suspend fun createMedicalRecord(
        recordType: String,
        history: String,
        notes: String? = null
    ): Response<MedicalRecordResponse> {
        return api.createMedicalRecord(
            MedicalRecordRequest(
                record_type = recordType,
                medical_history = history,
                notes = notes
            )
        )
    }

    /** Retrieve all medical records for the current user. */
    suspend fun getMedicalRecords(): Response<List<MedicalRecordResponse>> =
        api.getMedicalRecords()

    // ---- Financial Vault Methods ----

    suspend fun getWalletSummary(): Response<WalletResponse> = api.getWalletSummary()

    suspend fun requestWithdrawal(amount: Double, bankAccountId: String? = null): Response<WalletTransactionResponse> {
        return api.requestWithdrawal(WithdrawalRequest(amount, bankAccountId))
    }
}
