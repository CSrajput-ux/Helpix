package com.healthai.app.ui.screens.reminders

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
import com.healthai.app.data.local.AppDatabase
import com.healthai.app.data.local.Reminder
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineRemindersScreen(navController: NavController) {

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val reminders by db.reminderDao().getAllReminders().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Reminders") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.AddReminder) },
                containerColor = Color(0xFFFF4081)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Color.White)
            }
        },
        containerColor = Color(0xFF0B1221)
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
                    Text("Today's Reminders", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(reminders) { reminder ->
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
    
    // Get all days of the current month
    val firstDayOfMonth = today.withDayOfMonth(1)
    val daysInMonth = (1..today.lengthOfMonth()).map { day ->
        firstDayOfMonth.withDayOfMonth(day)
    }

    // Initial scroll position near today's date
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (today.dayOfMonth - 3).coerceAtLeast(0))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
            .padding(vertical = 16.dp)
    ) {
        // Month Year Header
        Text(
            text = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + today.year,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(daysInMonth) { date ->
                val isSelected = date == selectedDate
                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedDate = date }
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                        color = if (isSelected) Color(0xFFFF4081) else Color.Gray,
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
                                    isSelected -> Color(0xFFFF4081)
                                    isToday -> Color(0xFFFF4081).copy(alpha = 0.15f)
                                    else -> Color(0xFF1E293B)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected || isToday) Color(0xFFFF4081) else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = Color.White,
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
fun ReminderListItem(reminder: Reminder, db: AppDatabase) {
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Reminder") },
            text = { Text("Are you sure you want to delete this reminder for ${reminder.medicineName}?") },
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
                    Text("Cancel")
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.NotificationsActive, 
                contentDescription = null, 
                tint = Color(0xFFFF4081), 
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4081).copy(alpha = 0.1f))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reminder.medicineName, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${reminder.dosage} • ${reminder.time}", color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
            Checkbox(
                checked = reminder.isTaken,
                onCheckedChange = { 
                    coroutineScope.launch {
                        db.reminderDao().updateTakenStatus(reminder.id, it)
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF4081))
            )
        }
    }
}
