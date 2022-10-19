package com.example.timer.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.timer.MainActivity
import com.example.timer.R
import com.example.timer.TimerWidget
import com.example.timer.models.Element
import com.example.timer.utils.foregroundStartService
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer

const val INTENT_COMMAND = "Command"
const val INTENT_COMMAND_BACK = "Back"
const val INTENT_COMMAND_FORWARD = "Forward"
const val INTENT_COMMAND_RESUME = "Resume"
const val INTENT_COMMAND_PAUSE = "Pause"
const val INTENT_COMMAND_START = "Start"
const val INTENT_COMMAND_EXIT = "Exit"

private const val NOTIFICATION_CHANNEL_GENERAL = "Checking"
private const val CODE_FOREGROUND_SERVICE = 1
const val CODE_BACK_INTENT = 2
const val CODE_FORWARD_INTENT = 3
const val CODE_RESUME_INTENT = 4
const val CODE_PAUSE_INTENT = 5
private const val CODE_CLICK_INTENT = 6

class TimerService : Service() {

    private val binder = TimerBinder()

    private lateinit var timer: Timer

    lateinit var toast: Toast
    lateinit var lastSecondsMP: MediaPlayer
    lateinit var newPhaseMP: MediaPlayer

    lateinit var context: Context

    var stringTimerStarted = ""
    var stringTimerFinished = ""
    var stringTimerBack = ""
    var stringTimerPause = ""
    var stringTimerResume = ""
    var stringTimerForward = ""

    var phases: List<Element> = listOf()
    var current = mutableStateOf(Element(id = "0", time = 0))
    private var index = mutableStateOf(-1)
    var isTimerRunning = mutableStateOf(true)
    private var currentTime = mutableStateOf(0)
    var isBackEnabled = mutableStateOf(false)
    var isForwardEnabled = mutableStateOf(true)
    var isServiceRunning = mutableStateOf(false)
    var sequenceId = mutableStateOf("null")
    var color = mutableStateOf(Color(0xFFFFFFFF))

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra(INTENT_COMMAND)
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        if (command == INTENT_COMMAND_START) {
            isServiceRunning.value = true
            toast.cancel()
            toast.setText(stringTimerStarted)
            toast.show()
            val preferences: SharedPreferences =
                getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("isLoading", false)
            editor.apply()
            clickPendingIntent()?.send()
            startTimer()
            TimerWidget.isTimerStarted = true
        } else if (command == INTENT_COMMAND_BACK) {
            goBack()
        } else if (command == INTENT_COMMAND_FORWARD) {
            goForward()
        } else if (command == INTENT_COMMAND_PAUSE) {
            pauseTimer()
        } else if (command == INTENT_COMMAND_RESUME) {
            resumeTimer()
        }

        showNotification()

        return START_STICKY
    }

    private fun stopService() {
        isServiceRunning.value = false
        toast.cancel()
        toast.setText(stringTimerFinished)
        toast.show()
        stopTimer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        TimerWidget.isTimerStarted = false
        val intent = Intent(context, MainActivity::class.java)
        //startForegroundService(intent)
        /*val pendingIntent = androidx.core.app.TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(CODE_CLICK_INTENT, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        pendingIntent?.send()*/
        context.startActivity(intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun clickPendingIntent(): PendingIntent? {
        val clickIntent = Intent(
            Intent.ACTION_VIEW,
            "https://example.com/sequenceId=${sequenceId}".toUri(),
            this,
            MainActivity::class.java
        )
        return androidx.core.app.TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(clickIntent)
            getPendingIntent(CODE_CLICK_INTENT, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun backPendingIntent(): PendingIntent {
        val backIntent = Intent(this, TimerService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_BACK)
        }
        return PendingIntent.getService(
            this,
            CODE_BACK_INTENT,
            backIntent,
            0
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun forwardPendingIntent(): PendingIntent {
        val forwardIntent = Intent(this, TimerService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_FORWARD)
        }
        return PendingIntent.getService(
            this,
            CODE_FORWARD_INTENT,
            forwardIntent,
            0
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun pausePendingIntent(): PendingIntent {
        val pauseIntent = Intent(this, TimerService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_PAUSE)
        }
        return PendingIntent.getService(
            this,
            CODE_PAUSE_INTENT,
            pauseIntent,
            0
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun resumePendingIntent(): PendingIntent {
        val resumeIntent = Intent(this, TimerService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_RESUME)
        }
        return PendingIntent.getService(
            this,
            CODE_RESUME_INTENT,
            resumeIntent,
            0
        )
    }

    @SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
    private fun showNotification() {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.UP

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(
            NotificationChannel(
                NOTIFICATION_CHANNEL_GENERAL,
                "Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        ) {
            enableLights(false)
            setShowBadge(false)
            enableVibration(false)
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(this)
        }

        with(
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_GENERAL)
        ) {
            setTicker(null)
            val number = if (current.value.id == "null") {
                0
            } else {
                current.value.id.toInt() + 1
            }
            setContentTitle("${number}/${phases.size} ${current.value.title}")
            val hours = current.value.time / 1000 / 3600
            val minutes = (current.value.time / 1000 - hours * 3600) / 60
            val seconds = current.value.time / 1000 - hours * 3600 - minutes * 60
            //setContentText(df.format(current.value.time.toFloat() / 1000).toInt().toString())
            setContentText(
                "${
                    if (hours < 10) {
                        0
                    } else {
                        ""
                    }
                }${hours}:${
                    if (minutes < 10) {
                        0
                    } else {
                        ""
                    }
                }${minutes}:${
                    if (seconds < 10) {
                        0
                    } else {
                        ""
                    }
                }${seconds}"
            )
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.ic_launcher_foreground)
            priority = Notification.PRIORITY_MAX
            setContentIntent(clickPendingIntent())
            if (isBackEnabled.value) {
                addAction(
                    0, stringTimerBack, backPendingIntent()
                )
            }
            if (isBackEnabled.value || isForwardEnabled.value) {
                if (isTimerRunning.value) {
                    addAction(
                        0, stringTimerPause, pausePendingIntent()
                    )
                } else {
                    addAction(
                        0, stringTimerResume, resumePendingIntent()
                    )
                }
            }
            if (isForwardEnabled.value) {
                addAction(
                    0, stringTimerForward, forwardPendingIntent()
                )
            }
            startForeground(CODE_FOREGROUND_SERVICE, build())
        }
    }

    private fun startTimer() {
        isTimerRunning.value = true
        TimerWidget.isTimerRunning = true
        var isFirst = true
        timer = fixedRateTimer(initialDelay = 10L, period = 1000L) {
            var time = current.value.time
            if (isFirst) {
                time += 1000
                isFirst = false
            }
            if (time > 0) {
                time -= 1000
            }
            if (time in 1000..3000) {
                lastSecondsMP.start()
            }
            if (time == 0) {
                newPhaseMP.start()
                index.value += 1
                isBackEnabled.value = index.value > 0 && index.value < phases.size
                isForwardEnabled.value = index.value < phases.size
                if (index.value >= phases.size) {
                    current.value = Element(id = "null", title = "ФИНИШ", time = 0)
                    isTimerRunning.value = false
                    if (index.value >= phases.size + 3) {
                        foregroundStartService("Exit")
                    }
                } else {
                    current.value = phases[index.value]
                }
            } else {
                current.value =
                    Element(id = current.value.id, title = current.value.title, time = time)
            }
            showNotification()
            TimerWidget.isBackEnabled = isBackEnabled.value
            TimerWidget.isForwardEnabled = isForwardEnabled.value
            TimerWidget.isPauseEnabled = isBackEnabled.value || isForwardEnabled.value
            TimerWidget.isForwardEnabled = isBackEnabled.value || isForwardEnabled.value
            val currentHours = current.value.time / 1000 / 3600
            val currentMinutes = (current.value.time / 1000 - currentHours * 3600) / 60
            val currentSeconds =
                current.value.time / 1000 - currentHours * 3600 - currentMinutes * 60
            TimerWidget.time = "${
                if (currentHours < 10) {
                    0
                } else {
                    ""
                }
            }${currentHours}:${
                if (currentMinutes < 10) {
                    0
                } else {
                    ""
                }
            }${currentMinutes}:${
                if (currentSeconds < 10) {
                    0
                } else {
                    ""
                }
            }${currentSeconds}"
            val number = if (current.value.id == "null") {
                0
            } else {
                current.value.id.toInt() + 1
            }
            val title = if (current.value.title.length > 25) {
                "${number}/${phases.size} ${current.value.title.subSequence(0, 24)}..."
            } else {
                "${number}/${phases.size} ${current.value.title}"
            }
            TimerWidget.title = title
            /*val intent = Intent()
            intent.action = "timer.action.CURRENT_TIME"
            intent.putExtra("timer.broadcast.Message", current.value.time.toString())
            //intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            sendBroadcast(intent)*/
        }
    }

    private fun stopTimer() {
        isTimerRunning.value = false
        TimerWidget.isTimerRunning = false
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        showNotification()
    }

    fun goBack() {
        stopTimer()
        index.value -= 1
        isBackEnabled.value = index.value > 0 && index.value < phases.size
        isForwardEnabled.value = index.value < phases.size
        current.value = phases[index.value]
        startTimer()
        showNotification()
    }

    fun goForward() {
        stopTimer()
        index.value += 1
        isBackEnabled.value = index.value > 0 && index.value < phases.size
        isForwardEnabled.value = index.value < phases.size
        if (index.value >= phases.size) {
            current.value = Element(id = "null", title = "ФИНИШ", time = 0)
            startTimer()
            isTimerRunning.value = false
        } else {
            current.value = phases[index.value]
            startTimer()

        }
    }

    fun selectPhase(index: Int) {
        stopTimer()
        this.index.value = index
        isBackEnabled.value = this.index.value > 0 && this.index.value < phases.size
        isForwardEnabled.value = this.index.value < phases.size
        if (index == phases.size) {
            current.value = Element(id = "null", title = "ФИНИШ", time = 0)
            startTimer()
            isTimerRunning.value = false
        } else {
            current.value = phases[index]
            startTimer()

        }
        showNotification()
    }

    fun pauseTimer() {
        stopTimer()
        currentTime.value = current.value.time
        showNotification()
    }

    fun resumeTimer() {
        current.value.time = currentTime.value
        startTimer()
        showNotification()
    }

    fun restartService() {
        current.value = Element(id = "null", time = 0)
        index.value = -1
        isTimerRunning.value = true
        currentTime.value = 0
        isBackEnabled.value = false
        isForwardEnabled.value = true
        isServiceRunning.value = false
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}
