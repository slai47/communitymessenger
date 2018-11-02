package com.slai.communitymessenger.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.slai.communitymessenger.model.events.SMSDeliverEvent
import org.greenrobot.eventbus.EventBus

class DeliveryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("DeliveryReciever", intent?.action + " " + intent?.extras.toString())
        EventBus.getDefault().post(SMSDeliverEvent(""))
    }
}