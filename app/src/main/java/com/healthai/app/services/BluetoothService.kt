package com.healthai.app.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BleDevice(val name: String, val address: String, val device: BluetoothDevice)

class BluetoothService(private val context: Context) {

    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bleScanner = bluetoothAdapter?.bluetoothLeScanner

    private val _foundDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val foundDevices = _foundDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            // IMPORTANT: Some devices hide name in scan record
            val deviceName = result.scanRecord?.deviceName ?: device.name ?: "Unknown Device"
            
            val bleDevice = BleDevice(deviceName, device.address, device)
            
            val currentList = _foundDevices.value.toMutableList()
            if (!currentList.any { it.address == bleDevice.address }) {
                currentList.add(bleDevice)
                _foundDevices.value = currentList
                Log.d("BluetoothService", "Found device: $deviceName [${device.address}]")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothService", "Scan failed with error: $errorCode")
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (bleScanner == null || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BluetoothService", "Bluetooth LE Scanner is not available")
            return
        }

        _foundDevices.value = emptyList()
        _isScanning.value = true
        
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // High power, fast results
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        // Stop scanning after 15 seconds
        handler.postDelayed({
            stopScanning()
        }, 15000)

        bleScanner.startScan(null, settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (_isScanning.value) {
            bleScanner?.stopScan(scanCallback)
            _isScanning.value = false
        }
    }
}
