package com.healthai.app.ui.screens.scan.multidisease

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScanScreen(navController: NavController) {

    var heartRate by remember { mutableStateOf(TextFieldValue("")) }
    var bpSystolic by remember { mutableStateOf(TextFieldValue("")) }
    var bpDiastolic by remember { mutableStateOf(TextFieldValue("")) }
    var bloodSugar by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Vitals Scan",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Agar aapke paas device hai, to readings enter karein.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Placeholder for Camera Heart Rate
            Button(onClick = { /* TODO: Implement Camera Heart Rate */ }) {
                Text("Measure Heart Rate with Camera")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = heartRate,
                onValueChange = { heartRate = it },
                label = { Text("Heart Rate (BPM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FFFF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = bpSystolic,
                    onValueChange = { bpSystolic = it },
                    label = { Text("BP Systolic") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                     colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FFFF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = bpDiastolic,
                    onValueChange = { bpDiastolic = it },
                    label = { Text("BP Diastolic") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                     colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FFFF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = bloodSugar,
                onValueChange = { bloodSugar = it },
                label = { Text("Blood Sugar (mg/dL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                 colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00FFFF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate(NavRoutes.Symptoms) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFFF))
            ) {
                Text("Next Step", color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
            }
        }
    }
}