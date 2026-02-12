package com.healthai.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R

// Define Colors
val DoctorThemeColor = Color(0xFF00C853)
val LightGrayBG = Color(0xFFF5F5F5)
val TextGray = Color(0xFF9E9E9E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var selectedDateIndex by remember { mutableIntStateOf(1) }
    var selectedTimeSlot by remember { mutableStateOf("10:00 AM") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Doctor Details", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                // 1. Doctor Profile
                DoctorProfileCard()
                Spacer(modifier = Modifier.height(20.dp))

                // 2. About
                AboutDoctorSection()
                Spacer(modifier = Modifier.height(20.dp))

                // 3. Schedule
                SchedulesSection(
                    selectedDateIndex = selectedDateIndex,
                    onDateSelected = { selectedDateIndex = it },
                    selectedTimeSlot = selectedTimeSlot,
                    onTimeSlotSelected = { selectedTimeSlot = it }
                )
                Spacer(modifier = Modifier.height(100.dp))
            }

            // 4. Button
            Button(
                onClick = { /* Handle Booking */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DoctorThemeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Book Appointment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun DoctorProfileCard() {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(LightGrayBG)) {
            // Replace with your image or keep commented for placeholder
            // Image(painter = painterResource(id = R.drawable.doctor_image), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dr. Albert Flores", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = DoctorThemeColor, modifier = Modifier.size(20.dp))
            }
            Text("Surgeon", color = TextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("St. Mary's Hospital", color = TextGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Text(" 4.8 (4,278 reviews)", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AboutDoctorSection() {
    Column {
        Text("About Doctor", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append("Dr. Albert Flores is one of the best surgeons at St. Mary's Hospital... ")
                withStyle(style = SpanStyle(color = DoctorThemeColor, fontWeight = FontWeight.Bold)) { append("Read more") }
            },
            color = TextGray, fontSize = 14.sp, lineHeight = 20.sp
        )
    }
}

@Composable
fun SchedulesSection(selectedDateIndex: Int, onDateSelected: (Int) -> Unit, selectedTimeSlot: String, onTimeSlotSelected: (String) -> Unit) {
    Column {
        Text("Schedules", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        val dates = listOf("Sun" to "Aug 22", "Mon" to "Aug 23", "Tue" to "Aug 24", "Wed" to "Aug 25", "Thu" to "Aug 26")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(dates) { index, (day, date) ->
                DateSlotCard(day, date, index == selectedDateIndex) { onDateSelected(index) }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        TimeSlotGrid(selectedTimeSlot, onTimeSlotSelected)
    }
}

@Composable
fun DateSlotCard(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(70.dp).height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) DoctorThemeColor else Color.Transparent)
            .border(if (isSelected) 0.dp else 1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(day, color = if (isSelected) Color.White else TextGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(date, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotGrid(selectedSlot: String, onSlotSelected: (String) -> Unit) {
    val times = listOf("09:00 AM", "10:00 AM", "11:00 AM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM")
    Column {
        Text("Available Time", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            times.forEach { time ->
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
            .background(if (isSelected) DoctorThemeColor else Color.Transparent)
            .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(time, color = if (isSelected) Color.White else Color.Black, fontSize = 14.sp)
    }
}