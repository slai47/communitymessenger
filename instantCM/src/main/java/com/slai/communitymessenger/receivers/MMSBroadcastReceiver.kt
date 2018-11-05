package com.slai.communitymessenger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class MMSBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MMSBroadcastReceiver", intent?.action + " " + intent?.extras.toString())
        if(intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
        }
    }
}