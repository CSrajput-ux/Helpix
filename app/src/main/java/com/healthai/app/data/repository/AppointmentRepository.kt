package com.healthai.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.healthai.app.domain.model.Appointment
import kotlinx.coroutines.tasks.await

class AppointmentRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createAppointment(appointment: Appointment) {
        firestore.collection("appointments").add(appointment).await()
    }

    suspend fun getAppointmentsForDoctor(doctorId: String): List<Appointment> {
        val snapshot = firestore.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .await()
        return snapshot.toObjects(Appointment::class.java)
    }

    suspend fun getAppointmentsForPatient(patientId: String): List<Appointment> {
        val snapshot = firestore.collection("appointments")
            .whereEqualTo("patientId", patientId)
            .get()
            .await()
        return snapshot.toObjects(Appointment::class.java)
    }

    suspend fun updateAppointmentStatus(appointmentId: String, status: String) {
        // Find document by a field if ID is not the document ID, 
        // but usually we should use the document ID.
        // Assuming the 'id' in Appointment model matches Firestore document ID
        firestore.collection("appointments").document(appointmentId)
            .update("status", status)
            .await()
    }
}