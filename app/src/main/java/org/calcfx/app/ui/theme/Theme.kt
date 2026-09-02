package org.calcfx.app.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalAppAccent = compositionLocalOf { AccentCyan }

@Composable
fun CalcFXTheme(
    selectedAccent: AccentTheme = AccentTheme.DYNAMIC,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val resolvedAccent: Color = remember(selectedAccent) {
        if (selectedAccent == AccentTheme.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val dynamicColors = dynamicDarkColorScheme(context)
                dynamicColors.primary
            } catch (_: Exception) {
                selectedAccent.color
            }
        } else {
            selectedAccent.color
        }
    }

    val amoledColorScheme = darkColorScheme(
        primary = resolvedAccent,
        onPrimary = Color.Black,
        primaryContainer = resolvedAccent.copy(alpha = 0.2f),
        onPrimaryContainer = Color.White,
        secondary = resolvedAccent,
        onSecondary = Color.Black,
        tertiary = AccentGold,
        background = AmoledBlack,
        surface = AmoledSurface,
        surfaceVariant = AmoledFunctionKey,
        onBackground = AmoledTextPrimary,
        onSurface = AmoledTextPrimary,
        onSurfaceVariant = AmoledTextSecondary,
        outline = AmoledCardBorder
    )

    CompositionLocalProvider(LocalAppAccent provides resolvedAccent) {
        MaterialTheme(
            colorScheme = amoledColorScheme,
            typography = Typography,
            content = content
        )
    }
}
