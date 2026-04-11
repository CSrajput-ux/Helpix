package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.domain.model.Appointment
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScheduleScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = viewModel()
) {
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddSlotDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAppointments()
    }

    Scaffold(
        bottomBar = {
            HelpixBottomNav(navController = navController, userType = "DOCTOR")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSlotDialog = true },
                containerColor = colorResource(id = R.color.logo_cyan),
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp) // Offset for bottom nav
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule Slot")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.login_bg_top),
                            colorResource(id = R.color.login_bg_bottom)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "My Schedule",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Manage your patient appointments and availability",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScheduleFilterChip("All", true)
                    ScheduleFilterChip("Pending", false)
                    ScheduleFilterChip("Confirmed", false)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            Text(
                                "Today's Agenda",
                                color = colorResource(id = R.color.logo_cyan),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(appointments) { appointment ->
                            DoctorScheduleCard(appointment)
                        }
                    }
                }
            }

            if (showAddSlotDialog) {
                AddSlotDialog(
                    onDismiss = { showAddSlotDialog = false },
                    onConfirm = { startTime, endTime ->
                        // Logic to save availability to backend
                        showAddSlotDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSlotDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("10:00 AM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = {
            Text("Add Available Slot", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("Set your free hours for today. Patients will see these as bookable slots.", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = colorResource(id = R.color.logo_cyan)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.logo_cyan),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = colorResource(id = R.color.logo_cyan)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.logo_cyan),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(startTime, endTime) },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan))
            ) {
                Text("Add Slot", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
fun ScheduleFilterChip(label: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) colorResource(id = R.color.logo_cyan) else Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DoctorScheduleCard(appointment: Appointment) {
    val formattedDate = appointment.appointmentDate?.let { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(it) } ?: ""
    val formattedTime = appointment.appointmentDate?.let { SimpleDateFormat("h:mm a", Locale.getDefault()).format(it) } ?: ""
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.helpix_bg_top).copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formattedTime, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text("Today", color = colorResource(id = R.color.logo_cyan), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(20.dp))
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.White.copy(alpha = 0.1f)))
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Patient: ${appointment.patientId}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(formattedDate, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when(appointment.status) {
                        "COMPLETED" -> Color(0xFF00E676).copy(alpha = 0.1f)
                        "SCHEDULED" -> Color(0xFF2979FF).copy(alpha = 0.1f)
                        else -> Color(0xFFFFAB00).copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = appointment.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when(appointment.status) {
                            "COMPLETED" -> Color(0xFF00E676)
                            "SCHEDULED" -> Color(0xFF2979FF)
                            else -> Color(0xFFFFAB00)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(onClick = { }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}
