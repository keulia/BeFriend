package com.casoca.befriend.utilidades

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import com.casoca.befriend.HomeActivity
import com.casoca.befriend.R


class BeFriendApplication: Application(){
    companion object{
        lateinit var prefs: Prefs
        lateinit var instance: BeFriendApplication
            private set
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        prefs = Prefs(applicationContext)

        NotificationUtils(this).createNotificationChannel(this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")

    }
}