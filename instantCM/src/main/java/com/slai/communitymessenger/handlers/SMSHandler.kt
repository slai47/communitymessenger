package com.slai.communitymessenger.handlers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.slai.communitymessenger.model.Message

class SMSHandler (val context : Context){
    companion object {
        @JvmField val PERMISSION_SEND_SMS = 47
    }

    fun sendSMS(phoneNumber: String, message: String) {
        val manager = SmsManager.getDefault()
        manager.sendTextMessage(phoneNumber, null, message, null, null)
    }
    fun sendMMS(phoneNumber: String, message: String) {
        val manager = SmsManager.getDefault()
        val parts = manager.divideMessage(message)
        manager.sendMultipartTextMessage(phoneNumber, null, parts, null , null)
    }

    fun getSMSList() : List<Message>{
        val list = ArrayList<Message>()
        // Get texts here
        var objSms : Message
        // content://sms/sent
        // contetn://sms/draft
        val cr = context.contentResolver
        val columns = arrayOf(Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.READ, Telephony.Sms.PERSON)
        val c = cr.query(
            Telephony.Sms.Inbox.CONTENT_URI, columns, null, null, null)
        val totalSMS = c.count


        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {

                val id = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
                val address = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val state = c.getString(c.getColumnIndex(Telephony.Sms.READ))
                val date = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE))
                var folder : String
                if (c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)).contains("1")) {
                    folder = "inbox"
                } else {
                    folder = "sent"
                }
                val person = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.PERSON))

                objSms = Message(body, address, state == "1", date, folder, id, person)
                list.add(objSms)
                c.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close()

        return list
    }


    // Permission Code

    fun isSMSPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    }

    fun requestReadAndSendSmsPermission( activity : Activity?) {
        ActivityCompat.requestPermissions(activity!!,
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

    fun isDefaultApp() : Boolean{
        return !Telephony.Sms.getDefaultSmsPackage(context).equals(context.packageName)
    }

}