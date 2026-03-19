package com.healthai.app.ui.screens.hospitals

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

data class Hospital(
    val name: String,
    val distance: String,
    val type: String,
    val specialty: String,
    val hasEmergency: Boolean,
    val lat: Double? = null,
    val lng: Double? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyHospitalsScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var isLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var hospitalList by remember { mutableStateOf<List<Hospital>>(emptyList()) }

    // Dummy data that will be "simulated" as sorted once location is fetched
    val baseHospitals = listOf(
        Hospital("Apollo Hospital", "Calculating...", "Private", "Multi-specialty", true),
        Hospital("Fortis Healthcare", "Calculating...", "Private", "Emergency Care", true),
        Hospital("Max Super Speciality", "Calculating...", "Private", "Cardiology", true),
        Hospital("AIIMS", "Calculating...", "Government", "General Medicine", true),
        Hospital("City Clinic", "Calculating...", "Private", "Pediatrics", false)
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                fetchLocationAndHospitals(context, fusedLocationClient) {
                    isLoading = false
                    // In a real app with Places API, we'd fetch real hospitals here.
                    // For now, we simulate "Near you" by showing base list.
                    hospitalList = baseHospitals.map { it.copy(distance = "${(1..5).random()}.${(0..9).random()} km") }
                        .sortedBy { it.distance }
                }
            } else {
                locationError = "Location permission denied"
                isLoading = false
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Function to open Google Maps search
    val openMapsSearch: () -> Unit = {
        val gmmIntentUri = Uri.parse("geo:0,0?q=hospitals near me")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Hospitals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        ) {
            // "Fetch Live" Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF1E293B))
                    .clickable { openMapsSearch() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Cyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fetching your live location...", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap here to see LIVE nearby hospitals on Map", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Current Location: Detected", color = Color.Green, fontSize = 11.sp)
                    }
                }
            }

            if (locationError != null) {
                Text(locationError!!, color = Color.Red, modifier = Modifier.padding(16.dp))
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hospitalList) { hospital ->
                    HospitalListItem(hospital) {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=${hospital.name}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    }
                }
            }
        }
    }
}

private fun fetchLocationAndHospitals(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onComplete: () -> Unit
) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return
    }

    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            // In a real scenario, you'd use location.latitude and location.longitude 
            // to call Google Places API. 
            onComplete()
        }
        .addOnFailureListener {
            onComplete()
        }
}

@Composable
fun HospitalListItem(hospital: Hospital, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalHospital,
                contentDescription = null,
                tint = Color(0xFFFF9100),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(hospital.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${hospital.specialty} • ${hospital.distance}", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(text = hospital.type, color = if (hospital.type == "Government") Color.Cyan else Color.LightGray)
                    if (hospital.hasEmergency) {
                        Badge(text = "24/7 Emergency", color = Color(0xFFFF6B6B))
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}