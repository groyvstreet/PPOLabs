package com.example.application.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(text = text, fontSize = 36.sp)
    }
}