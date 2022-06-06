package com.casoca.befriend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread

class MainActivity : AppCompatActivity() {

    private lateinit var handlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handlerThread = HandlerThread("BackgroundWorker")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }
}