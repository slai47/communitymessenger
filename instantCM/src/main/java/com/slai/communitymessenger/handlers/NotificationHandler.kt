package com.slai.communitymessenger.handlers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHandler(val context : Context?) {

    companion object {
        @JvmField val CHANNEL_ID = "474747474"
    }


    fun setup(){
        var service : NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Main Notification", NotificationCompat.PRIORITY_DEFAULT)
            notificationChannel.description = "All text/MMS notifications"

            service.createNotificationChannel(notificationChannel)
        }
    }


}