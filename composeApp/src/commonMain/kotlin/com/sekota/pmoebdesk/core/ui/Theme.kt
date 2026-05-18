package com.sekota.pmoebdesk.core.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Stitch Design Tokens
val PrimaryNavy = Color(0xFF000666)
val MainBackground = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfaceDark = Color(0xFF191C1D)
val OutlineColor = Color(0xFF767683)
val OutlineVariant = Color(0xFFC6C5D4)
val ErrorColor = Color(0xFFBA1A1A)

val StatusGreen = Color(0xFF4CAF50)
val StatusGreenBackground = Color(0xFFE8F5E9)
val StatusAmber = Color(0xFFF57C00)
val StatusAmberBackground = Color(0xFFFFF3E0)
val StatusRed = Color(0xFFD32F2F)
val StatusRedBackground = Color(0xFFFFEBEE)
val StatusBlue = Color(0xFF2196F3)
val StatusBlueBackground = Color(0xFFE3F2FD)
val StatusPurple = Color(0xFF9C27B0)
val StatusPurpleBackground = Color(0xFFF3E5F5)
val StatusBrown = Color(0xFF795548)
val StatusBrownBackground = Color(0xFFEFEBE9)
val CardDarkNavy = Color(0xFF000A3A)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        onPrimary = Color.White,
        surface = SurfaceWhite,
        onSurface = OnSurfaceDark,
        background = MainBackground,
        onBackground = OnSurfaceDark,
        outlineVariant = OutlineVariant,
    )

    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontSize = 57.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryNavy,
            lineHeight = 40.sp
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}
