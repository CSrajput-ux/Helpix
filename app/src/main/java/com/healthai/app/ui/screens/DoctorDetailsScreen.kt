package com.healthai.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Dynamic Date Generation
    val today = LocalDate.now()
    val next7Days = (0..6).map { today.plusDays(it.toLong()) }
    
    var selectedDate by remember { mutableStateOf(today) }
    var selectedTimeSlot by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Details", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                // 1. Doctor Profile Header
                DoctorProfileHeader()
                
                Spacer(modifier = Modifier.height(24.dp))

                // 2. Stats Row
                DoctorStatsRow()

                Spacer(modifier = Modifier.height(24.dp))

                // 3. About Section
                Text("About Doctor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dr. Albert Flores is a highly experienced Surgeon at St. Mary's Hospital. He has successfully performed over 1000+ surgeries and is known for his precision and patient care. He specializes in minimally invasive surgical techniques.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Date Selection
                Text("Select Date", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(next7Days) { _, date ->
                        val isSelected = date == selectedDate
                        DateCard(
                            day = date.format(DateTimeFormatter.ofPattern("EEE")),
                            date = date.format(DateTimeFormatter.ofPattern("dd MMM")),
                            isSelected = isSelected,
                            onClick = { selectedDate = date }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Time Slots
                Text("Available Time Slots", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                TimeSlotGrid(selectedTimeSlot) { selectedTimeSlot = it }
                
                Spacer(modifier = Modifier.height(100.dp))
            }

            // 6. Footer Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF0F172A))
                        )
                    )
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { 
                        if (selectedTimeSlot.isEmpty()) {
                            Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Appointment Booked for ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM"))} at $selectedTimeSlot", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Book Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun DoctorProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f))
                .border(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(50.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Dr. Albert Flores", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text("Senior Surgeon", color = colorResource(id = R.color.logo_cyan), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("St. Mary's Hospital, London", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun DoctorStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatItem("Experience", "10 Years", Icons.Default.WorkHistory, colorResource(id = R.color.logo_cyan))
        StatItem("Patients", "2.5K+", Icons.Default.Groups, Color(0xFF00E676))
        StatItem("Reviews", "1.2K+", Icons.Default.Star, Color(0xFFFFC107))
    }
}

@Composable
fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
fun DateCard(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(70.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) colorResource(id = R.color.logo_cyan) else Color(0xFF1E293B))
            .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day, color = if (isSelected) Color.Black else Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(date, color = if (isSelected) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotGrid(selectedSlot: String, onSlotSelected: (String) -> Unit) {
    val morningSlots = listOf("09:00 AM", "10:00 AM", "11:00 AM")
    val afternoonSlots = listOf("01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM")
    
    Column {
        Text("Morning", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            morningSlots.forEach { time ->
                TimeSlotChip(time, time == selectedSlot) { onSlotSelected(time) }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Afternoon", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            afternoonSlots.forEach { time ->
                TimeSlotChip(time, time == selectedSlot) { onSlotSelected(time) }
            }
        }
    }
}

@Composable
fun TimeSlotChip(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colorResource(id = R.color.logo_cyan) else Color.Transparent)
            .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(time, color = if (isSelected) Color.Black else Color.White, fontSize = 14.sp)
    }
}
