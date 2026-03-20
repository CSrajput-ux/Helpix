package com.healthai.app.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
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

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name ?: "Unknown Device"
            val bleDevice = BleDevice(deviceName, device.address, device)
            
            val currentList = _foundDevices.value.toMutableList()
            if (!currentList.any { it.address == bleDevice.address }) {
                currentList.add(bleDevice)
                _foundDevices.value = currentList
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothService", "Scan failed with error: $errorCode")
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BluetoothService", "Bluetooth is disabled or not supported")
            return
        }

        _foundDevices.value = emptyList()
        _isScanning.value = true
        
        // Stop scanning after 10 seconds
        handler.postDelayed({
            stopScanning()
        }, 10000)

        bleScanner?.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (_isScanning.value) {
            bleScanner?.stopScan(scanCallback)
            _isScanning.value = false
        }
    }
}