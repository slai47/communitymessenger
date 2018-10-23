package com.slai.communitymessenger.handlers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SMSHandler (val context : Context){

    private val PERMISSION_SEND_SMS = 123


    fun sendSMS(phoneNumber: String, message: String) {
        val manager = SmsManager.getDefault()

        manager.sendTextMessage(phoneNumber, null, message, null, null)
    }


    // Permission Code

    fun isSMSPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    fun requestReadAndSendSmsPermission( activity : Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.SEND_SMS),
            PERMISSION_SEND_SMS)
    }

    fun supported() : Boolean {
        val manager = context.packageManager
        var supported = false
        if(manager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY))
            supported = true
        return supported
    }

}

fun instance(context: Context): SMSHandler {
    return SMSHandler(context)
}