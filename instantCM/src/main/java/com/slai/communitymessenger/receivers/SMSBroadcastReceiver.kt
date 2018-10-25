package com.slai.communitymessenger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.slai.communitymessenger.model.events.SMSReceivedEvent
import org.greenrobot.eventbus.EventBus


class SMSBroadcastReceiver() : BroadcastReceiver() {

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

            EventBus.getDefault().post(SMSReceivedEvent(smsBody, smsSender))
//            if (smsSender == serviceProviderNumber && smsBody.startsWith(serviceProviderSmsCondition)) {
//                if (listener != null) {
//                    listener.onTextReceived(smsBody)
//                }
//            }
        }
    }
}