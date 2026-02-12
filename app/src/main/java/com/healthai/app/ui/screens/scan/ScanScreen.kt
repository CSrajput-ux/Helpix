package com.healthai.app.ui.screens.scan

import android.Manifest
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.viewmodel.ScanUiState
import com.healthai.app.ui.viewmodel.ScanViewModel

@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanState by viewModel.scanState.collectAsState()

    // Permissions Launcher
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.values.all { it }) {
            Toast.makeText(context, "Permissions needed for AI Scan", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Camera Preview Layer
        if (scanState is ScanUiState.Idle || scanState is ScanUiState.Scanning) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        }

        // 2. Overlay Layer (Scanning Animation)
        if (scanState is ScanUiState.Scanning || scanState is ScanUiState.Analyzing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val infiniteTransition = rememberInfiniteTransition(label = "scan")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ), label = "pulse"
                    )

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(4.dp, colorResource(id = R.color.neon_cyan), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (scanState is ScanUiState.Analyzing) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.neon_pink),
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = if (scanState is ScanUiState.Analyzing) "ANALYZING VITALS..." else "SCANNING...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Controls Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (scanState is ScanUiState.Idle) {
                Button(
                    onClick = { viewModel.startScanning() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.neon_cyan)),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("START DIAGNOSIS", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Result Popup (Simple Overlay for now)
        if (scanState is ScanUiState.Success) {
            val result = (scanState as ScanUiState.Success).result
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.dark_background)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Text("ANALYSIS COMPLETE", color = colorResource(id = R.color.neon_cyan), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Detected: ${result.diseaseName}", color = Color.White, fontSize = 20.sp)
                    Text("Confidence: ${(result.confidenceScore * 100).toInt()}%", color = colorResource(id = R.color.neon_pink), fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { 
                            viewModel.resetScan()
                            navController.popBackStack() 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("DONE", color = Color.Black)
                    }
                }
            }
        }
    }
}