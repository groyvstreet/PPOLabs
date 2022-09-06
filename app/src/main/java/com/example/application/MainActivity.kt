package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.application.ui.theme.ApplicationTheme
import com.example.application.components.TopNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                ConverterApp()
            }
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ConverterApp() {
    val screens = listOf("Data", "Length", "Mass")
    TopNavigationBar(screens = screens)
}