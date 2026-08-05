package com.healthai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthai.app.R
import com.healthai.app.ui.DeviceConnectScreen
import com.healthai.app.ui.WatchScannerScreen
import com.healthai.app.ui.screens.auth.ForgotPasswordScreen
import com.healthai.app.ui.screens.auth.PhoneLoginScreen
import com.healthai.app.ui.screens.chat.AiChatScreen
import com.healthai.app.ui.screens.cough.CoughAnalysisScreen
import com.healthai.app.ui.screens.cough.CoughAnalyzerStartScreen
import com.healthai.app.ui.screens.cough.CoughRecordingScreen
import com.healthai.app.ui.screens.dashboard.DashboardScreen
import com.healthai.app.ui.screens.diet.DietPlannerScreen
import com.healthai.app.ui.screens.doctor.DoctorBookingsScreen
import com.healthai.app.ui.screens.doctor.DoctorDashboardScreen
import com.healthai.app.ui.screens.doctor.DoctorDashboardViewModel
import com.healthai.app.ui.screens.doctor.DoctorPatientsScreen
import com.healthai.app.ui.screens.doctor.DoctorProfileManagementScreen
import com.healthai.app.ui.screens.doctor.DoctorScheduleScreen
import com.healthai.app.ui.screens.doctor.DoctorVaultScreen
import com.healthai.app.ui.screens.doctor.DoctorsScreen
import com.healthai.app.ui.screens.DoctorDetailsScreen
import com.healthai.app.ui.screens.emergency.EmergencyScreen
import com.healthai.app.ui.screens.fitness.FitnessTrackerScreen
import com.healthai.app.ui.screens.health.HealthHistoryScreen
import com.healthai.app.ui.screens.health.HealthScreen
import com.healthai.app.ui.screens.hospitals.NearbyHospitalsScreen
import com.healthai.app.ui.screens.kids.KidsModeScreen
import com.healthai.app.ui.screens.onboarding.OnboardingScreen
import com.healthai.app.ui.screens.patient.BookingSummaryScreen
import com.healthai.app.ui.screens.patient.MyAppointmentsScreen
import com.healthai.app.ui.screens.patient.PaymentProcessScreen
import com.healthai.app.ui.screens.prescription.PrescriptionAnalysisScreen
import com.healthai.app.ui.screens.prescription.PrescriptionReaderScreen
import com.healthai.app.ui.screens.prescription.PrescriptionResultScreen
import com.healthai.app.ui.screens.prescription.PrescriptionScanningScreen
import com.healthai.app.ui.screens.profile.AboutHelpixScreen
import com.healthai.app.ui.screens.profile.HelpCenterScreen
import com.healthai.app.ui.screens.profile.ProfileScreen
import com.healthai.app.ui.screens.profile.VaccinationsScreen
import com.healthai.app.ui.screens.reminders.AddReminderScreen
import com.healthai.app.ui.screens.reminders.MedicineRemindersScreen
import com.healthai.app.ui.screens.results.ResultsScreen
import com.healthai.app.ui.screens.results.cough.CoughResultScreen
import com.healthai.app.ui.screens.results.multidisease.MultiDiseaseResultScreen
import com.healthai.app.ui.screens.results.skin.SkinResultScreen
import com.healthai.app.ui.screens.rural.RuralModeScreen
import com.healthai.app.ui.screens.scan.ScanScreen
import com.healthai.app.ui.screens.scan.multidisease.*
import com.healthai.app.ui.screens.settings.AppSettingsScreen
import com.healthai.app.ui.screens.skin.SkinAnalysisScreen
import com.healthai.app.ui.screens.skin.SkinDetectorStartScreen
import com.healthai.app.ui.screens.skin.SkinScanningScreen
import com.healthai.app.ui.screens.symptom.SymptomAnalysisScreen
import com.healthai.app.ui.screens.symptom.SymptomBodyMapScreen
import com.healthai.app.ui.screens.symptom.SymptomChatScreen
import com.healthai.app.ui.screens.symptom.SymptomDoctorStartScreen
import com.healthai.app.ui.screens.symptom.SymptomResultScreen
import com.healthai.app.ui.screens.senior.SeniorModeScreen
import com.healthai.app.ui.screens.tools.ToolsScreen
import com.healthai.app.ui.screens.vault.AddRecordScreen
import com.healthai.app.ui.screens.vault.HealthVaultScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    var startDestination by remember { mutableStateOf<String?>(null) }
    var userType by remember { mutableStateOf("PATIENT") }

    LaunchedEffect(Unit) {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userType = document.getString("userType") ?: "PATIENT"
                    startDestination = if (userType == "DOCTOR") NavRoutes.DoctorDashboard else NavRoutes.Dashboard
                }
                .addOnFailureListener {
                    startDestination = NavRoutes.Dashboard
                }
        } else {
            startDestination = NavRoutes.Dashboard
        }
    }

    val destination = startDestination
    if (destination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = colorResource(id = R.color.logo_cyan))
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = destination
    ) {
        composable(NavRoutes.Onboarding) { OnboardingScreen(navController = navController) }
        composable(NavRoutes.ForgotPassword) { ForgotPasswordScreen(navController = navController) }
        composable(NavRoutes.PhoneLogin) { PhoneLoginScreen(navController = navController) }
        composable(NavRoutes.DoctorDashboard) {
            DoctorDashboardScreen(navController = navController)
        }
        composable(NavRoutes.DoctorBookings) {
            val parentEntry = remember(it) {
                try { navController.getBackStackEntry(NavRoutes.DoctorDashboard) }
                catch (_: Exception) { null }
            }
            val viewModel: DoctorDashboardViewModel = if (parentEntry != null) hiltViewModel(parentEntry) else hiltViewModel()
            DoctorBookingsScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.DoctorSchedule) {
            val parentEntry = remember(it) {
                try { navController.getBackStackEntry(NavRoutes.DoctorDashboard) }
                catch (_: Exception) { null }
            }
            val viewModel: DoctorDashboardViewModel = if (parentEntry != null) hiltViewModel(parentEntry) else hiltViewModel()
            DoctorScheduleScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.DoctorPatients) { DoctorPatientsScreen(navController = navController) }
        composable(NavRoutes.DoctorProfileManagement) {
            val parentEntry = remember(it) {
                try { navController.getBackStackEntry(NavRoutes.DoctorDashboard) }
                catch (_: Exception) { null }
            }
            val viewModel: DoctorDashboardViewModel = if (parentEntry != null) hiltViewModel(parentEntry) else hiltViewModel()
            DoctorProfileManagementScreen(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.DoctorVault) {
            val parentEntry = remember(it) {
                try { navController.getBackStackEntry(NavRoutes.DoctorDashboard) }
                catch (_: Exception) { null }
            }
            val viewModel: DoctorDashboardViewModel = if (parentEntry != null) hiltViewModel(parentEntry) else hiltViewModel()
            DoctorVaultScreen(navController = navController, viewModel = viewModel)
        }

        composable(NavRoutes.Dashboard) { DashboardScreen(navController = navController) }
        composable(NavRoutes.Scan) { ScanScreen(navController = navController) }
        composable(NavRoutes.Results) { ResultsScreen(navController = navController) }
        composable(NavRoutes.Profile) { ProfileScreen(navController = navController) }
        composable(NavRoutes.Vaccinations) { VaccinationsScreen(navController = navController) }
        composable(NavRoutes.HelpCenter) { HelpCenterScreen(navController = navController) }
        composable(NavRoutes.AboutHelpix) { AboutHelpixScreen(navController = navController) }
        
        composable(NavRoutes.Doctors) { DoctorsScreen(navController = navController) }
        
        composable(NavRoutes.DoctorDetails) { DoctorDetailsScreen(navController = navController) }
        composable(NavRoutes.Health) { HealthScreen(navController = navController) }
        composable(NavRoutes.HealthHistory) { HealthHistoryScreen() }
        composable(NavRoutes.MyAppointments) { MyAppointmentsScreen(navController = navController) }
        
        composable(
            route = NavRoutes.BookingSummary,
            arguments = listOf(
                navArgument("docName") { type = NavType.StringType },
                navArgument("specialization") { type = NavType.StringType },
                navArgument("fee") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val docName = backStackEntry.arguments?.getString("docName") ?: ""
            val specialization = backStackEntry.arguments?.getString("specialization") ?: ""
            val fee = backStackEntry.arguments?.getString("fee")?.toDoubleOrNull() ?: 0.0
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val time = backStackEntry.arguments?.getString("time") ?: ""
            
            BookingSummaryScreen(navController, docName, specialization, fee, date, time)
        }
        
        composable(NavRoutes.PaymentProcess) { PaymentProcessScreen(navController = navController) }
        
        composable(NavRoutes.DietPlanner) { DietPlannerScreen(navController = navController) }
        composable(NavRoutes.AppSettings) { AppSettingsScreen(navController = navController) }
        composable(NavRoutes.DeviceConnect) { DeviceConnectScreen(navController = navController) }
        composable(NavRoutes.WatchScanner) {
            WatchScannerScreen(navController = navController) { _ -> navController.popBackStack() }
        }
        composable(NavRoutes.Tools) { ToolsScreen(navController = navController) }
        composable(NavRoutes.KidsMode) { KidsModeScreen(navController = navController) }
        composable(NavRoutes.SeniorMode) { SeniorModeScreen(navController = navController) }
        composable(NavRoutes.RuralMode) { RuralModeScreen(navController = navController) }
        composable(NavRoutes.Emergency) { EmergencyScreen(navController = navController) }
        
        // Multi-Disease Scan Flow
        composable(NavRoutes.MultiDiseaseScanStart) { MultiDiseaseScanStartScreen(navController = navController) }
        composable(NavRoutes.FaceScan) { FaceScanScreen(navController = navController) }
        composable(NavRoutes.VoiceScan) { VoiceScanScreen(navController = navController) }
        composable(NavRoutes.VitalsScan) { VitalsScanScreen(navController = navController) }
        composable(NavRoutes.Symptoms) { SymptomsScreen(navController = navController) }
        composable(NavRoutes.ScanAnalysis) { ScanAnalysisScreen(navController = navController) }
        composable(NavRoutes.MultiDiseaseResult) { MultiDiseaseResultScreen(navController = navController) }

        // Cough Analyzer Flow
        composable(NavRoutes.CoughAnalyzerStart) { CoughAnalyzerStartScreen(navController = navController) }
        composable(NavRoutes.CoughRecording) { CoughRecordingScreen(navController = navController) }
        composable(NavRoutes.CoughAnalysis) { CoughAnalysisScreen(navController = navController) }
        composable(NavRoutes.CoughResult) { CoughResultScreen(navController = navController) }

        // Skin Detector Flow
        composable(NavRoutes.SkinDetectorStart) { SkinDetectorStartScreen(navController = navController) }
        composable(NavRoutes.SkinScanning) { SkinScanningScreen(navController = navController) }
        composable(NavRoutes.SkinAnalysis) { SkinAnalysisScreen(navController = navController) }
        composable(
            route = "${NavRoutes.SkinResult}/{label}/{confidence}",
            arguments = listOf(
                navArgument("label") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val label = backStackEntry.arguments?.getString("label") ?: "Unknown"
            val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0f
            SkinResultScreen(navController, diseaseName = label, confidence = confidence)
        }

        // Symptom Doctor Flow
        composable(NavRoutes.SymptomDoctorStart) { SymptomDoctorStartScreen(navController = navController) }
        composable(NavRoutes.SymptomChat) { SymptomChatScreen(navController = navController) }
        composable(NavRoutes.SymptomBodyMap) { SymptomBodyMapScreen(navController = navController) }
        composable(NavRoutes.SymptomAnalysis) { SymptomAnalysisScreen(navController = navController) }
        composable(NavRoutes.SymptomResult) { SymptomResultScreen(navController = navController) }

        // AI Chat Doctor
        composable(NavRoutes.AiChat) { AiChatScreen(navController = navController) }

        // Nearby Hospitals
        composable(NavRoutes.NearbyHospitals) { NearbyHospitalsScreen(navController = navController) }

        // Medicine Reminders
        composable(NavRoutes.MedicineReminders) { MedicineRemindersScreen(navController = navController) }
        composable(NavRoutes.AddReminder) { AddReminderScreen(navController = navController) }

        // Health Vault
        composable(NavRoutes.HealthVault) { HealthVaultScreen(navController = navController) }
        composable(NavRoutes.AddRecord) { AddRecordScreen(navController = navController) }

        // Fitness Tracker
        composable(NavRoutes.FitnessTracker) { FitnessTrackerScreen(navController = navController) }

        // Prescription Reader
        composable(NavRoutes.PrescriptionReader) { PrescriptionReaderScreen(navController = navController) }
        composable(NavRoutes.PrescriptionScanning) { PrescriptionScanningScreen(navController = navController) }
        composable(NavRoutes.PrescriptionAnalysis) { PrescriptionAnalysisScreen(navController = navController) }
        composable(NavRoutes.PrescriptionResult) { PrescriptionResultScreen(navController = navController) }
    }
}
