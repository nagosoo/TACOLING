package com.eundmswlji.tacoling.presentation.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.presentation.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "LOGGING"

    //백그라운드에서 푸시 알림을 받을때 포그라운드와 동일한 처리를 해주고 싶으면 handleIntent를 override 해야함
    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                val builder = RemoteMessage.Builder("MyFirebaseMessagingService")
                for (key in intent.extras!!.keySet()) {
                    print("key: $key")
                    builder.addData(key!!, intent.extras!!.getString(key))
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("fcmTest", "${message.notification?.title}")
        Log.d("fcmTest", "${message.notification?.body}")
        Log.d("fcmTest", "${message.data["shopId"]}")
        createNotificationChannel() //채널 만들기
        message.notification?.let {
            with(NotificationManagerCompat.from(this)) {
                notify(
                    Random.nextInt(),
                    createNotificationNormal(it.title ?: "알림", it.body ?: "알림입니다.", message.data)
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_id)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val id = getString(R.string.channel_id)
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationNormal(
        title: String,
        content: String,
        data: Map<String, String>?
    ): Notification {

        val intent = Intent(this, MainActivity::class.java).apply {
            if (!data.isNullOrEmpty()) {
                val args = Bundle()
                args.putString("shopId", data["shopId"])
                putExtra("bundle", args)
            }
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this,
                    100,
                    intent,
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    100,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        return NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_taco)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setChannelId(getString(R.string.channel_id))
            .setContentIntent(pendingIntent)
            .build()
    }

}