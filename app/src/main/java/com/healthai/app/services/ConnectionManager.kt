package com.healthai.app.services

import android.content.Context
import android.content.SharedPreferences

class ConnectionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("smartwatch_prefs", Context.MODE_PRIVATE)

    fun saveDevice(deviceName: String) {
        with(sharedPreferences.edit()) {
            putString("connected_device", deviceName)
            apply()
        }
    }

    fun getConnectedDevice(): String? {
        return sharedPreferences.getString("connected_device", null)
    }

    fun clearDevice() {
        with(sharedPreferences.edit()) {
            remove("connected_device")
            apply()
        }
    }
}