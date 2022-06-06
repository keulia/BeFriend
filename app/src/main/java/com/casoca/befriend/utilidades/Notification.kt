package com.casoca.befriend.utilidades

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


const val notificationID = 1
const val channelID = "all_notifications"
const val titleExtra = "titleExtra"
const val messageExtra = "messageextra"


class Notification: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == "my.action.reminder") {
            val id = intent.extras!!.getInt("id")
            val notificationText = intent.extras!!.getString("username")
            val title = intent.extras!!.getString("title")
            val idContact = intent.extras!!.getInt("idContact")

            val notificationUtils = NotificationUtils(context)
            val notificationObject = notificationUtils.setNotification(title, notificationText).build()
            notificationUtils.manager!!.notify(idContact, notificationObject)
        }


    }
}