package com.casoca.befriend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cambiarActivity()
        setContentView(R.layout.activity_splash_screen)
    }

    private fun cambiarActivity(){
        object : CountDownTimer(800,100){
            override fun onTick(p0: Long) {}

            override fun onFinish() {
                startActivity(Intent(this@SplashScreen,MainActivity::class.java))
                finish()
            }
        }.start()
    }
}