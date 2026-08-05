package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.domain.model.Appointment
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorScheduleScreen(
    navController: NavController,
    viewModel: DoctorDashboardViewModel = hiltViewModel()
) {
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddSlotDialog by remember { mutableStateOf(false) }
    
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    val daysInMonth = remember { getDaysOfMonth() }

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
                containerColor = MedicalEmerald,
                contentColor = DocDeepSlate,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule Slot")
            }
        },
        containerColor = DocDeepSlate
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DocScheduleGridBackground()

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Custom Top Bar
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(DocCardBg, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Clinic Agenda",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate),
                            color = DocTextGrey,
                            fontSize = 14.sp
                        )
                    }
                }

                // Aesthetic Date Selector
                DocDateSelector(
                    days = daysInMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = if (isSameDay(selectedDate, calendar.time)) "Today's Schedule" 
                               else "Appointments for ${SimpleDateFormat("MMM d", Locale.getDefault()).format(selectedDate)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MedicalEmerald)
                        }
                    } else {
                        val filteredAppointments = appointments.filter { 
                            isSameDay(it.appointmentDate, selectedDate)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            if (filteredAppointments.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 60.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = DocTextGrey, modifier = Modifier.size(48.dp))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("No appointments found", color = DocTextGrey, fontSize = 14.sp)
                                        }
                                    }
                                }
                            } else {
                                items(filteredAppointments) { appointment ->
                                    AestheticScheduleCard(appointment)
                                }
                            }
                        }
                    }
                }
            }

            if (showAddSlotDialog) {
                AddSlotDialog(
                    onDismiss = { showAddSlotDialog = false },
                    onConfirm = { _, _ -> showAddSlotDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSlotDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    val currentTime = Calendar.getInstance()
    
    val startState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false
    )
    var showStartTimePicker by remember { mutableStateOf(false) }
    
    val endState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY) + 1,
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false
    )
    var showEndTimePicker by remember { mutableStateOf(false) }

    fun formatTime(state: TimePickerState): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, state.hour)
        cal.set(Calendar.MINUTE, state.minute)
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DocCardBg,
        title = { Text("Add Available Slot", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Choose your free hours for today.", color = DocTextGrey, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedCard(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = DocDeepSlate),
                    border = BorderStroke(1.dp, MedicalEmerald.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedicalEmerald)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Start Time", color = DocTextGrey, fontSize = 10.sp)
                            Text(formatTime(startState), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = DocDeepSlate),
                    border = BorderStroke(1.dp, MedicalEmerald.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedicalEmerald)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("End Time", color = DocTextGrey, fontSize = 10.sp)
                            Text(formatTime(endState), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(formatTime(startState), formatTime(endState)) },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalEmerald)
            ) {
                Text("Add Slot", color = DocDeepSlate, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DocTextGrey) }
        }
    )

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { showStartTimePicker = false }
        ) {
            TimePicker(state = startState)
        }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { showEndTimePicker = false }
        ) {
            TimePicker(state = endState)
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("OK", color = MedicalEmerald) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DocTextGrey) }
        },
        text = { content() },
        containerColor = DocCardBg
    )
}

@Composable
fun DocScheduleGridBackground() {
    val color = Color.White.copy(alpha = 0.03f)
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 40.dp.toPx()
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(color, start = androidx.compose.ui.geometry.Offset(x.toFloat(), 0f), end = androidx.compose.ui.geometry.Offset(x.toFloat(), size.height))
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, y.toFloat()), end = androidx.compose.ui.geometry.Offset(size.width, y.toFloat()))
        }
    }
}

@Composable
fun DocDateSelector(
    days: List<Date>,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(days) { date ->
            val isSelected = isSameDay(date, selectedDate)
            val isToday = isSameDay(date, Calendar.getInstance().time)
            
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
            val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(date)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(65.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when {
                            isSelected -> MedicalEmerald
                            isToday -> MedicalEmerald.copy(alpha = 0.1f)
                            else -> DocCardBg
                        }
                    )
                    .border(
                        1.dp, 
                        if (isSelected) MedicalEmerald else if (isToday) MedicalEmerald.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) DocDeepSlate else DocTextGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayNumber,
                    color = if (isSelected) DocDeepSlate else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun AestheticScheduleCard(appointment: Appointment) {
    val formattedTime = appointment.appointmentDate?.let { SimpleDateFormat("h:mm a", Locale.getDefault()).format(it) } ?: ""
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DocCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(ProfessionalIndigo.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTime.split(" ")[0],
                    color = ProfessionalIndigo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Patient ID: ${appointment.patientId.takeLast(6).uppercase()}",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                when(appointment.status) {
                                    "COMPLETED" -> Color(0xFF00E676)
                                    "SCHEDULED" -> ProfessionalIndigo
                                    else -> Color(0xFFFFAB00)
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = appointment.status,
                        color = DocTextGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            IconButton(
                onClick = { },
                modifier = Modifier.background(DocDeepSlate, CircleShape).size(32.dp)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun getDaysOfMonth(): List<Date> {
    val calendar = Calendar.getInstance()
    // Start from today
    val days = mutableListOf<Date>()
    repeat(30) {
        days.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

private fun isSameDay(date1: Date?, date2: Date?): Boolean {
    if (date1 == null || date2 == null) return false
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
