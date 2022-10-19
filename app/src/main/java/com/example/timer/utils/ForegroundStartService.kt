package com.example.timer.utils

import android.content.Context
import android.content.Intent
import com.example.timer.services.INTENT_COMMAND
import com.example.timer.services.TimerService

fun Context.foregroundStartService(command: String) {

    val intent = Intent(this, TimerService::class.java)

    if (command == "Start") {
        intent.putExtra(INTENT_COMMAND, command)
        this.startForegroundService(intent)
    } else if (command == "Exit") {
        intent.putExtra(INTENT_COMMAND, command)
        this.startForegroundService(intent)
    }
}
