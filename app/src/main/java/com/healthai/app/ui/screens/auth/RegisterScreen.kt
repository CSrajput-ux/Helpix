package com.healthai.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.components.AIButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("CREATE ACCOUNT", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            // Fields updated to match theme
            val commonColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.logo_cyan),
                unfocusedBorderColor = colorResource(id = R.color.text_grey),
                focusedTextColor = Color.White,
                containerColor = Color.Transparent
            )

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password", color = Color.Gray) }, colors = commonColors, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(32.dp))

            AIButton(
                text = "REGISTER",
                backgroundColor = colorResource(id = R.color.logo_blue),
                textColor = Color.White,
                onClick = { navController.navigate("dashboard_screen") { popUpTo("onboarding_screen") { inclusive = true } } }
            )
        }
    }
}