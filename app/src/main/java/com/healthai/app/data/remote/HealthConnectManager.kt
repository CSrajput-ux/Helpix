package com.healthai.app.data.remote

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthConnectClient by lazy {
        try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            null
        }
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class)
    )

    suspend fun isAvailable(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE && healthConnectClient != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun hasAllPermissions(): Boolean {
        return try {
            if (!isAvailable()) return false
            val grantedPermissions = healthConnectClient?.permissionController?.getGrantedPermissions()
            grantedPermissions?.containsAll(permissions) == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun readSteps(startTime: Instant, endTime: Instant): Long {
        if (!hasAllPermissions()) return 0
        return try {
            val response = healthConnectClient?.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response?.records?.sumOf { it.count } ?: 0
        } catch (e: Exception) {
            Log.e("HealthConnect", "Error reading steps", e)
            0
        }
    }

    suspend fun readHeartRate(startTime: Instant, endTime: Instant): Int {
        if (!hasAllPermissions()) return 0
        return try {
            val response = healthConnectClient?.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            if (response?.records?.isNotEmpty() == true) {
                val lastRecord = response.records.last()
                lastRecord.samples.lastOrNull()?.beatsPerMinute?.toInt() ?: 0
            } else 0
        } catch (e: Exception) {
            Log.e("HealthConnect", "Error reading heart rate", e)
            0
        }
    }
}