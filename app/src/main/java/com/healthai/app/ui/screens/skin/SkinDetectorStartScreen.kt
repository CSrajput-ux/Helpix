package com.healthai.app.ui.screens.skin

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes


@Composable
fun SkinDetectorStartScreen(navController: NavController) {
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCamPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Icon(Icons.Default.FaceRetouchingNatural, contentDescription = "Skin Analysis", tint = Color(0xFF00E676), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AI Skin Health Analyzer",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Understand your skin concerns better. Read instructions below before taking a photo.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text("Instructions:", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                InstructionRow(number = "1.", text = "The affected area should have good lighting.")
                InstructionRow(number = "2.", text = "Keep the camera very close to the skin (4-6 inches).")
                InstructionRow(number = "3.", text = "The photo must be clear, not blurry.")
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    navController.navigate(NavRoutes.SkinScanning)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                enabled = hasCamPermission
            ) {
                Text("Start Scanning", color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black)
            }
        }
    }
}

@Composable
fun InstructionRow(number: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(number, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}