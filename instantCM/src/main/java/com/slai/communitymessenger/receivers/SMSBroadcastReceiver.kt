package com.slai.communitymessenger.receivers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.NotificationHandler
import com.slai.communitymessenger.model.events.SMSReceivedEvent
import com.slai.communitymessenger.ui.MainActivity
import org.greenrobot.eventbus.EventBus


class SMSBroadcastReceiver : BroadcastReceiver() {

    companion object {
        @JvmField val REPLY_ACTION : String = "reply_action"
        @JvmField val NOTIFICATION_ID : Int = 4747
        @JvmField val EXTRA_NOTIFICATION_ID : String = "notificationId"
        @JvmField val EXTRA_NOTIFICATION_MESSAGE_ID : String = "messageId"
        @JvmField val KEY_REPLY: String = "reply"
    }

    val TAG : String = "SMSBroadcastReceiver"

    override fun onReceive(context : Context?, intent : Intent?) {
        if(intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            var smsSender = ""
            var smsBody = ""
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.displayOriginatingAddress
                smsBody += smsMessage.messageBody
            }
            Log.d(TAG, "onReceived sender = " + smsSender + " message = " + smsBody)

            // Alert user if in app
            if(EventBus.getDefault().hasSubscriberForEvent(SMSReceivedEvent::class.java))
                EventBus.getDefault().post(SMSReceivedEvent(smsBody, smsSender))
            else // Create Notification
                sendNotification(context, smsSender)
        }
    }

    private fun sendNotification(context: Context?, smsSender: String) {
        val builder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context!!, NotificationHandler.CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }

        val replyLabel = "Reply"
        val remoteInput = RemoteInput.Builder(KEY_REPLY)
            .setLabel(replyLabel)
            .build()

        builder.setContentTitle("")
        builder.setContentText("")
        builder.setShowWhen(true)

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_new_message,
            replyLabel,
            getReplyPendingIntent(context, smsSender)
        )
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()
        builder.addAction(replyAction)

        val service: NotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.notify(4747, builder.build())
    }

    private fun getReplyPendingIntent(context: Context?, messageId : String): PendingIntent? {
        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of
            // this codelab, will be doing a broadcast receiver
            intent = Intent(context, NotificationReplyReceiver::class.java)
            intent.action = REPLY_ACTION
            intent.putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID)
            intent.putExtra(EXTRA_NOTIFICATION_MESSAGE_ID, messageId)
            return PendingIntent.getBroadcast(
                context, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            // start your activity for Android M and below
            intent = Intent(context, MainActivity::class.java)
            intent.action = REPLY_ACTION
            intent.putExtra(EXTRA_NOTIFICATION_MESSAGE_ID, messageId)
            intent.putExtra(EXTRA_NOTIFICATION_ID, NOTIFICATION_ID)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return PendingIntent.getActivity(
                context, 100, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

    }
}