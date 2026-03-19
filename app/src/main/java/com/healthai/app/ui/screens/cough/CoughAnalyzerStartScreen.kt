package com.healthai.app.ui.screens.cough

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes

@Composable
fun CoughAnalyzerStartScreen(navController: NavController) {
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasAudioPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasAudioPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
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

            Icon(Icons.Default.GraphicEq, contentDescription = "Cough Analysis", tint = Color(0xFFD500F9), modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.ai_cough_tb_analyzer),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.cough_analyzer_subtitle),
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(stringResource(id = R.string.instructions), color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                InstructionRow(number = "1.", text = stringResource(id = R.string.instruction_1))
                InstructionRow(number = "2.", text = stringResource(id = R.string.instruction_2))
                InstructionRow(number = "3.", text = stringResource(id = R.string.instruction_3))
            }
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    navController.navigate(NavRoutes.CoughRecording)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD500F9)),
                enabled = hasAudioPermission
            ) {
                Text(stringResource(id = R.string.start_recording), color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun InstructionRow(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(number, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}