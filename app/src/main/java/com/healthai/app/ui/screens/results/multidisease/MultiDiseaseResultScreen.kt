package com.healthai.app.ui.screens.results.multidisease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    // Dummy data for preview
    val results = listOf(
        DiseaseClusterResult("Heart Health", "Potential Risk", Color.Yellow),
        DiseaseClusterResult("Lung Health", "Looks Good", Color.Green),
        DiseaseClusterResult("Skin Conditions", "Observation Found", Color.Yellow),
        DiseaseClusterResult("Diabetes Risk", "Low Risk", Color.Green)
    )

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
                // Overall Risk Score Gauge (Placeholder)
                Text("Overall Risk Score", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Medium", color = Color.Yellow, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // AI Explanation
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("AI Explanation", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Aapke diye gaye data ke anusaar, aapka risk score Medium hai. Yeh koi medical nidaan nahi hai. Yeh sirf ek AI-aaklan hai taaki aap apni sehat par dhyan de sakein.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Disease Clusters", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(results) { result ->
                ClusterResultCard(result)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("nearby_places") }) {
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
