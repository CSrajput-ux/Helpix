package com.healthai.app.ui

import android.app.Application
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.VitalsLog
import com.healthai.app.services.ConnectionManager
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

//--- DATA & VIEW MODELS ---//

data class Device(val name: String)

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Scanning : ConnectionState()
    data class DevicesFound(val devices: List<Device>) : ConnectionState()
    data class Connecting(val device: Device) : ConnectionState()
    data class Connected(val device: Device) : ConnectionState()
}

class DeviceConnectViewModel(application: Application) : ViewModel() {
    private val _state = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val state = _state.asStateFlow()

    private val _vitals = MutableStateFlow<VitalsLog?>(null)
    val vitals = _vitals.asStateFlow()

    private val connectionManager = ConnectionManager(application)
    private val userRepository = UserRepository()
    private var vitalsJob: Job? = null

    private val _simulatedDevices = listOf(
        Device("Helpix Watch Pro"),
        Device("Galaxy Watch 6"),
        Device("Mi Band 7")
    )

    init {
        val connectedDevice = connectionManager.getConnectedDevice()
        if (connectedDevice != null) {
            _state.value = ConnectionState.Connected(Device(connectedDevice))
            startVitalsListener()
        }
    }

    fun startScan() {
        viewModelScope.launch {
            _state.value = ConnectionState.Scanning
            kotlinx.coroutines.delay(2000) // Simulate scanning for 2 seconds
            _state.value = ConnectionState.DevicesFound(_simulatedDevices)
        }
    }

    fun connectToDevice(device: Device) {
        viewModelScope.launch {
            _state.value = ConnectionState.Connecting(device)
            kotlinx.coroutines.delay(1500) // Simulate connecting for 1.5 seconds
            connectionManager.saveDevice(device.name)
            _state.value = ConnectionState.Connected(device)
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
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
    val viewModel: DeviceConnectViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = DeviceConnectViewModelFactory(application)
    )
    val state by viewModel.state.collectAsState()
    val vitals by viewModel.vitals.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1221)),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is ConnectionState.Disconnected -> {
                Button(
                    onClick = { viewModel.startScan() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Scan for Smartwatch", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            is ConnectionState.Scanning -> {
                RadarScanningView()
            }
            is ConnectionState.DevicesFound -> {
                DeviceSelectionScreen(devices = currentState.devices) { device ->
                    viewModel.connectToDevice(device)
                }
            }
            is ConnectionState.Connecting -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Connecting to ${currentState.device.name}...",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            is ConnectionState.Connected -> {
                vitals?.let {
                    VitalsDashboard(navController = navController, device = currentState.device, vitals = it) {
                        viewModel.disconnect()
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No vitals data found.", color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Please make sure your smartwatch is connected and sending data.", color = Color.Gray, modifier = Modifier.padding(horizontal = 32.dp))
                        }
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
fun DeviceSelectionScreen(devices: List<Device>, onDeviceClick: (Device) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Devices Found",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(devices) { device ->
                DeviceListItem(device, onDeviceClick)
            }
        }
    }
}

@Composable
fun DeviceListItem(device: Device, onDeviceClick: (Device) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDeviceClick(device) },
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
            Text(device.name, color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun VitalsDashboard(
    navController: NavController,
    device: Device,
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
        Text("Connected to ${device.name}", color = Color.Gray)
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
                     Text("320 kcal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold) // Calories are not in VitalsLog, so this is still a dummy value
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(NavRoutes.HealthHistory) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
        ) {
            Text("View History", color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDisconnect,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Disconnect", color = Color.White)
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