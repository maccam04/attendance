package com.macsanityapps.virtualattendance.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.macsanityapps.virtualattendance.R

class MessagingService : FirebaseMessagingService() {

    companion object {

        private val TAG = MessagingService::class.java.canonicalName
    }

    var notificationManager : NotificationManagerCompat? = null;

    override fun onCreate() {
        super.onCreate()

        notificationManager = NotificationManagerCompat.from(this)

        createNotification()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)
        Log.d(TAG, "From: " + remoteMessage.data!!)

        showNotification(remoteMessage.data["title"].toString(), remoteMessage.data["content"].toString())

    }


    private fun createNotification(){

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            val channel = NotificationChannel("attendanceChannel", "Test Channel", NotificationManager.IMPORTANCE_HIGH)

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title : String, content : String){

        val notification = NotificationCompat.Builder(this, "attendanceChannel")
        with(notification){
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(content)
        }

        notificationManager?.notify(1, notification.build())
    }
}

