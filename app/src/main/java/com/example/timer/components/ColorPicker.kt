package com.example.timer.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.*

@Composable
fun ColorPicker(onColorChanged: (String) -> Unit) {
    val controller = rememberColorPickerController()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaTile(
                controller = controller,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
                    .clip(RoundedCornerShape(6.dp))
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        HsvColorPicker(
            controller = controller,
            onColorChanged = { onColorChanged(it.hexCode) },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        BrightnessSlider(
            controller = controller,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
        )
    }
}
