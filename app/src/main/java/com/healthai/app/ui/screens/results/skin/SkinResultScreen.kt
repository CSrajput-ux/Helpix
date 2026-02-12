package com.healthai.app.ui.screens.results.skin

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R // Assume a placeholder image is in drawables

data class SimilarityResult(val name: String, val similarity: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinResultScreen(navController: NavController) {

    // Dummy data for preview
    val mainResult = "Eczema"
    val flagText: String
    val flagColor: Color

    when(mainResult) {
        "Eczema" -> {
            flagText = "Doctor se Salah Lena Behtar Hoga"
            flagColor = Color.Yellow
        }
        "Melanoma" -> {
             flagText = "Turant Doctor ko Dikhayein"
             flagColor = Color.Red
        }
        else -> {
            flagText = "Gharelu Dekhbhaal Sambhav Hai"
            flagColor = Color.Green
        }
    }

    val similarityResults = listOf(
        SimilarityResult("Eczema", 0.90f),
        SimilarityResult("Psoriasis", 0.45f),
        SimilarityResult("Normal Rash", 0.30f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skin Analysis Report") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User's photo placeholder
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), 
                contentDescription = "Scanned Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Triage Flag
            Card(colors = CardDefaults.cardColors(containerColor = flagColor), modifier = Modifier.fillMaxWidth()) {
                Text(flagText, color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("AI Analysis", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Text("AI ko is jagah par laalpan (redness), halki soojan (mild inflammation), aur dry patches mile hain.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(16.dp))

            // Similarity Sliders
            similarityResults.forEach {
                SimilarityItem(it)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { /* Navigate to doctors */ }) {
                Text("Find a Skin Specialist")
            }
        }
    }
}

@Composable
fun SimilarityItem(result: SimilarityResult) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${result.name}", color = Color.White)
            Text("${(result.similarity * 100).toInt()}% Match", color = Color(0xFF00E676))
        }
        LinearProgressIndicator(
            progress = result.similarity,
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF00E676),
            trackColor = Color(0xFF1E293B)
        )
    }
}