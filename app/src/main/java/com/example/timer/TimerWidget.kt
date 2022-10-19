package com.example.timer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.timer.services.*
import java.util.*

class TimerWidget : AppWidgetProvider() {

    companion object {
        var time = ""
        var isTimerStarted = false
        var isTimerRunning = true
        var isBackEnabled = true
        var isForwardEnabled = true
        var isPauseEnabled = true
        var isResumeEnabled = true
        var title = ""
    }

    var timer = Timer()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            startTimer(context, appWidgetManager, appWidgetId);
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun startTimer(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                val language = preferences.getString("language", "en")
                val locale = Locale(language!!)
                Locale.setDefault(locale)
                val configuration = context.resources.configuration
                configuration.setLocale(locale)
                val resources = context.resources
                resources.updateConfiguration(configuration, resources.displayMetrics)
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }, 0, 100)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.timer_widget)

    views.setTextViewText(R.id.back, context.getString(R.string.notification_back))
    views.setTextViewText(R.id.pause, context.getString(R.string.notification_pause))
    views.setTextViewText(R.id.resume, context.getString(R.string.notification_resume))
    views.setTextViewText(R.id.forward, context.getString(R.string.notification_forward))

    if (TimerWidget.isTimerStarted) {
        views.setTextViewText(R.id.appwidget_text, TimerWidget.time)
        views.setTextViewText(R.id.textView, TimerWidget.title)
        views.setViewVisibility(R.id.back, View.VISIBLE)
        views.setViewVisibility(R.id.pause, View.VISIBLE)
        views.setViewVisibility(R.id.resume, View.INVISIBLE)
        views.setViewVisibility(R.id.forward, View.VISIBLE)

        if (TimerWidget.isTimerRunning) {
            views.setViewVisibility(R.id.pause, View.VISIBLE)
            views.setViewVisibility(R.id.resume, View.INVISIBLE)
        } else {
            views.setViewVisibility(R.id.pause, View.INVISIBLE)
            views.setViewVisibility(R.id.resume, View.VISIBLE)
        }

        if (TimerWidget.isBackEnabled) {
            views.setViewVisibility(R.id.back, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.back, View.INVISIBLE)
        }
        if (TimerWidget.isForwardEnabled) {
            views.setViewVisibility(R.id.forward, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.forward, View.INVISIBLE)
        }
        if (!TimerWidget.isPauseEnabled) {
            views.setViewVisibility(R.id.pause, View.INVISIBLE)
        }
        if (!TimerWidget.isResumeEnabled) {
            views.setViewVisibility(R.id.resume, View.INVISIBLE)
        }
    } else {
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.timer_finished))
        views.setTextViewText(R.id.textView, "")
        views.setViewVisibility(R.id.back, View.INVISIBLE)
        views.setViewVisibility(R.id.pause, View.INVISIBLE)
        views.setViewVisibility(R.id.resume, View.INVISIBLE)
        views.setViewVisibility(R.id.forward, View.INVISIBLE)
    }

    val backIntent = Intent(context, TimerService::class.java).apply {
        putExtra(INTENT_COMMAND, INTENT_COMMAND_BACK)
    }
    val backPendingIntent = PendingIntent.getService(
        context,
        CODE_BACK_INTENT,
        backIntent,
        0
    )
    views.setOnClickPendingIntent(R.id.back, backPendingIntent)

    val forwardIntent = Intent(context, TimerService::class.java).apply {
        putExtra(INTENT_COMMAND, INTENT_COMMAND_FORWARD)
    }
    val forwardPendingIntent = PendingIntent.getService(
        context,
        CODE_FORWARD_INTENT,
        forwardIntent,
        0
    )
    views.setOnClickPendingIntent(R.id.forward, forwardPendingIntent)

    val pauseIntent = Intent(context, TimerService::class.java).apply {
        putExtra(INTENT_COMMAND, INTENT_COMMAND_PAUSE)
    }
    val pausePendingIntent = PendingIntent.getService(
        context,
        CODE_PAUSE_INTENT,
        pauseIntent,
        0
    )
    views.setOnClickPendingIntent(R.id.pause, pausePendingIntent)

    val resumeIntent = Intent(context, TimerService::class.java).apply {
        putExtra(INTENT_COMMAND, INTENT_COMMAND_RESUME)
    }
    val resumePendingIntent = PendingIntent.getService(
        context,
        CODE_RESUME_INTENT,
        resumeIntent,
        0
    )
    views.setOnClickPendingIntent(R.id.resume, resumePendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
