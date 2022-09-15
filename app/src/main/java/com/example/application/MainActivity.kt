package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.application.ui.theme.ApplicationTheme
import com.example.application.components.TopNavigationBar
import com.example.application.viewModels.ConverterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val converterViewModel: ConverterViewModel by viewModels()
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                ConverterApp(converterViewModel)
            }
        }
    }
}

@Composable
fun ConverterApp(converterViewModel: ConverterViewModel) {
    val screens = listOf("Data", "Length", "Mass")
    TopNavigationBar(screens = screens, converterViewModel)
}