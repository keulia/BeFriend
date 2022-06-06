package com.casoca.befriend.utilidades

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.casoca.befriend.R


class NotificationUtils(base: Context) : ContextWrapper(base) {
    private var _notificationManager: NotificationManager? = null
    private val _context: Context
    private val CHANNEL_ID = "notification channel";

    private val TIMELINE_CHANNEL_NAME = "Timeline notification";
    fun setNotification(title: String?, body: String?): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_yes)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }
    fun createNotificationChannel(
        context: Context,
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {

        // Crear el NotificationChannel

        val channelId = "${context.packageName}-$name"
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.setShowBadge(showBadge)

        // Registrar el canal con el sistema
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                TIMELINE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager!!.createNotificationChannel(channel)
        }
    }

    val manager: NotificationManager?
        get() {
            if (_notificationManager == null) {
                _notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return _notificationManager
        }

    fun setReminder(notificationText: String, repeatFrequencyInMilliSec: Long, idContact: Int) {
        val intent = Intent("my.action.reminder")
        intent.setClass(_context, com.casoca.befriend.utilidades.Notification::class.java)
        // Valores para cambiar la notificacion a mostrar
        intent.putExtra("username", notificationText)
        intent.putExtra("title","Hey!")
        intent.putExtra("idContact",idContact)
        val currentTime = System.currentTimeMillis()

        val pendingIntent = PendingIntent.getBroadcast(_context, currentTime.toInt(), intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime+repeatFrequencyInMilliSec , repeatFrequencyInMilliSec , pendingIntent) // Millisec * Second * Minute

    }

    init {
        _context = base
        createChannel()
    }


}