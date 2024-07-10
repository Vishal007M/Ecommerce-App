package com.antsglobe.restcommerse.Utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.ui.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            pushNotification(message.notification!!.title, message.notification!!.body)
        }
    }

    @SuppressLint("MissingPermission")
    private fun pushNotification(title: String?, body: String?) {

        var NOTIFICATION_CHANNEL_ID = "Testing"
        val INTENT_PENDING = System.currentTimeMillis().toInt()

        val nm = NotificationManagerCompat.from(this)
        val iNotify = Intent(applicationContext, HomeActivity::class.java)
        iNotify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pi = PendingIntent.getActivity(
            this, INTENT_PENDING, iNotify,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel Description"
            }
            nm.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setColorized(true)
            .setColor(ContextCompat.getColor(this, android.R.color.black))
            .setSmallIcon(R.drawable.notify_logo)
            .setSubText(title)
            .setContentText(body)
            .setContentIntent(pi)


        val notification = notificationBuilder.build()
        nm.notify(INTENT_PENDING, notification)

    }
}

