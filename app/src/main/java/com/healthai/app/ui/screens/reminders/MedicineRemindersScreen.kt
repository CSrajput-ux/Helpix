package com.healthai.app.ui.screens.reminders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.data.local.database.HelpixDatabase
import com.healthai.app.data.local.entity.Reminder
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

private val OffWhiteBg = Color(0xFFF8FAFC)
private val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineRemindersScreen(navController: NavController) {

    val context = LocalContext.current
    val db = remember { HelpixDatabase.getDatabase(context) }
    val reminders by db.reminderDao().getAllReminders().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Reminders", color = DarkCharcoalText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OffWhiteBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.AddReminder) },
                containerColor = MedicalTeal,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Color.White)
            }
        },
        containerColor = OffWhiteBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalCalendarView()

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { 
                    Text("Today's Reminders", color = DarkCharcoalText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(
                    items = reminders,
                    key = { reminder -> reminder.id }
                ) { reminder ->
                    ReminderListItem(reminder, db)
                }
            }
        }
    }
}

@Composable
fun HorizontalCalendarView() {
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf(today) }
    
    val firstDayOfMonth = today.withDayOfMonth(1)
    val daysInMonth = (1..today.lengthOfMonth()).map { day ->
        firstDayOfMonth.withDayOfMonth(day)
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (today.dayOfMonth - 3).coerceAtLeast(0))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(BorderStroke(1.dp, CardBorderGray))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + today.year,
            color = DarkCharcoalText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = daysInMonth,
                key = { date -> date.toString() }
            ) { date ->
                val isSelected = date == selectedDate
                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedDate = date }
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                        color = if (isSelected) MedicalTeal else SlateGrayText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MedicalTeal
                                    isToday -> MedicalTeal.copy(alpha = 0.15f)
                                    else -> OffWhiteBg
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected || isToday) MedicalTeal else CardBorderGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isSelected) Color.White else DarkCharcoalText,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderListItem(reminder: Reminder, db: HelpixDatabase) {
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Reminder", color = DarkCharcoalText) },
            text = { Text("Are you sure you want to delete this reminder for ${reminder.medicineName}?", color = SlateGrayText) },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        db.reminderDao().deleteReminder(reminder.id)
                        showDeleteDialog = false
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = SlateGrayText)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDeleteDialog = true }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.NotificationsActive, 
                contentDescription = null, 
                tint = MedicalTeal, 
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCCFBF1))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reminder.medicineName, color = DarkCharcoalText, fontWeight = FontWeight.Bold)
                Text("${reminder.dosage} • ${reminder.time}", color = SlateGrayText, fontSize = 12.sp)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SlateGrayText)
            }
            Checkbox(
                checked = reminder.isTaken,
                onCheckedChange = { 
                    coroutineScope.launch {
                        db.reminderDao().updateTakenStatus(reminder.id, it)
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = MedicalTeal)
            )
        }
    }
}
