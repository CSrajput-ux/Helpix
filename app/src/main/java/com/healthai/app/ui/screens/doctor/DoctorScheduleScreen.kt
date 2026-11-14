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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.domain.model.Appointment
import com.healthai.app.ui.screens.dashboard.CardBorderGray
import com.healthai.app.ui.screens.dashboard.HelpixBottomNav
import java.text.SimpleDateFormat
import java.util.*

private val OffWhiteBg = Color(0xFFF8FAFC)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

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
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule Slot", tint = Color.White)
            }
        },
        containerColor = OffWhiteBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(OffWhiteBg)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .border(1.dp, CardBorderGray, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Clinic Agenda",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkCharcoalText
                        )
                        Text(
                            text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate),
                            color = SlateGrayText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                DocDateSelector(
                    days = daysInMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = if (isSameDay(selectedDate, calendar.time)) "Today's Schedule" 
                               else "Appointments for ${SimpleDateFormat("MMM d", Locale.getDefault()).format(selectedDate)}",
                        color = DarkCharcoalText,
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
                                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = SlateGrayText, modifier = Modifier.size(48.dp))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("No appointments found", color = SlateGrayText, fontSize = 14.sp)
                                        }
                                    }
                                }
                            } else {
                                items(
                                    items = filteredAppointments,
                                    key = { appointment -> appointment.id }
                                ) { appointment ->
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
        containerColor = Color.White,
        title = { Text("Add Available Slot", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Choose your free hours for today.", color = SlateGrayText, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                
                OutlinedCard(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = OffWhiteBg),
                    border = BorderStroke(1.dp, CardBorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedicalEmerald)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Start Time", color = SlateGrayText, fontSize = 10.sp)
                            Text(formatTime(startState), color = DarkCharcoalText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = OffWhiteBg),
                    border = BorderStroke(1.dp, CardBorderGray)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = MedicalEmerald)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("End Time", color = SlateGrayText, fontSize = 10.sp)
                            Text(formatTime(endState), color = DarkCharcoalText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                Text("Add Slot", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SlateGrayText) }
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
            TextButton(onClick = onDismiss) { Text("Cancel", color = SlateGrayText) }
        },
        text = { content() },
        containerColor = Color.White
    )
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
        items(
            items = days,
            key = { date -> date.time }
        ) { date ->
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
                            isToday -> MedicalEmerald.copy(alpha = 0.15f)
                            else -> Color.White
                        }
                    )
                    .border(
                        1.dp, 
                        if (isSelected || isToday) MedicalEmerald else CardBorderGray,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 14.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color.White else SlateGrayText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayNumber,
                    color = if (isSelected) Color.White else DarkCharcoalText,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE0E7FF), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = ProfessionalIndigo, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Patient: ${appointment.patientId.take(8)}...", color = DarkCharcoalText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "$formattedTime • ${appointment.reason ?: "Consultation"}", color = SlateGrayText, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SlateGrayText)
        }
    }
}

fun getDaysOfMonth(): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val days = mutableListOf<Date>()
    val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in 1..maxDays) {
        days.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

fun isSameDay(d1: Date?, d2: Date?): Boolean {
    if (d1 == null || d2 == null) return false
    val cal1 = Calendar.getInstance().apply { time = d1 }
    val cal2 = Calendar.getInstance().apply { time = d2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
