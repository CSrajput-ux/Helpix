package com.healthai.app.ui.screens.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes

data class HealthRecord(val name: String, val date: String, val type: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthVaultScreen(navController: NavController) {

    // Dummy data for preview
    val records = listOf(
        HealthRecord("July Blood Sugar Report", "15 July 2024", "Blood Report"),
        HealthRecord("Dr. Sharma Prescription", "12 July 2024", "Prescription"),
        HealthRecord("Chest X-Ray", "05 June 2024", "X-Ray")
    )

    // In a real app, you would have a biometric check here before showing the content.
    // For now, we will assume the check has passed.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Records Vault") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Icon(Icons.Default.Lock, contentDescription = "Secured", tint = Color(0xFFAA00FF))
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.AddRecord) },
                containerColor = Color(0xFFAA00FF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record", tint = Color.White)
            }
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // TODO: Add Folder View / Timeline View toggle
            Text("All Records (Timeline View)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(records) { record ->
                    RecordListItem(record)
                }
            }
        }
    }
}

@Composable
fun RecordListItem(record: HealthRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFAA00FF), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(record.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${record.date} • ${record.type}", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}