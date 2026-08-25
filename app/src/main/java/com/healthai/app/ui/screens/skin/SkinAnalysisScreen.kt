package com.healthai.app.ui.screens.skin

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.data.local.database.HelpixDatabase
import com.healthai.app.data.local.entity.SkinScanEntity
import com.healthai.app.ml.SkinClassifier
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** Agar model ka top confidence itne se kam ho toh image skin nahi hai */
private const val NOT_SKIN_THRESHOLD = 0.15f

@Composable
fun SkinAnalysisScreen(navController: NavController, imagePath: String?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Non-skin image warning dialog states
    var showNotSkinDialog by remember { mutableStateOf(false) }
    var pendingRecognition by remember { mutableStateOf<SkinClassifier.Recognition?>(null) }
    var pendingImagePath by remember { mutableStateOf<String?>(null) }

    // DB mein save karke navigate karo
    fun saveAndNavigate(result: SkinClassifier.Recognition, filePath: String) {
        coroutineScope.launch {
            runCatching {
                val scanId = withContext(Dispatchers.IO) {
                    HelpixDatabase.getDatabase(context).skinScanDao().insertScan(
                        SkinScanEntity(
                            diseaseName = result.label,
                            confidence = result.confidence,
                            isUncertain = result.isUncertain,
                            topPredictions = result.top3.joinToString(",") { "${it.first}:${it.second}" },
                            imagePath = filePath
                        )
                    )
                }
                navController.navigate(
                    NavRoutes.SkinResult.replace("{scanId}", scanId.toInt().toString())
                ) {
                    popUpTo(NavRoutes.SkinDetectorStart) { inclusive = true }
                }
            }.onFailure { e ->
                errorMessage = "Analysis could not be completed. ${e.message.orEmpty()}"
            }
        }
    }

    LaunchedEffect(imagePath) {
        if (imagePath.isNullOrBlank()) {
            errorMessage = "No image was selected for analysis."
            return@LaunchedEffect
        }

        runCatching {
            withContext(Dispatchers.Default) {
                val imageFile = File(imagePath)
                require(imageFile.isFile && imageFile.length() > 0) { "Selected image is unavailable." }

                val bitmap = SkinImageDecoder.decode(imageFile)
                try {
                    val classifier = SkinClassifier(context)
                    try {
                        classifier.classifySkin(bitmap)
                    } finally {
                        classifier.close()
                    }
                } finally {
                    if (!bitmap.isRecycled) bitmap.recycle()
                }
            }
        }.onSuccess { result ->
            if (result.confidence < NOT_SKIN_THRESHOLD) {
                // ⚠️ Confidence bahut kam — skin image nahi lag rahi
                pendingRecognition = result
                pendingImagePath = imagePath
                showNotSkinDialog = true
            } else {
                // ✅ Normal flow — save karke navigate karo
                saveAndNavigate(result, imagePath)
            }
        }.onFailure { error ->
            Log.e("SkinAnalysis", "Skin analysis failed", error)
            errorMessage = "Analysis could not be completed. ${error.message.orEmpty()}"
        }
    }

    // ─────────────────────────────────────────────
    // ⚠️ Non-Skin Image Warning Modal
    // ─────────────────────────────────────────────
    if (showNotSkinDialog) {
        AlertDialog(
            onDismissRequest = { /* Bahar touch se dismiss na ho */ },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "⚠️",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Skin Image Nahi Laga",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column {
                    // Warning banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFF6B35).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFFF6B35).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Yeh image kisi skin disease jaisi nahi lag rahi hai. " +
                                    "Galat image se galat result aa sakta hai.",
                            color = Color(0xFFFF6B35),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Behtar results ke liye:\n" +
                                "• Affected skin area ki clear photo lo\n" +
                                "• Achhi roshni mein photo lo\n" +
                                "• Close-up shot lena better hoga",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Primary: Doosri photo lo
                    Button(
                        onClick = {
                            showNotSkinDialog = false
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                    ) {
                        Text(
                            "📷  Doosri Photo Lo",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    // Secondary: Phir bhi analyze karo
                    OutlinedButton(
                        onClick = {
                            showNotSkinDialog = false
                            val rec = pendingRecognition ?: return@OutlinedButton
                            val path = pendingImagePath ?: return@OutlinedButton
                            saveAndNavigate(rec, path)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, Color.White.copy(alpha = 0.3f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                    ) {
                        Text(
                            "Phir Bhi Analyze Karo",
                            fontSize = 14.sp
                        )
                    }
                }
            },
            dismissButton = null  // Custom buttons upar hain
        )
    }

    // ─────────────────────────────────────────────
    // Main Screen
    // ─────────────────────────────────────────────
    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (errorMessage == null) {
                SkinAnalysisAnimation()
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "Analyzing Your Skin...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Helpix AI is scanning for patterns, color, and texture markers.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    "Unable to analyze image",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage.orEmpty(), color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Choose another photo")
                }
            }
        }
    }
}

@Composable
private fun SkinAnalysisAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "skin-analysis")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "skin-analysis-angle"
    )
    Canvas(modifier = Modifier.size(150.dp)) {
        drawArc(
            color = Color(0xFF00E676),
            startAngle = angle,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
    }
}
