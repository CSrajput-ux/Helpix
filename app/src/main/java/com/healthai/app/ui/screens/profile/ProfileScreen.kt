package com.healthai.app.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Form States
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context, fusedLocationClient) { loc ->
                location = loc
            }
        }
    }

    LaunchedEffect(user) {
        user?.let {
            fullName = it.name
            age = "25" // Placeholder for now
            location = it.clinicAddress ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (!isLoading && user != null) {
                        TextButton(onClick = {
                            if (isEditing) {
                                isEditing = false
                                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                            } else {
                                isEditing = true
                            }
                        }) {
                            Text(
                                if (isEditing) "SAVE" else "EDIT",
                                color = colorResource(id = R.color.logo_cyan),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
            }
        } else if (user == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Please login to view profile.", color = Color.White)
                Button(onClick = { navController.navigate(NavRoutes.Login) }) {
                    Text("Return to Login")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Profile Image Section
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(2.dp, colorResource(id = R.color.logo_cyan), CircleShape)
                            .padding(4.dp)
                    ) {
                        Image(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.DarkGray),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfileSectionTitle("Personal Information")
                
                ProfileTextField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    enabled = isEditing,
                    icon = Icons.Default.Person
                )

                ProfileTextField(
                    label = "Email",
                    value = user?.email ?: "",
                    onValueChange = { },
                    enabled = false,
                    icon = Icons.Default.Email
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileTextField(
                            label = "Age",
                            value = age,
                            onValueChange = { age = it },
                            enabled = isEditing,
                            icon = Icons.Default.DateRange,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileTextField(
                            label = "Blood Group",
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            enabled = isEditing,
                            icon = Icons.Default.Bloodtype
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!isEditing) {
                    Button(
                        onClick = { 
                            viewModel.logout()
                            navController.navigate(NavRoutes.Login) {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        color = colorResource(id = R.color.logo_cyan),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = if (enabled) colorResource(id = R.color.logo_cyan) else Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(id = R.color.logo_cyan),
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.DarkGray,
            disabledTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledLabelColor = Color.Gray
        )
    )
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, client: com.google.android.gms.location.FusedLocationProviderClient, onLocationFound: (String) -> Unit) {
    client.lastLocation.addOnSuccessListener { loc ->
        if (loc != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: ""
                    val country = address.countryName ?: ""
                    onLocationFound("$city, $country")
                } else {
                    onLocationFound("${loc.latitude}, ${loc.longitude}")
                }
            } catch (e: Exception) {
                onLocationFound("${loc.latitude}, ${loc.longitude}")
            }
        } else {
            Toast.makeText(context, "Please turn on GPS", Toast.LENGTH_SHORT).show()
        }
    }
}
