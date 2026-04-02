package com.healthai.app.ui.screens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
    var gender by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    // Gender Dropdown state
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Other", "Prefer not to say")

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(context, fusedLocationClient) { loc ->
                location = loc
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfileImage(it)
        }
    }

    // Update local state when user profile is fetched
    LaunchedEffect(user) {
        user?.let {
            fullName = it.full_name ?: ""
            age = it.age?.toString() ?: ""
            bloodGroup = it.blood_group ?: ""
            location = it.location ?: ""
            gender = it.gender ?: ""
            emergencyContact = it.emergency_contact ?: ""
            allergies = it.allergies ?: ""
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
                                viewModel.updateProfile(
                                    fullName = fullName,
                                    age = age.toIntOrNull(),
                                    bloodGroup = bloodGroup,
                                    gender = gender,
                                    emergencyContact = emergencyContact,
                                    location = location,
                                    allergies = allergies
                                )
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
        if (isLoading && user == null) {
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
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(NavRoutes.Login) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.logo_cyan))
                ) {
                    Text("Return to Login", color = Color.Black)
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, colorResource(id = R.color.logo_cyan), CircleShape)
                            .clickable {
                                if (isEditing) {
                                    imagePickerLauncher.launch("image/*")
                                }
                            }
                    ) {
                        if (user?.profile_image_url != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user?.profile_image_url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                        
                        if (isEditing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // EMERGENCY CARD SECTION (Visual matches your screenshot)
                Text(
                    text = "Your Emergency Card",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            EmergencyItem("Name", fullName, Modifier.weight(1f))
                            EmergencyItem("Age", if (age.isNotEmpty()) "$age years" else "--", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            EmergencyItem("Blood Group", bloodGroup, Modifier.weight(1f), icon = Icons.Default.Bloodtype)
                            EmergencyItem("Allergies", if (allergies.isNotEmpty()) allergies else "None", Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

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

                // Gender Selection (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = genderExpanded && isEditing,
                    onExpandedChange = { if (isEditing) genderExpanded = !genderExpanded },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        leadingIcon = { Icon(Icons.Default.Transgender, contentDescription = null, tint = if (isEditing) colorResource(id = R.color.logo_cyan) else Color.Gray) },
                        trailingIcon = { if (isEditing) ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
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
                    ExposedDropdownMenu(
                        expanded = genderExpanded && isEditing,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.White) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                // Location with auto-extract
                ProfileTextField(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    enabled = isEditing,
                    icon = Icons.Default.LocationOn,
                    trailingIcon = {
                        if (isEditing) {
                            IconButton(onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    getCurrentLocation(context, fusedLocationClient) { loc ->
                                        location = loc
                                    }
                                } else {
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Get Location", tint = colorResource(id = R.color.logo_cyan))
                            }
                        }
                    }
                )

                ProfileTextField(
                    label = "Emergency Contact",
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    enabled = isEditing,
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                ProfileTextField(
                    label = "Allergies",
                    value = allergies,
                    onValueChange = { allergies = it },
                    enabled = isEditing,
                    icon = Icons.Default.Warning
                )

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
fun EmergencyItem(label: String, value: String, modifier: Modifier, icon: ImageVector? = null) {
    Column(modifier = modifier) {
        Text(label, color = Color.Red.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(if (value.isNotEmpty()) value else "--", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
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
        trailingIcon = trailingIcon,
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
                    val subLocality = address.subLocality ?: ""
                    onLocationFound(if (subLocality.isNotEmpty()) "$subLocality, $city" else city)
                } else {
                    onLocationFound("${loc.latitude}, ${loc.longitude}")
                }
            } catch (e: Exception) {
                onLocationFound("${loc.latitude}, ${loc.longitude}")
            }
        } else {
            Toast.makeText(context, "Please turn on GPS and wait a moment", Toast.LENGTH_SHORT).show()
        }
    }
}
