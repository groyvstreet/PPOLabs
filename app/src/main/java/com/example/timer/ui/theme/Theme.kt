package com.example.timer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = DarkGreen,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color(0xFF0F0F0F),
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorPalette = lightColors(
    primary = LightGreen,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun TimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontSize: Int = 16,
    content: @Composable () -> Unit
) {

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = if (darkTheme) {
            Grey900.toArgb()
        } else {
            Grey50.toArgb()
        }
        window.navigationBarColor = if (darkTheme) {
            Grey900.toArgb()
        } else {
            Grey50.toArgb()
        }

        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightNavigationBars = darkTheme
    }
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography(
            subtitle1 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            subtitle2 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            body1 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            body2 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            button = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            caption = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            ),
            overline = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = fontSize.sp
            )
        ),
        shapes = Shapes,
        content = content
    )
}
