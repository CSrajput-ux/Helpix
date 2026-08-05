package com.healthai.app.data.repository

import com.healthai.app.data.remote.api.AppointmentRequest
import com.healthai.app.data.remote.api.AppointmentResponse
import com.healthai.app.data.remote.api.AppointmentStatusUpdate
import com.healthai.app.data.remote.api.HelpixApi
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FIX #2b: AppointmentRepository now routes all appointment operations through
 * the FastAPI backend (HelpixApi / Retrofit) instead of directly using Firebase Firestore.
 *
 * Firebase Firestore was causing appointments to be invisible to the backend,
 * making doctor/patient coordination impossible across the system.
 */
@Singleton
class AppointmentRepository @Inject constructor(
    private val api: HelpixApi
) {

    /** Book a new appointment (patient → doctor). */
    suspend fun createAppointment(
        doctorId: String,
        appointmentDatetime: String,
        reason: String
    ): Response<AppointmentResponse> {
        return api.bookAppointment(
            AppointmentRequest(
                doctor_id = doctorId,
                appointment_datetime = appointmentDatetime,
                reason = reason
            )
        )
    }

    /** List appointments for the authenticated doctor. */
    suspend fun getAppointmentsForDoctor(
        status: String? = null
    ): Response<List<AppointmentResponse>> = api.getAppointments(status)

    /** List appointments for the authenticated patient. */
    suspend fun getAppointmentsForPatient(
        status: String? = null
    ): Response<List<AppointmentResponse>> = api.getAppointments(status)

    /** Get a single appointment by ID (for detail view). */
    suspend fun getAppointmentById(appointmentId: String): Response<AppointmentResponse> =
        api.getAppointmentById(appointmentId)

    /**
     * Update the status of an appointment.
     * Valid values: "SCHEDULED" | "COMPLETED" | "CANCELLED" | "PENDING"
     */
    suspend fun updateAppointmentStatus(
        appointmentId: String,
        status: String
    ): Response<AppointmentResponse> {
        return api.updateAppointmentStatus(appointmentId, AppointmentStatusUpdate(status))
    }

    /** Cancel / delete an appointment. */
    suspend fun cancelAppointment(appointmentId: String): Response<Unit> =
        api.cancelAppointment(appointmentId)
}