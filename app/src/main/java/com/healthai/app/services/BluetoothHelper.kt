package com.healthai.app.services

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class BluetoothHelper(
    private val context: Context,
    private val onDataReceived: (heartRate: Int, steps: Int) -> Unit,
    private val onConnectionStateChanged: (isConnected: Boolean) -> Unit
) {
    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val HEART_RATE_CHAR_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE", "Connected! Discovering services...")
                onConnectionStateChanged(true)
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                onConnectionStateChanged(false)
                bluetoothGatt = null
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setupHeartRateNotification(gatt)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic.uuid == HEART_RATE_CHAR_UUID) {
                val heartRate = decodeHeartRate(characteristic)
                onDataReceived(heartRate, 0) // Steps logic depends on watch model
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connectAndPair(device: BluetoothDevice) {
        // Step 1: Trigger Pairing Popup
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            Log.d("BLE", "Starting Pairing...")
            device.createBond()
        }
        
        // Step 2: Connect to GATT
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun setupHeartRateNotification(gatt: BluetoothGatt) {
        val service = gatt.getService(HEART_RATE_SERVICE_UUID)
        val char = service?.getCharacteristic(HEART_RATE_CHAR_UUID)
        
        if (char != null) {
            gatt.setCharacteristicNotification(char, true)
            val descriptor = char.getDescriptor(CLIENT_CONFIG_UUID)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
            Log.d("BLE", "Heart Rate Notifications Enabled")
        }
    }

    private fun decodeHeartRate(characteristic: BluetoothGattCharacteristic): Int {
        val format = if (characteristic.value[0].toInt() and 0x01 != 0) {
            BluetoothGattCharacteristic.FORMAT_UINT16
        } else {
            BluetoothGattCharacteristic.FORMAT_UINT8
        }
        return characteristic.getIntValue(format, 1) ?: 0
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }
}
