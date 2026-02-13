package com.healthai.app.ui.screens.doctor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.healthai.app.R
import com.healthai.app.data.repository.AppointmentRepository
import com.healthai.app.data.repository.UserRepository
import com.healthai.app.domain.model.Appointment
import com.healthai.app.domain.model.User
import com.healthai.app.ui.components.AIButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class DoctorProfileViewModel(private val doctorId: String) : ViewModel() {
    private val userRepository = UserRepository()
    private val appointmentRepository = AppointmentRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _doctor = MutableStateFlow<User?>(null)
    val doctor = _doctor.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _bookingConfirmed = MutableStateFlow(false)
    val bookingConfirmed = _bookingConfirmed.asStateFlow()

    init {
        fetchDoctorDetails()
    }

    private fun fetchDoctorDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _doctor.value = userRepository.getDoctorById(doctorId)
            _isLoading.value = false
        }
    }

    fun bookAppointment(appointmentDate: Date) {
        viewModelScope.launch {
            _isLoading.value = true
            val patientId = auth.currentUser?.uid ?: return@launch
            val appointment = Appointment(
                doctorId = doctorId,
                patientId = patientId,
                appointmentDate = appointmentDate
            )
            appointmentRepository.createAppointment(appointment)
            _bookingConfirmed.value = true
            _isLoading.value = false
        }
    }
}

@Composable
fun DoctorProfileScreen(navController: NavController, doctorId: String) {
    val viewModel: DoctorProfileViewModel = viewModel(factory = DoctorProfileViewModelFactory(doctorId))
    val doctor by viewModel.doctor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookingConfirmed by viewModel.bookingConfirmed.collectAsState()

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }

    if (bookingConfirmed) {
        Toast.makeText(context, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.login_bg_top),
                        colorResource(id = R.color.login_bg_bottom)
                    )
                )
            )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = colorResource(id = R.color.logo_cyan))
        } else {
            doctor?.let { doc ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text(stringResource(id = R.string.doctor_login), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally).size(120.dp).clip(CircleShape).background(colorResource(id = R.color.helpix_bg_top)), contentAlignment = Alignment.Center){
                        Text(doc.name.first().toString(), fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(doc.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text(doc.specialization ?: "", fontSize = 16.sp, color = colorResource(id = R.color.logo_cyan), modifier = Modifier.align(Alignment.CenterHorizontally))
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileDetailItem(Icons.Default.MedicalServices, "License No: ${doc.licenseNumber}")
                    ProfileDetailItem(Icons.Default.Business, "Address: ${doc.clinicAddress}")
                    ProfileDetailItem(Icons.Default.VerifiedUser, "Email: ${doc.email}")

                    Spacer(modifier = Modifier.weight(1f))
                    
                    AIButton(
                        text = stringResource(id = R.string.book_appointment),
                        backgroundColor = colorResource(id = R.color.logo_blue),
                        textColor = Color.White,
                        onClick = { showDatePicker = true }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.time
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val finalDate = selectedDate?.let {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)
                    cal.time
                }
                if (finalDate != null) {
                    viewModel.bookAppointment(finalDate)
                }
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }
}

@Composable
private fun ProfileDetailItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.size(16.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

class DoctorProfileViewModelFactory(private val doctorId: String) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorProfileViewModel(doctorId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}