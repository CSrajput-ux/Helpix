package com.healthai.app.ui.screens.reminders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.healthai.app.data.local.database.HelpixDatabase
import com.healthai.app.data.local.entity.Reminder
import com.healthai.app.utils.ReminderScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

private val OffWhiteBg = Color(0xFFF8FAFC)
private val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(navController: NavController) {

    val context = LocalContext.current
    val db = remember { HelpixDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("Daily") }
    
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    var time by remember { mutableStateOf(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)) }
    var startDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)) }
    
    val endCalendar = Calendar.getInstance()
    endCalendar.add(Calendar.DAY_OF_YEAR, 7)
    var endDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(endCalendar.time)) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m -> time = String.format(Locale.getDefault(), "%02d:%02d", h, m) },
        hour,
        minute,
        true
    )

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val datePicker = DatePickerDialog(
            context,
            { _, y, m, d ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(y, m, d)
                onDateSelected(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Reminder", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhiteBg)
            )
        },
        containerColor = OffWhiteBg
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
                label = { Text("Medicine Name", color = SlateGrayText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkCharcoalText,
                    unfocusedTextColor = DarkCharcoalText,
                    cursorColor = MedicalTeal,
                    focusedBorderColor = MedicalTeal,
                    unfocusedBorderColor = CardBorderGray
                )
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g., 1 tablet, 2 spoons)", color = SlateGrayText) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DarkCharcoalText,
                    unfocusedTextColor = DarkCharcoalText,
                    cursorColor = MedicalTeal,
                    focusedBorderColor = MedicalTeal,
                    unfocusedBorderColor = CardBorderGray
                )
            )

            ScheduleDropDown(selected = schedule, onSelected = { schedule = it })
            
            OutlinedTextField(
                value = time,
                onValueChange = { },
                label = { Text("Time", color = SlateGrayText) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { timePickerDialog.show() },
                enabled = false,
                trailingIcon = { Icon(Icons.Default.Schedule, null, tint = DarkCharcoalText) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = DarkCharcoalText,
                    disabledBorderColor = CardBorderGray,
                    disabledLabelColor = SlateGrayText,
                    disabledTrailingIconColor = DarkCharcoalText
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
                    label = { Text("Start Date", color = SlateGrayText) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.weight(1f).clickable { showDatePicker { startDate = it } },
                    trailingIcon = { Icon(Icons.Default.DateRange, null, tint = DarkCharcoalText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = DarkCharcoalText,
                        disabledBorderColor = CardBorderGray,
                        disabledLabelColor = SlateGrayText,
                        disabledTrailingIconColor = DarkCharcoalText
                    )
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    label = { Text("End Date", color = SlateGrayText) },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.weight(1f).clickable { showDatePicker { endDate = it } },
                    trailingIcon = { Icon(Icons.Default.DateRange, null, tint = DarkCharcoalText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = DarkCharcoalText,
                        disabledBorderColor = CardBorderGray,
                        disabledLabelColor = SlateGrayText,
                        disabledTrailingIconColor = DarkCharcoalText
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
                        val id = db.reminderDao().insertReminder(newReminder)
                        ReminderScheduler.scheduleReminder(context, newReminder.copy(id = id.toInt()))
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal),
                enabled = medicineName.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Save Reminder", color = Color.White, fontWeight = FontWeight.Bold)
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
            label = { Text("Schedule", color = SlateGrayText) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = DarkCharcoalText) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DarkCharcoalText,
                unfocusedTextColor = DarkCharcoalText,
                cursorColor = MedicalTeal,
                focusedBorderColor = MedicalTeal,
                unfocusedBorderColor = CardBorderGray
            )
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option, color = DarkCharcoalText) }, onClick = { 
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}
