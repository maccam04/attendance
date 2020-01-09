package com.macsanityapps.virtualattendance.util

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

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.macsanityapps.virtualattendance.R

class MessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from!!)

        // Check if message contains a data payload.


        sendNotification(remoteMessage.data)
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }

    }

    private fun sendNotification(data: MutableMap<String, String>) {
        //**add this line**

      /*  val requestID = System.currentTimeMillis().toInt()
        intent.action = "sendByNotif"
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        val pendingIntent = PendingIntent.getActivity(
            this, requestID *//* Request code *//*, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )*/

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder: NotificationCompat.Builder

        /* = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_rewards)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(data["title"])
                .setColor(Color.BLUE)
                .setContentText(data["content"])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))

        } else {

            notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(data["title"])
                .setContentText(data["content"])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))

        }


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0  /* ID of notification */, notificationBuilder.build())
        // notificationManager.cancelAll();
    }

    companion object {

        private val TAG = MessagingService::class.java.canonicalName
    }
}

