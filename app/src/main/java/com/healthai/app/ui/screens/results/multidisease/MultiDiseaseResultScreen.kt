package com.healthai.app.ui.screens.results.multidisease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DiseaseClusterResult(val name: String, val risk: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiDiseaseResultScreen(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Health Scan Report") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("No scan results available", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "This guided scan has not produced diagnostic data. Use the symptom checker or consult a doctor.",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate(com.healthai.app.ui.navigation.NavRoutes.Tools) }) {
                        Text("Open symptom checker")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { navController.navigate(com.healthai.app.ui.navigation.NavRoutes.Doctors) }) {
                        Text("Find a Doctor")
                    }
                }
        }
    }
}

@Composable
fun ClusterResultCard(result: DiseaseClusterResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(result.color, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.name, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Text(result.risk, color = result.color, fontWeight = FontWeight.Bold)
        }
    }
}
