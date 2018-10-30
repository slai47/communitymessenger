package com.slai.communitymessenger.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.NotificationHandler


class NotificationReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent : Intent) {
        if (SMSBroadcastReceiver.REPLY_ACTION.equals(intent.action)) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            val message = getReplyMessage(intent)
            val messageId = intent.getIntExtra(SMSBroadcastReceiver.EXTRA_NOTIFICATION_MESSAGE_ID, 0)

            Log.d("NotificationReply", "message = " + message + " id = " + messageId)

            // update notification

            val service : NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val builder: NotificationCompat.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                builder = NotificationCompat.Builder(context, NotificationHandler.CHANNEL_ID)
            } else {
                builder = NotificationCompat.Builder(context)
            }

            builder.setSmallIcon(R.drawable.shape_notification)
                .setContentText("Sent")

            service.notify(SMSBroadcastReceiver.NOTIFICATION_ID, builder.build())
        }
    }

    private fun getReplyMessage(intent: Intent): CharSequence? {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return if (remoteInput != null) {
            remoteInput.getCharSequence(SMSBroadcastReceiver.KEY_REPLY)
        } else null
    }
}