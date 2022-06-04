package com.casoca.befriend.utilidades

import android.app.Application

class BeFriendApplication: Application(){
    companion object{
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}