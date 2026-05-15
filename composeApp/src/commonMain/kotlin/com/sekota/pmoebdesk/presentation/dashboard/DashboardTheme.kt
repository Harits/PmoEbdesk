package com.sekota.pmoebdesk.presentation.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryNavy = Color(0xFF000666)
val SidebarBackground = Color(0xFFECEEF4)
val MainBackground = Color(0xFFF8F9FB)
val SurfaceWhite = Color(0xFFFFFFFF)
val SelectedNav = Color(0xFFB4C5FF)
val OnSurfaceDark = Color(0xFF191C1D)
val OutlineVariant = Color(0xFFC6C5D4)

val StatusGreenText = Color(0xFF2E7D32)
val StatusRedText = Color(0xFFC62828)
val StatusAmberText = Color(0xFFF57C00)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        onPrimary = Color.White,
        surface = SurfaceWhite,
        onSurface = OnSurfaceDark,
        background = MainBackground,
        onBackground = OnSurfaceDark,
        outlineVariant = OutlineVariant
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
