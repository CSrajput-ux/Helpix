package com.healthai.app.ui.screens.vault

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Reports") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
    }

    // Camera Launcher (Simplified for URI)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // In a real app, save bitmap to URI
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Upload", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1221),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0B1221)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Record Title (e.g. Blood Report)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFAA00FF),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Dropdown (Simplified)
            Text("Select Category", color = Color.Gray, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Reports", "Prescriptions", "Scans").forEach { cat ->
                    val isSelected = category == cat
                    Button(
                        onClick = { category = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFAA00FF) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(cat, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Selection Options
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectionCard(
                    "Camera",
                    Icons.Default.CameraAlt,
                    Modifier.weight(1f)
                ) { cameraLauncher.launch(null) }
                
                SelectionCard(
                    "Gallery",
                    Icons.Default.Image,
                    Modifier.weight(1f)
                ) { galleryLauncher.launch("*/*") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preview or Status
            if (selectedUri != null) {
                Text("File Selected: ${selectedUri?.path?.takeLast(20)}...", color = Color(0xFF00E676))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Upload Button
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedUri != null) {
                        isUploading = true
                        // TODO: Implement actual backend upload call
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA00FF)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isUploading && title.isNotBlank() && selectedUri != null
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Securely Upload to Vault", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SelectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(120.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFAA00FF), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}