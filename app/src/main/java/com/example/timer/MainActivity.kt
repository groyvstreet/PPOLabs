package com.example.timer

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.timer.components.TimerScaffold
import com.example.timer.data.TimerDatabase
import com.example.timer.services.TimerService
import com.example.timer.ui.theme.TimerTheme
import com.example.timer.utils.foregroundStartService
import com.example.timer.viewModels.SequenceListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var timerDatabase: TimerDatabase

    private lateinit var timerService: TimerService
    private var isBound by mutableStateOf(false)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            timerService.context = this@MainActivity
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isLoading = mutableStateOf(true)
        val viewModel: SequenceListViewModel by viewModels()
        installSplashScreen().setKeepOnScreenCondition { viewModel.isLoading.value && isLoading.value }
        setContent {
            val bool = preferences.getInt("dark_theme", 0) != 0
            var isDarkTheme by remember { mutableStateOf(bool) }
            var fontSize by remember { mutableStateOf(preferences.getString("font_size", "16")) }
            var language by remember { mutableStateOf(preferences.getString("language", "en")) }
            val locale = Locale(language!!)
            Locale.setDefault(locale)
            val configuration = LocalConfiguration.current
            configuration.setLocale(locale)
            val resources = LocalContext.current.resources
            resources.updateConfiguration(configuration, resources.displayMetrics)

            fun updateIsDarkTheme() {
                isDarkTheme = preferences.getInt("dark_theme", 0) != 0
            }

            fun updateFontSize() {
                fontSize = preferences.getString("font_size", "16")
            }

            fun updateLanguage() {
                language = preferences.getString("language", "en")
            }

            TimerTheme(
                darkTheme = isDarkTheme,
                fontSize = fontSize!!.toInt()
            ) {
                if (isBound) {
                    TimerScaffold(
                        viewModel = viewModel,
                        timerService = timerService,
                        isDarkTheme = isDarkTheme,
                        fontSize = fontSize!!,
                        language = language!!,
                        updateIsDarkTheme = ::updateIsDarkTheme,
                        updateFontSize = ::updateFontSize,
                        updateLanguage = ::updateLanguage,
                        clearData = timerDatabase::clearAllTables
                    )
                }
                LaunchedEffect(key1 = isLoading) {
                    isLoading.value = false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.activity.ComponentActivity")
    )
    override fun onBackPressed() {
        if (timerService.isServiceRunning.value) {
            this.foregroundStartService("Exit")
        }
        super.onBackPressed()
    }
}
