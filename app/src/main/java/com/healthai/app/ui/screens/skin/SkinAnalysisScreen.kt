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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.healthai.app.ml.SkinImageGate
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ─────────────────────────────────────────────────────────────────────────────
// State machine for the 2-phase pipeline
// ─────────────────────────────────────────────────────────────────────────────
private sealed class PipelineState {
    /** Phase 1: Running SkinImageGate */
    object GateChecking : PipelineState()

    /** Phase 1 FAILED: Not a skin image */
    data class GateRejected(val skinRatio: Float) : PipelineState()

    /** Phase 2: Gate passed, TFLite running */
    object ModelRunning : PipelineState()

    /** Phase 2 DONE: Saving to DB and navigating */
    object Navigating : PipelineState()

    /** Any error */
    data class Failed(val message: String) : PipelineState()
}

@Composable
fun SkinAnalysisScreen(navController: NavController, imagePath: String?) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var pipelineState by remember { mutableStateOf<PipelineState>(PipelineState.GateChecking) }

    // For "Analyze Anyway" from Gate-Rejected dialog
    var pendingBitmapPath by remember { mutableStateOf<String?>(null) }

    // ── Helper: save result → DB → navigate ──────────────────────────────────
    fun saveAndNavigate(result: SkinClassifier.Recognition, filePath: String) {
        coroutineScope.launch {
            pipelineState = PipelineState.Navigating
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
                pipelineState = PipelineState.Failed("Could not save result: ${e.message}")
            }
        }
    }

    // ── Helper: run Hugging Face Cloud AI on a file ─────────────────────────
    suspend fun runClassifier(filePath: String): SkinClassifier.Recognition {
        return withContext(Dispatchers.Default) {
            val bitmap = SkinImageDecoder.decode(File(filePath))
            try {
                val hfClassifier = com.healthai.app.ml.HuggingFaceSkinClassifier()
                hfClassifier.classifyAsRecognition(bitmap)
            } finally {
                if (!bitmap.isRecycled) bitmap.recycle()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Main Pipeline LaunchedEffect
    // ─────────────────────────────────────────────────────────────────────────
    LaunchedEffect(imagePath) {
        if (imagePath.isNullOrBlank()) {
            pipelineState = PipelineState.Failed("No image was selected for analysis.")
            return@LaunchedEffect
        }

        val imageFile = File(imagePath)
        if (!imageFile.isFile || imageFile.length() == 0L) {
            pipelineState = PipelineState.Failed("Selected image is unavailable.")
            return@LaunchedEffect
        }

        runCatching {
            // ── PHASE 1: Skin Image Gate ──────────────────────────────────────
            pipelineState = PipelineState.GateChecking
            val bitmap = withContext(Dispatchers.Default) {
                SkinImageDecoder.decode(imageFile)
            }
            val gateResult = withContext(Dispatchers.Default) {
                SkinImageGate.check(bitmap)
            }
            if (!bitmap.isRecycled) bitmap.recycle()

            Log.d("SkinGate", gateResult.reason)

            if (!gateResult.passed) {
                // ❌ Gate REJECTED — not a skin image
                pendingBitmapPath = imagePath
                pipelineState = PipelineState.GateRejected(gateResult.skinRatio)
                return@LaunchedEffect
            }

            // ── PHASE 2: TFLite 7-class classifier ───────────────────────────
            pipelineState = PipelineState.ModelRunning
            val result = runClassifier(imagePath)

            // Save and navigate
            saveAndNavigate(result, imagePath)

        }.onFailure { error ->
            Log.e("SkinAnalysis", "Pipeline failed", error)
            pipelineState = PipelineState.Failed("Analysis failed: ${error.message.orEmpty()}")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ❌ Gate Rejected — NOT SKIN Modal
    // ─────────────────────────────────────────────────────────────────────────
    // ─────────────────────────────────────────────────────────────────────────
    // ❌ Gate Rejected — Non-skin image, simple clean message
    // ─────────────────────────────────────────────────────────────────────────
    val state = pipelineState
    if (state is PipelineState.GateRejected) {
        Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFFF3B30).copy(alpha = 0.12f), RoundedCornerShape(50.dp))
                        .border(2.dp, Color(0xFFFF3B30).copy(alpha = 0.35f), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚫", fontSize = 44.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Main message
                Text(
                    text = "Please upload a clear image\nof the affected skin area.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Non-skin images (like pan, car, food, etc.) cannot be analyzed.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Retry button
                Button(
                    onClick = {
                        pipelineState = PipelineState.GateChecking
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text(
                        "📷  Upload Skin Image",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
        return
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Main UI — Loading / Error
    // ─────────────────────────────────────────────────────────────────────────
    Scaffold(containerColor = Color(0xFF0B1221)) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val s = pipelineState) {
                is PipelineState.GateChecking -> {
                    SkinAnalysisAnimation(color = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        "Phase 1: Verifying Skin Image...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Checking image for skin pixels before analysis.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                is PipelineState.ModelRunning, is PipelineState.Navigating -> {
                    SkinAnalysisAnimation(color = Color(0xFF00E676))
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        if (pipelineState is PipelineState.Navigating)
                            "Saving Results..."
                        else
                            "Phase 2: AI Model Running...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Helpix Cloud AI is analyzing skin lesion patterns.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                is PipelineState.Failed -> {
                    Text("❌", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Analysis Failed",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        s.message,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Text("Choose Another Photo", color = Color.White)
                    }
                }

                else -> { /* Gate rejected state — dialog handles it */ }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Spinner animation (reusable)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SkinAnalysisAnimation(color: Color = Color(0xFF00E676)) {
    val transition = rememberInfiniteTransition(label = "skin-spinner")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "spin"
    )
    Canvas(modifier = Modifier.size(130.dp)) {
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 110f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx())
        )
    }
}
