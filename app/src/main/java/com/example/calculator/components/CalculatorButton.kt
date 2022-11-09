package com.example.calculator.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import com.example.calculator.viewModels.CalculatorViewModel

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String = "",
    withIcon: Boolean = true,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(100),
        colors = ButtonDefaults.buttonColors(
            contentColor = if (isSystemInDarkTheme()) {
                Color.Magenta
            } else {
                Color.White
            }
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        if (withIcon) {
            Icon(
                imageVector = imageVector,
                contentDescription = ""
            )
        } else {
            Text(
                text = text,
                fontSize = if (CalculatorViewModel.scientificMode) {
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        12.sp
                    } else {
                        14.sp
                    }
                } else {
                    20.sp
                }
            )
        }
    }
}
