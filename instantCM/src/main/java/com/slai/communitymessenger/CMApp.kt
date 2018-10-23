package com.slai.communitymessenger

import android.app.Application
import android.content.IntentFilter
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import com.slai.communitymessenger.receivers.SMSBroadcastReceiver


class CMApp : Application() {

    private var smsBroadcastReceiver: SMSBroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        smsBroadcastReceiver = SMSBroadcastReceiver()
        registerReceiver(smsBroadcastReceiver, IntentFilter(SMS_RECEIVED_ACTION))


    }

    override fun onTerminate() {
        unregisterReceiver(smsBroadcastReceiver)
        super.onTerminate()
    }
}