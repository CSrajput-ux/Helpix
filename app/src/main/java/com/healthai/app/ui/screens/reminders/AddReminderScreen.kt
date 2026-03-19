package com.healthai.app.ui.screens.reminders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.healthai.app.data.local.AppDatabase
import com.healthai.app.data.local.Reminder
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(navController: NavController) {

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("Daily") }
    var time by remember { mutableStateOf("09:00 AM") }
    var startDate by remember { mutableStateOf("25/07/2024") }
    var endDate by remember { mutableStateOf("30/07/2024") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Reminder") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF4081),
                    focusedBorderColor = Color(0xFFFF4081),
                    unfocusedBorderColor = Color(0xFF334155)
                )
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g., 1 tablet, 2 spoons)") },
                modifier = Modifier.fillMaxWidth(),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF4081),
                    focusedBorderColor = Color(0xFFFF4081),
                    unfocusedBorderColor = Color(0xFF334155)
                )
            )

            ScheduleDropDown(selected = schedule, onSelected = { schedule = it })
            
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { /* TODO: Show Time Picker Dialog */ },
                trailingIcon = { Icon(Icons.Default.Schedule, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF4081),
                    focusedBorderColor = Color(0xFFFF4081),
                    unfocusedBorderColor = Color(0xFF334155)
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    readOnly = true,
                    modifier = Modifier.weight(1f).clickable { /* TODO: Show Date Picker Dialog */ },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFF4081),
                        focusedBorderColor = Color(0xFFFF4081),
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
                 OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date") },
                    readOnly = true,
                    modifier = Modifier.weight(1f).clickable { /* TODO: Show Date Picker Dialog */ },
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFF4081),
                        focusedBorderColor = Color(0xFFFF4081),
                        unfocusedBorderColor = Color(0xFF334155)
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    coroutineScope.launch {
                        val newReminder = Reminder(
                            medicineName = medicineName,
                            dosage = dosage,
                            time = time,
                            schedule = schedule,
                            startDate = startDate,
                            endDate = endDate
                        )
                        db.reminderDao().insertReminder(newReminder)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                enabled = medicineName.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Save Reminder", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDropDown(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("Daily", "Every other day", "Once a week")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Schedule") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFFFF4081),
                focusedBorderColor = Color(0xFFFF4081),
                unfocusedBorderColor = Color(0xFF334155)
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { 
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}