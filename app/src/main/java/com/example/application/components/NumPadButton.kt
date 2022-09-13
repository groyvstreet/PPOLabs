package com.example.application.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp

@Composable
fun NumPadButton(
    text: String,
    onClick: () -> Unit = {},
    width: Float = 1.0f,
    height: Float = 1.0f
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(width)
            .fillMaxHeight(height)
    ) {
        Text(
            text = text,
            fontSize = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                36.sp
            } else {
                18.sp
            }
        )
    }
}