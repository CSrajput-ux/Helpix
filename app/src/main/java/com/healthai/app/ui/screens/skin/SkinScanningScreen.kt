package com.healthai.app.ui.screens.skin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun SkinScanningScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    DisposableEffect(cameraExecutor) {
        onDispose { cameraExecutor.shutdown() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCamPermission = granted }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri ?: return@rememberLauncherForActivityResult
            coroutineScope.launch {
                runCatching {
                    withContext(Dispatchers.IO) { copyGalleryImage(context, uri.toString()) }
                }.onSuccess { file ->
                    navController.navigate(NavRoutes.skinAnalysis(file.absolutePath))
                }.onFailure { error ->
                    Log.e(TAG, "Gallery image import failed", error)
                    snackbarHostState.showSnackbar("Unable to open that image. Please choose another photo.")
                }
            }
        }
    )

    LaunchedEffect(hasCamPermission) {
        if (!hasCamPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        containerColor = Color(0xFF0B1221),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Skin Analysis", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "Align the affected area within the circle and capture.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (hasCamPermission) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 24.dp)
                ) {
                    CameraPreview(imageCapture)
                    Box(
                        modifier = Modifier.align(Alignment.Center).size(250.dp)
                            .border(3.dp, Color(0xFF00E676), CircleShape)
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Camera permission is required.", color = Color.White)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = cameraExecutor,
                            onImageCaptured = { file ->
                                coroutineScope.launch {
                                    navController.navigate(NavRoutes.skinAnalysis(file.absolutePath))
                                }
                            },
                            onError = { error ->
                                coroutineScope.launch {
                                    Log.e(TAG, "Camera photo capture failed", error)
                                    snackbarHostState.showSnackbar("Photo capture failed. Please try again.")
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(72.dp),
                    enabled = hasCamPermission
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Capture",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
private fun CameraPreview(imageCapture: ImageCapture) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { viewContext ->
            val previewView = PreviewView(viewContext)
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            cameraProviderFuture.addListener({
                runCatching {
                    cameraProviderFuture.get().apply {
                        unbindAll()
                        bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    }
                }.onFailure { Log.e(TAG, "Camera binding failed", it) }
            }, ContextCompat.getMainExecutor(viewContext))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun copyGalleryImage(context: Context, uri: String): File {
    val sourceUri = android.net.Uri.parse(uri)
    require(context.contentResolver.getType(sourceUri)?.startsWith("image/") != false) {
        "Selected content is not an image."
    }
    val destination = File(context.cacheDir, "skin_scan_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(destination).use { output -> input.copyTo(output) }
    } ?: error("Unable to read selected image")
    require(destination.length() > 0) { "Selected image was empty" }
    return destination
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onImageCaptured: (File) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(context.cacheDir, "skin_scan_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) = onImageCaptured(photoFile)
        override fun onError(exception: ImageCaptureException) = onError(exception)
    })
}

private const val TAG = "SkinScanning"
