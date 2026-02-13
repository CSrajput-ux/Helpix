package com.healthai.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.healthai.app.domain.model.User
import com.healthai.app.domain.model.VitalsLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun createUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun addVitalsLog(vitalsLog: VitalsLog) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).collection("vitals").add(vitalsLog).await()
    }

    suspend fun getVitalsHistory(): List<VitalsLog> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val snapshot = firestore.collection("users").document(userId).collection("vitals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .await()
        return snapshot.toObjects(VitalsLog::class.java)
    }

    suspend fun getDoctors(): List<User> {
        val snapshot = firestore.collection("users")
            .whereEqualTo("userType", "DOCTOR")
            .get()
            .await()
        return snapshot.toObjects(User::class.java)
    }

    suspend fun getDoctorById(doctorId: String): User? {
        val snapshot = firestore.collection("users").document(doctorId).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun getUserById(userId: String): User? {
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    fun getLatestVitalsStream(): Flow<VitalsLog?> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("users").document(userId).collection("vitals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    trySend(snapshot.documents[0].toObject(VitalsLog::class.java))
                } else {
                    trySend(null)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}