package com.healthai.app.ui.screens.prescription

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
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

private val OffWhiteBg = Color(0xFFF8FAFC)
private val CardBorderGray = Color(0xFFE2E8F0)
private val MedicalTeal = Color(0xFF0D9488)
private val FriendlyRoyalBlue = Color(0xFF2563EB)
private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@Composable
fun PrescriptionReaderScreen(navController: NavController) {
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

    Scaffold(containerColor = OffWhiteBg) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color(0xFFE0E7FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = "Prescription Reader", tint = FriendlyRoyalBlue, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AI Prescription Reader",
                color = DarkCharcoalText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scan the doctor's prescription to easily understand medicines and their details.",
                color = SlateGrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .border(androidx.compose.foundation.BorderStroke(1.dp, CardBorderGray), RoundedCornerShape(18.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Instructions:", color = DarkCharcoalText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                InstructionRow(number = "1.", text = "Place the prescription on a flat surface.")
                InstructionRow(number = "2.", text = "Ensure there is good lighting.")
                InstructionRow(number = "3.", text = "Bring the entire prescription within the frame.")
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    navController.navigate(NavRoutes.PrescriptionScanning)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FriendlyRoyalBlue),
                enabled = hasCamPermission
            ) {
                Text("Scan Prescription", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun InstructionRow(number: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text(number, color = MedicalTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = SlateGrayText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
