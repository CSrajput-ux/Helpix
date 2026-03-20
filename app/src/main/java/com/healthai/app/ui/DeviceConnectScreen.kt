package com.healthai.app.ui

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.VitalsLog
import com.healthai.app.services.BleDevice
import com.healthai.app.services.BluetoothService
import com.healthai.app.services.ConnectionManager
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//--- DATA & VIEW MODELS ---//

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Scanning : ConnectionState()
    data class DevicesFound(val devices: List<BleDevice>) : ConnectionState()
    data class Connecting(val device: BleDevice) : ConnectionState()
    data class Connected(val device: BleDevice) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

class DeviceConnectViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val state = _state.asStateFlow()

    private val _vitals = MutableStateFlow<VitalsLog?>(null)
    val vitals = _vitals.asStateFlow()

    val bluetoothService = BluetoothService(application)
    private val connectionManager = ConnectionManager(application)
    private val userRepository = UserRepository()
    private var vitalsJob: Job? = null

    init {
        // Observe real bluetooth scanning results
        viewModelScope.launch {
            bluetoothService.foundDevices.collect { bleDevices ->
                if (_state.value is ConnectionState.Scanning || _state.value is ConnectionState.DevicesFound) {
                    if (bleDevices.isNotEmpty()) {
                        _state.value = ConnectionState.DevicesFound(bleDevices)
                    }
                }
            }
        }
        
        viewModelScope.launch {
            bluetoothService.isScanning.collect { scanning ->
                if (!scanning && _state.value is ConnectionState.Scanning && bluetoothService.foundDevices.value.isEmpty()) {
                     _state.value = ConnectionState.Error("No devices found nearby. Make sure your watch is in pairing mode.")
                }
            }
        }
    }

    fun startScan() {
        if (!bluetoothService.isBluetoothEnabled()) {
            _state.value = ConnectionState.Error("Bluetooth is disabled. Please enable it to scan.")
            return
        }
        _state.value = ConnectionState.Scanning
        bluetoothService.startScanning()
    }

    fun connectToDevice(bleDevice: BleDevice) {
        viewModelScope.launch {
            _state.value = ConnectionState.Connecting(bleDevice)
            bluetoothService.stopScanning()
            
            // Simulating connection logic
            kotlinx.coroutines.delay(2000) 
            connectionManager.saveDevice(bleDevice.name)
            _state.value = ConnectionState.Connected(bleDevice)
            startVitalsListener()
        }
    }

    fun disconnect() {
        vitalsJob?.cancel()
        connectionManager.clearDevice()
        _state.value = ConnectionState.Disconnected
    }

    private fun startVitalsListener() {
        vitalsJob = viewModelScope.launch {
            userRepository.getLatestVitalsStream().collect { vitalsLog ->
                _vitals.value = vitalsLog
            }
        }
    }
}

class DeviceConnectViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceConnectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceConnectViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

//--- MAIN SCREEN ---//

@Composable
fun DeviceConnectScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: DeviceConnectViewModel = viewModel(
        factory = DeviceConnectViewModelFactory(application)
    )
    val state by viewModel.state.collectAsState()
    val vitals by viewModel.vitals.collectAsState()

    val bluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (viewModel.bluetoothService.isBluetoothEnabled()) {
            viewModel.startScan()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1221)),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is ConnectionState.Disconnected -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { 
                            if (!viewModel.bluetoothService.isBluetoothEnabled()) {
                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                bluetoothLauncher.launch(enableBtIntent)
                            } else {
                                viewModel.startScan() 
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Search for Watch", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            is ConnectionState.Scanning -> {
                RadarScanningView()
            }
            is ConnectionState.DevicesFound -> {
                DeviceSelectionScreen(devices = currentState.devices) { bleDevice ->
                    viewModel.connectToDevice(bleDevice)
                }
            }
            is ConnectionState.Connecting -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pairing with ${currentState.device.name}...",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            is ConnectionState.Connected -> {
                vitals?.let {
                    VitalsDashboard(navController = navController, deviceName = currentState.device.name, vitals = it) {
                        viewModel.disconnect()
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            CircularProgressIndicator(color = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Waiting for data from ${currentState.device.name}...", color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
            is ConnectionState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Text("Oops!", color = Color(0xFFFF5252), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentState.message, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { 
                            if (!viewModel.bluetoothService.isBluetoothEnabled()) {
                                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                bluetoothLauncher.launch(enableBtIntent)
                            } else {
                                viewModel.startScan() 
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                    ) {
                        Text("Try Again", color = Color.Black)
                    }
                }
            }
        }
    }
}

//--- UI COMPONENTS ---//

@Composable
fun RadarScanningView() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseAnim1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse1"
    )
    val pulseAnim2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulse2"
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(300.dp)
        ) {
            val canvasSize = size.minDimension
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = (1 - pulseAnim1)),
                radius = canvasSize * 0.5f * pulseAnim1,
                style = Stroke(width = 4.dp.toPx() * (1 - pulseAnim1))
            )
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = (1 - pulseAnim2)),
                radius = canvasSize * 0.5f * pulseAnim2,
                style = Stroke(width = 4.dp.toPx() * (1 - pulseAnim2))
            )
        }
        Text("Scanning...", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DeviceSelectionScreen(devices: List<BleDevice>, onDeviceClick: (BleDevice) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Select Your Watch",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(devices) { device ->
                BleDeviceListItem(device, onDeviceClick)
            }
        }
    }
}

@Composable
fun BleDeviceListItem(bleDevice: BleDevice, onDeviceClick: (BleDevice) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClick(bleDevice) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A293D))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF00E5FF), CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(bleDevice.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(bleDevice.address, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun VitalsDashboard(
    navController: NavController,
    deviceName: String,
    vitals: VitalsLog,
    onDisconnect: () -> Unit
) {
    val heartBeatAnim by rememberInfiniteTransition(label = "heartbeat").animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "heartbeat_anim"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Paired with $deviceName", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Heart Rate
        VitalsCard(modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = "Heart Rate",
                tint = Color.Red,
                modifier = Modifier
                    .size(60.dp)
                    .scale(heartBeatAnim)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text("Heart Rate", color = Color.Gray, fontSize = 16.sp)
                Text("${vitals.heartRate} bpm", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Steps
            VitalsCard(modifier = Modifier.weight(1f)) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = vitals.steps / 10000f,
                        modifier = Modifier.size(80.dp),
                        color = Color(0xFF00E5FF),
                        strokeWidth = 6.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${vitals.steps}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Steps", color = Color.Gray)
                    }
                }
            }
            // Calories
            VitalsCard(modifier = Modifier.weight(1f)) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.height(80.dp)) {
                     Text("Calories", color = Color.Gray, fontSize = 16.sp)
                     Text("320 kcal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                 }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Spo2
        VitalsCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                Text("Oxygen Saturation (SpO2)", color = Color.Gray, fontSize = 16.sp)
                Text("${vitals.spo2}%", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(NavRoutes.HealthHistory) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("View Detailed History", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onDisconnect
        ) {
            Text("Unpair Device", color = Color(0xFFFF5252))
        }
    }
}

@Composable
fun VitalsCard(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A293D))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}