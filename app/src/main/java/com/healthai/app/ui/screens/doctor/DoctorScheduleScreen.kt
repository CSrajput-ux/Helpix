package com.healthai.app.ui.screens.doctor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: DoctorDashboardViewModel = viewModel()
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
                containerColor = colorResource(id = R.color.logo_cyan),
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 80.dp)
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
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                DateSelector(
                    days = daysInMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
                        val filteredAppointments = appointments.filter { 
                            isSameDay(it.appointmentDate, selectedDate)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            item {
                                Text(
                                    if (isSameDay(selectedDate, calendar.time)) "Today's Agenda" else "Agenda for ${SimpleDateFormat("MMM d", Locale.getDefault()).format(selectedDate)}",
                                    color = colorResource(id = R.color.logo_cyan),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            
                            if (filteredAppointments.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                        Text("No appointments for this day", color = Color.Gray, fontSize = 14.sp)
                                    }
                                }
                            } else {
                                items(filteredAppointments) { appointment ->
                                    DoctorScheduleCard(appointment)
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

@Composable
fun DateSelector(
    days: List<Date>,
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { date ->
            val isSelected = isSameDay(date, selectedDate)
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(date)
            val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(date)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        if (isSelected) colorResource(id = R.color.logo_cyan) 
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .clickable { onDateSelected(date) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayNumber,
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getDaysOfMonth(): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val month = calendar.get(Calendar.MONTH)
    val days = mutableListOf<Date>()
    while (calendar.get(Calendar.MONTH) == month) {
        days.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    repeat(14) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSlotDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    val currentTime = Calendar.getInstance()
    
    // Start Time State
    val startState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false
    )
    var showStartTimePicker by remember { mutableStateOf(false) }
    
    // End Time State
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
        containerColor = Color(0xFF1E293B),
        title = { Text("Add Available Slot", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Choose your free hours for today.", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                
                // Start Time Trigger
                OutlinedCard(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = colorResource(id = R.color.logo_cyan))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Start Time", color = Color.Gray, fontSize = 10.sp)
                            Text(formatTime(startState), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // End Time Trigger
                OutlinedCard(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = colorResource(id = R.color.logo_cyan))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("End Time", color = Color.Gray, fontSize = 10.sp)
                            Text(formatTime(endState), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(formatTime(startState), formatTime(endState)) },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan))
            ) {
                Text("Add Slot", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
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
            TextButton(onClick = onConfirm) { Text("OK", color = colorResource(id = R.color.logo_cyan)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        },
        text = { content() },
        containerColor = Color(0xFF1E293B)
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
