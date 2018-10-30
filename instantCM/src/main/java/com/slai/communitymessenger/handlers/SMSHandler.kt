package com.slai.communitymessenger.handlers

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.slai.communitymessenger.model.Message
import java.util.*


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

    fun getLastestSMSList() : Map<String, Message>{
       val list = HashMap<String, Message>()
        val inbox = getSMSList(Telephony.Sms.Inbox.CONTENT_URI)
        val sent = getSMSList(Telephony.Sms.Sent.CONTENT_URI)

        for((key, item) in inbox){
            var sentObj : Message? = sent[key]

            var selectSent = false
            if(sentObj != null) {
                if(sentObj.time > item.time)
                    selectSent = true
            }

            if(selectSent)
                list[key] = sentObj!!
            else
                list[key] = item
        }

        return list
    }

    fun getSMSList(uri: Uri): Map<String, Message>{
        val list = HashMap<String, Message>()
        // Get texts here
        // content://sms/sent
        // contetn://sms/draft
        val cr = context.contentResolver
        val columns = arrayOf(Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.READ, Telephony.Sms.PERSON)
        val c = cr.query(
            uri, columns, null, null, null)
        val totalSMS = c.count

        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {
                val pair = parse(c)
                val id = pair.first
                val objSms = pair.second
                if(!list.containsKey(id))
                    list[id] = objSms
                c.moveToNext()
            }
        }
        return list
    }

    private fun parse(c: Cursor): Pair<String, Message> {
        val id = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))
        val address = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
        val body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY))
        val state = c.getString(c.getColumnIndex(Telephony.Sms.READ))
        val date = c.getLong(c.getColumnIndexOrThrow(Telephony.Sms.DATE))
        val folder: String = if (c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)).contains("1")) {
            "inbox"
        } else {
            "sent"
        }
        val person = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.PERSON))

        var objSms = Message(body, address, state == "1", date, folder, id, person)
        return Pair(id, objSms)
    }

    fun getIndividualThread(threadId: String?): List<Message>{
        val list : ArrayList<Message> = ArrayList()
        val sent = getIndividuaalSMSList(Telephony.Sms.Sent.CONTENT_URI, threadId)
        val inbox = getIndividuaalSMSList(Telephony.Sms.Inbox.CONTENT_URI, threadId)
        // add mms later

        list.addAll(sent)
        list.addAll(inbox)

        // sort
        list.sortBy { it.time }

        return list
    }

    fun getIndividuaalSMSList(uri: Uri, threadId : String?): List<Message> {
        val list = ArrayList<Message>()
        // Get texts here
        // content://sms/sent
        // contetn://sms/draft
        val cr = context.contentResolver
        val columns = arrayOf(Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.READ, Telephony.Sms.PERSON)

        val selection = if(threadId != null) "thread_id={$threadId}" else null

        val c = cr.query(
            uri, columns, selection, null, null)
        val totalSMS = c.count

        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {
                val pair = parse(c)
                val id = pair.first
                val objSms = pair.second
                list.add(objSms)
                c.moveToNext()
            }
        }

        return list
    }


    fun markMessageRead(number: String, body: String) {
        val cursor = context.contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, null)
        try {

            while (cursor!!.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("address")) == number
                    && cursor.getInt(cursor.getColumnIndex("read")) == 0
                    && cursor.getString(cursor.getColumnIndex("body")).startsWith(body)
                ) {
                    val SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"))
                    val values = ContentValues()
                    values.put("read", true)
                    context.contentResolver.update(
                        Uri.parse("content://sms/inbox"),
                        values,
                        "_id=$SmsMessageId",
                        null
                    )
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("Mark Read", "Error in Read: " + e.toString())
        }

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