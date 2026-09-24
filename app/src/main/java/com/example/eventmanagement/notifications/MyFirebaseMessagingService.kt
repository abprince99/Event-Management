package com.example.eventmanagement.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.eventmanagement.R
import com.example.eventmanagement.auth.data.repository.AuthRepository
import com.example.eventmanagement.events.ui.Dashboard
import com.example.eventmanagement.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService: FirebaseMessagingService() {


    private val authRepository = AuthRepository()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Event Reminder"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "You have an upcoming event."
        showNotification( title, body )
    }
    private fun showNotification(title: String, body: String ) {
        val intent = Intent(this, Dashboard::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            this,
            NotificationHelper.CHANNEL_ID )
            .setSmallIcon( R.drawable.ic_event_noti )
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setPriority(
                NotificationCompat.PRIORITY_HIGH
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU ) {
            if ( checkSelfPermission( Manifest.permission.POST_NOTIFICATIONS )
                != PackageManager.PERMISSION_GRANTED ) {
                return
            }
        }
        NotificationManagerCompat.from(this)
            .notify( System.currentTimeMillis().toInt(),
                notification
            )
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        saveToken(token)
    }

    private fun saveToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.saveFcmToken(token)
        }
    }
}