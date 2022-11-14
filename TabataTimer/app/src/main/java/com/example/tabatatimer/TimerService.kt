package com.example.tabatatimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.math.roundToLong


class TimerService: Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    private var timer = Timer()
    private var time = 0

    private var repeatsAll = 0
    private var repeatsLeft = 0
    private var timerName = ""
    private var timerDescription = ""

    private val channelID = "myChannel.13"
    private var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        time = intent.getIntExtra(CURRENT_TIME, 0)
        repeatsAll = intent.getIntExtra(REPEATS_ALL, 0)
        repeatsLeft = intent.getIntExtra(REPEATS_LEFT, 0)
        timerName = intent.getStringExtra(TIMER_NAME)!!
        timerDescription = intent.getStringExtra(TIMER_DESCRIPTION)!!

        notificationManager = application?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "TabataChannel", "channel for tabata timer")

        timer.scheduleAtFixedRate(StopWatchTimerTask(time + 1), 0 ,1000)

        return START_NOT_STICKY
    }
    private fun displayNotification(contentForHeader: String, contentForFooter:String){
        val notificationId = 13
        val notification = Notification.Builder(application, channelID)
            .setContentTitle(contentForHeader)
            .setContentText(contentForFooter)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager?.notify(notificationId, notification)
    }
    private fun createNotificationChannel(id: String, name: String, channelDescription: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name,importance).apply{
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
    companion object{
        const val CURRENT_TIME = "current time"
        const val UPDATED_TIME = "updated time"
        const val REPEATS_ALL = "all repeats"
        const val REPEATS_LEFT = "repeats left"
        const val TIMER_NAME = "timer name"
        const val TIMER_DESCRIPTION = "timer description"
    }
    private inner class StopWatchTimerTask(private var time:Int): TimerTask(){
        override fun run() {
            val intent = Intent(UPDATED_TIME)

            --time
            intent.putExtra(CURRENT_TIME, time)

            displayNotification("$timerName: $time","Repeats: ${repeatsAll - repeatsLeft + 1}/${repeatsAll} \n$timerDescription")
            sendBroadcast(intent)
        }

    }
}