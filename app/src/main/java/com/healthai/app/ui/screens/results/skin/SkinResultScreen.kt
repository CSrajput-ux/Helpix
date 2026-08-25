package com.healthai.app.ui.screens.results.skin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.healthai.app.data.local.database.HelpixDatabase
import com.healthai.app.data.local.entity.SkinScanEntity
import com.healthai.app.ui.navigation.NavRoutes

data class SimilarityResult(val name: String, val similarity: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinResultScreen(navController: NavController, scanId: Int) {
    val context = LocalContext.current
    var scanResult by remember { mutableStateOf<SkinScanEntity?>(null) }

    LaunchedEffect(scanId) {
        if (scanId != -1) {
            scanResult = HelpixDatabase.getDatabase(context).skinScanDao().getScanById(scanId)
        }
    }

    val diseaseName = scanResult?.diseaseName ?: "Loading..."
    val confidence = scanResult?.confidence ?: 0f

    val top3 = scanResult?.topPredictions?.split(",")?.mapNotNull {
        val parts = it.split(":")
        if (parts.size == 2) {
            SimilarityResult(parts[0], parts[1].toFloatOrNull() ?: 0f)
        } else null
    } ?: emptyList()

    val flagText: String
    val flagColor: Color

    // Determine warning levels based on confidence and disease type
    when {
        scanResult?.isUncertain == true -> {
            flagText = "Low Confidence: Consultation Advised"
            flagColor = Color.Yellow
        }
        diseaseName.contains("Malignant", ignoreCase = true) ||
            diseaseName.contains("Cancer", ignoreCase = true) ||
            diseaseName.contains("Melanoma", ignoreCase = true) ||
            diseaseName.contains("melanoma", ignoreCase = true) ||
            diseaseName.contains("basal_cell_carcinoma", ignoreCase = true) ||
            diseaseName.contains("actinic_keratoses", ignoreCase = true) -> {
            flagText = "Urgent Medical Attention Recommended"
            flagColor = Color.Red
        }
        else -> {
            flagText = "AI Analysis Complete"
            flagColor = Color.Green
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skin Analysis Report", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221))
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                if (scanResult?.imagePath != null) {
                    AsyncImage(
                        model = scanResult?.imagePath,
                        contentDescription = "Analyzed Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Analyzed Image", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = flagColor),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = flagText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (top3.isNotEmpty()) {
                Text("AI Analysis Results (Top Matches)", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                top3.forEach {
                    SimilarityItem(it)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Text("AI Analysis Results", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The AI detected patterns most consistent with: $diseaseName",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                SimilarityItem(SimilarityResult(diseaseName, confidence))
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "DISCLAIMER: This AI analysis is for informational purposes only. Consult a doctor for a professional diagnosis.",
                color = Color.Red.copy(alpha = 0.7f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ✅ Bug #2 Fixed — Doctors screen pe navigate karta hai ab
            Button(
                onClick = { navController.navigate(NavRoutes.Doctors) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
            ) {
                Text("Consult a Skin Specialist", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SimilarityItem(result: SimilarityResult) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(result.name, color = Color.White, fontSize = 14.sp)
            Text("${(result.similarity * 100).toInt()}% Match", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = result.similarity,
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
            color = Color(0xFF00E676),
            trackColor = Color(0xFF1E293B)
        )
    }
}
