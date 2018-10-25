package com.slai.communitymessenger.handlers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
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

    fun getSMSList() : List<Message>{
        val list = ArrayList<Message>()
        // Get texts here
        var objSms : Message
        val message = Uri.parse("content://sms/")
        val cr = mActivity.getContentResolver()

        val c = cr.query(message, null, null, null, null)
        context.startManagingCursor(c)
        val totalSMS = c.getCount()

        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {

                objSms = Message()
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")))
                objSms.setAddress(
                    c.getString(
                        c
                            .getColumnIndexOrThrow("address")
                    )
                )
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")))
                objSms.setReadState(c.getString(c.getColumnIndex("read")))
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")))
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox")
                } else {
                    objSms.setFolderName("sent")
                }

                list.add(objSms)
                c.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close()

        return lstSms

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

}