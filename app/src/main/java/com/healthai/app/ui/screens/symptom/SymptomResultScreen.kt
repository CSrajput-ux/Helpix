package com.healthai.app.ui.screens.symptom

import androidx.compose.foundation.layout.*
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

data class PossibleCause(val name: String, val matchPercentage: Int, val explanation: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomResultScreen(navController: NavController) {

    // Dummy data for preview
    val triageLevel = "Yellow"
    val triageText: String
    val triageColor: Color

    when (triageLevel) {
        "Red" -> {
            triageText = "Aapko turant medical sahayata leni chahiye."
            triageColor = Color.Red
        }
        "Yellow" -> {
            triageText = "Salah di jaati hai ki aap 24 ghante ke andar doctor se milein."
            triageColor = Color.Yellow
        }
        else -> {
            triageText = "Aap ghar par in lakshano ki dekhbhal kar sakte hain."
            triageColor = Color.Green
        }
    }

    val causes = listOf(
        PossibleCause("Viral Fever", 80, "Aapke bataye gaye lakshan - bukhar, sirdard, aur thakaan - aam taur par Viral Fever mein dekhe jaate hain."),
        PossibleCause("Migraine", 55, "Tez sirdard Migraine ka sanket ho sakta hai, khas kar agar roshni se takleef ho."),
        PossibleCause("Dehydration", 40, "Pani ki kami se bhi sirdard aur thakaan ho sakti hai.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Symptom Analysis Report") },
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
                .padding(16.dp)
        ) {
            // Triage Flag
            Card(colors = CardDefaults.cardColors(containerColor = triageColor), modifier = Modifier.fillMaxWidth()) {
                 Text(triageText, color = Color.Black, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Possible Causes (Sambhavit Kaaran)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            causes.forEach {
                CauseItem(cause = it)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { /* TODO: Navigate based on triage level */ }) {
                Text("Find a Doctor")
            }
        }
    }
}

@Composable
fun CauseItem(cause: PossibleCause) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cause.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("${cause.matchPercentage}% Match", color = Color(0xFF2979FF), fontWeight = FontWeight.SemiBold)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFF334155))
            Text(cause.explanation, color = Color.Gray, fontSize = 12.sp)
        }
    }
}