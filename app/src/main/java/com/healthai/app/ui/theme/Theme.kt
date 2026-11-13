package com.healthai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OffWhiteBackground = Color(0xFFF8FAFC)
val CardBorderGray = Color(0xFFE2E8F0)
val MedicalTeal = Color(0xFF0D9488)
val SoftTealTint = Color(0xFFCCFBF1)
val DarkTealText = Color(0xFF115E59)
val FriendlyRoyalBlue = Color(0xFF2563EB)
val DarkCharcoalText = Color(0xFF1E293B)
val SlateGrayText = Color(0xFF475569)
val CoralRed = Color(0xFFDC2626)

private val UserFriendlyColorScheme = lightColorScheme(
    primary = MedicalTeal,
    secondary = FriendlyRoyalBlue,
    tertiary = Color(0xFF0284C7),
    background = OffWhiteBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkCharcoalText,
    onSurface = DarkCharcoalText,
)

@Composable
fun HelpixTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UserFriendlyColorScheme,
        content = content
    )
}
