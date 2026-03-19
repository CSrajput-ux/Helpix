package com.healthai.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.healthai.app.ui.DeviceConnectScreen
import com.healthai.app.ui.screens.auth.DoctorLoginScreen
import com.healthai.app.ui.screens.auth.DoctorRegisterScreen
import com.healthai.app.ui.screens.auth.LoginScreen
import com.healthai.app.ui.screens.auth.PhoneLoginScreen
import com.healthai.app.ui.screens.auth.RegisterScreen
import com.healthai.app.ui.screens.chat.AiChatScreen
import com.healthai.app.ui.screens.cough.CoughAnalysisScreen
import com.healthai.app.ui.screens.cough.CoughAnalyzerStartScreen
import com.healthai.app.ui.screens.cough.CoughRecordingScreen
import com.healthai.app.ui.screens.dashboard.DashboardScreen
import com.healthai.app.ui.screens.diet.DietPlannerScreen
import com.healthai.app.ui.screens.doctor.DoctorDashboardScreen
import com.healthai.app.ui.screens.doctor.DoctorListScreen
import com.healthai.app.ui.screens.doctor.DoctorProfileScreen
import com.healthai.app.ui.screens.emergency.EmergencyScreen
import com.healthai.app.ui.screens.fitness.FitnessTrackerScreen
import com.healthai.app.ui.screens.health.HealthHistoryScreen
import com.healthai.app.ui.screens.health.HealthScreen
import com.healthai.app.ui.screens.hospitals.NearbyHospitalsScreen
import com.healthai.app.ui.screens.kids.KidsModeScreen
import com.healthai.app.ui.screens.onboarding.OnboardingScreen
import com.healthai.app.ui.screens.patient.MyAppointmentsScreen
import com.healthai.app.ui.screens.prescription.PrescriptionAnalysisScreen
import com.healthai.app.ui.screens.prescription.PrescriptionReaderScreen
import com.healthai.app.ui.screens.prescription.PrescriptionResultScreen
import com.healthai.app.ui.screens.prescription.PrescriptionScanningScreen
import com.healthai.app.ui.screens.profile.ProfileScreen
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
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Dashboard // DIRECT LOGIN BYPASS
    ) {
        composable(NavRoutes.Onboarding) {
            OnboardingScreen(navController = navController)
        }
        composable(NavRoutes.Login) {
            LoginScreen(navController = navController)
        }
        composable(NavRoutes.PhoneLogin) {
            PhoneLoginScreen(navController = navController)
        }
        
        composable(NavRoutes.Register) {
            RegisterScreen(navController = navController)
        }
        composable(NavRoutes.DoctorLogin) {
            DoctorLoginScreen(navController = navController)
        }
        composable(NavRoutes.DoctorRegister) {
            DoctorRegisterScreen(navController = navController)
        }
        composable(NavRoutes.DoctorDashboard) {
            DoctorDashboardScreen(navController = navController)
        }

        composable(NavRoutes.Dashboard) {
            DashboardScreen(navController = navController)
        }
        composable(NavRoutes.Scan) {
            ScanScreen(navController = navController)
        }
        composable(NavRoutes.Results) {
            ResultsScreen(navController = navController)
        }
        composable(NavRoutes.Profile) {
            ProfileScreen(navController = navController)
        }
        composable(NavRoutes.Doctors) {
            DoctorListScreen(navController = navController)
        }
        composable(
            route = "${NavRoutes.DoctorDetails}/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorProfileScreen(navController = navController, doctorId = doctorId)
        }
        composable(NavRoutes.Health) {
            HealthScreen(navController = navController)
        }
        composable(NavRoutes.HealthHistory) {
            HealthHistoryScreen()
        }
        composable(NavRoutes.MyAppointments) {
            MyAppointmentsScreen(navController = navController)
        }
        composable(NavRoutes.DietPlanner) {
            DietPlannerScreen(navController = navController)
        }
        composable(NavRoutes.AppSettings) {
            AppSettingsScreen(navController = navController)
        }
        composable("device_connect_screen") {
            DeviceConnectScreen(navController = navController)
        }
        composable(NavRoutes.Tools) {
            ToolsScreen(navController = navController)
        }
        composable(NavRoutes.KidsMode) {
            KidsModeScreen(navController = navController)
        }
        composable(NavRoutes.SeniorMode) {
            SeniorModeScreen(navController = navController)
        }
        composable(NavRoutes.RuralMode) {
            RuralModeScreen(navController = navController)
        }
        composable(NavRoutes.Emergency) {
            EmergencyScreen(navController = navController)
        }
        
        // Multi-Disease Scan Flow
        composable(NavRoutes.MultiDiseaseScanStart) {
            MultiDiseaseScanStartScreen(navController = navController)
        }
        composable(NavRoutes.FaceScan) {
            FaceScanScreen(navController = navController)
        }
        composable(NavRoutes.VoiceScan) {
            VoiceScanScreen(navController = navController)
        }
        composable(NavRoutes.VitalsScan) {
            VitalsScanScreen(navController = navController)
        }
        composable(NavRoutes.Symptoms) {
            SymptomsScreen(navController = navController)
        }
        composable(NavRoutes.ScanAnalysis) {
            ScanAnalysisScreen(navController = navController)
        }
        composable(NavRoutes.MultiDiseaseResult) {
            MultiDiseaseResultScreen(navController = navController)
        }

        // Cough Analyzer Flow
        composable(NavRoutes.CoughAnalyzerStart) {
            CoughAnalyzerStartScreen(navController = navController)
        }
        composable(NavRoutes.CoughRecording) {
            CoughRecordingScreen(navController = navController)
        }
        composable(NavRoutes.CoughAnalysis) {
            CoughAnalysisScreen(navController = navController)
        }
        composable(NavRoutes.CoughResult) {
            CoughResultScreen(navController = navController)
        }

        // Skin Detector Flow
        composable(NavRoutes.SkinDetectorStart) {
            SkinDetectorStartScreen(navController = navController)
        }
        composable(NavRoutes.SkinScanning) {
            SkinScanningScreen(navController = navController)
        }
        composable(NavRoutes.SkinAnalysis) {
            SkinAnalysisScreen(navController = navController)
        }
        // ✅ FIXED: Updated to accept disease and confidence parameters
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
        composable(NavRoutes.SymptomDoctorStart) {
            SymptomDoctorStartScreen(navController = navController)
        }
        composable(NavRoutes.SymptomChat) {
            SymptomChatScreen(navController = navController)
        }
        composable(NavRoutes.SymptomBodyMap) {
            SymptomBodyMapScreen(navController = navController)
        }
        composable(NavRoutes.SymptomAnalysis) {
            SymptomAnalysisScreen(navController = navController)
        }
        composable(NavRoutes.SymptomResult) {
            SymptomResultScreen(navController = navController)
        }

        // AI Chat Doctor
        composable(NavRoutes.AiChat) {
            AiChatScreen(navController = navController)
        }

        // Nearby Hospitals
        composable(NavRoutes.NearbyHospitals) {
            NearbyHospitalsScreen(navController = navController)
        }

        // Medicine Reminders
        composable(NavRoutes.MedicineReminders) {
            MedicineRemindersScreen(navController = navController)
        }
        composable(NavRoutes.AddReminder) {
            AddReminderScreen(navController = navController)
        }

        // Health Vault
        composable(NavRoutes.HealthVault) {
            HealthVaultScreen(navController = navController)
        }
        composable(NavRoutes.AddRecord) {
            AddRecordScreen(navController = navController)
        }

        // Fitness Tracker
        composable(NavRoutes.FitnessTracker) {
            FitnessTrackerScreen(navController = navController)
        }

        // Prescription Reader
        composable(NavRoutes.PrescriptionReader) {
            PrescriptionReaderScreen(navController = navController)
        }
        composable(NavRoutes.PrescriptionScanning) {
            PrescriptionScanningScreen(navController = navController)
        }
        composable(NavRoutes.PrescriptionAnalysis) {
            PrescriptionAnalysisScreen(navController = navController)
        }
        composable(NavRoutes.PrescriptionResult) {
            PrescriptionResultScreen(navController = navController)
        }
    }
}