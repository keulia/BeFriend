package com.catata.notificationsexample


import android.app.*
import android.app.Notification
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import com.casoca.befriend.MainActivity
import com.casoca.befriend.R
import com.casoca.befriend.utilidades.*
import kotlinx.coroutines.*
import kotlin.random.Random

class NotificationsTest :AppCompatActivity(){




    private val pendingIntent: PendingIntent by lazy { makePendingIntent() }
    private val pendingIntentYes: PendingIntent by lazy { makePendingIntentYes() }
    private val pendingIntentNo: PendingIntent by lazy { makePendingIntentNo() }

    private  lateinit var image :ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_events)



       image.setOnClickListener{
            sendNotification()
        }
    }

    private fun sendNotification() {
        CoroutineScope(Dispatchers.Default).launch {
            delay(6000L)
            makeNotificationChannel()
            makeNotificaton()
        }
    }



    //Crear una NotificationChannel
    private fun makeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            //Notificacion es visible en el icono de la app
            notificationChannel.setShowBadge(true)

            //Crea la notificacion
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun makeNotificaton() {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID)


        with(builder){
            setSmallIcon(R.drawable.ic_baseline_access_alarm)
            setContentTitle("Notification Example")
            setContentText("This is my notification")
            setStyle(NotificationCompat.BigTextStyle()
                .bigText("This is a long text that can't it into a single line. This is a long text that can't it into a single line. This is a long text that can't it into a single line. This is a long text that can't it into a single line"))


            color = Color.RED
            priority = NotificationCompat.PRIORITY_DEFAULT

            //1 sec on 1 sec off
            setLights(Color.MAGENTA, 1000, 1000)

            //1 sec on 1 sec off 1 sec on 1 sec off
            setVibrate(longArrayOf(1000, 1000, 1000, 1000))

            //Poner sonido
            setDefaults(Notification.DEFAULT_SOUND)

            //Al hacer click en la notificacion
            setContentIntent(pendingIntent)

            setVisibility(VISIBILITY_PUBLIC)

            addAction(R.drawable.ic_yes, "Yes", pendingIntentYes)
            addAction(R.drawable.ic_no, "No", pendingIntentNo)


            setFullScreenIntent(pendingIntent, true)

            //setTimeoutAfter(5000L) //cancela la notification despues 5 sec
            setAutoCancel(true)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(
            this)

        //ejecutar notificacion
        notificationManagerCompat.notify(NOTIFICATION_ID , builder.build())
    }

    private fun makePendingIntent():PendingIntent {
        val intent = Intent(this, MainActivity::class.java)

        return PendingIntent.getActivity(this, PENDING_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun makePendingIntentYes():PendingIntent {
        //Intent real
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(YES_EXTRA_KEY, "This text goes to Yes Activity")
        }


        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)

        return stackBuilder.getPendingIntent(PENDING_REQUEST,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun makePendingIntentNo():PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        return PendingIntent.getActivity(this, 0, intent, 0)
    }


    private suspend fun sendMessage(text:String) = withContext(Dispatchers.Main){
        Toast.makeText(this@NotificationsTest,text, Toast.LENGTH_SHORT).show()
    }





}