package com.healthai.app.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.ui.navigation.NavRoutes

private val DarkCharcoalText = Color(0xFF1E293B)
private val SlateGrayText = Color(0xFF475569)

@Composable
fun PhoneLoginScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }

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
        GridBackgroundPhone()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colorResource(id = R.color.logo_cyan), colorResource(id = R.color.logo_blue))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock, 
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = DarkCharcoalText, fontWeight = FontWeight.Bold)) {
                        append("HELP")
                    }
                    withStyle(style = SpanStyle(color = colorResource(id = R.color.logo_cyan), fontWeight = FontWeight.Bold)) {
                        append("iX")
                    }
                },
                fontSize = 28.sp
            )
            
            Text(
                text = "✨ AI-Powered Healthcare Platform",
                color = SlateGrayText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- MAIN PHONE CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, spotColor = colorResource(id = R.color.card_border_glow))
                    .border(1.dp, colorResource(id = R.color.card_border_glow), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "← Back to options",
                        color = colorResource(id = R.color.logo_cyan),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorResource(id = R.color.logo_cyan).copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Phone, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Phone Login",
                            color = DarkCharcoalText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Phone Number", color = SlateGrayText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    PhoneTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = "+1 (555) 000-0000"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFCCFBF1), RoundedCornerShape(10.dp))
                            .border(1.dp, colorResource(id = R.color.logo_cyan).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, tint = colorResource(id = R.color.logo_cyan), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phone sign-in is not available yet. Continue with email instead.",
                                color = Color(0xFF115E59),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { navController.navigate(NavRoutes.EmailLogin) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            colorResource(id = R.color.btn_phone_start),
                                            colorResource(id = R.color.btn_phone_end)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Continue with Email", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Protected by 256-bit encryption • Your data is secure",
                color = SlateGrayText.copy(alpha = 0.7f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = SlateGrayText) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            focusedBorderColor = colorResource(id = R.color.logo_cyan),
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedTextColor = DarkCharcoalText,
            unfocusedTextColor = DarkCharcoalText,
            cursorColor = colorResource(id = R.color.logo_cyan)
        ),
        singleLine = true
    )
}

@Composable
fun GridBackgroundPhone() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 50.dp.toPx()
        val width = size.width
        val height = size.height

        for (x in 0..width.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.Black.copy(alpha = 0.03f),
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 1f
            )
        }

        for (y in 0..height.toInt() step gridSize.toInt()) {
            drawLine(
                color = Color.Black.copy(alpha = 0.03f),
                start = Offset(0f, y.toFloat()),
                end = Offset(width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}
