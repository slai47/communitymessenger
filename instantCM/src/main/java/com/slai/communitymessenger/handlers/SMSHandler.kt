package com.slai.communitymessenger.handlers

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.receivers.DeliveryReceiver
import com.slai.communitymessenger.receivers.SentReceiver
import java.util.*
import kotlin.collections.ArrayList


class SMSHandler (val context : Context){
    companion object {
        @JvmField val PERMISSION_SEND_SMS = 47
    }

    fun sendSMS(phoneNumber: String, message: String) {
        val manager = SmsManager.getDefault()
        val split = manager.divideMessage(message)
        val sentIntent : Intent = Intent(context, SentReceiver::class.java)
        sentIntent.putExtra("phoneNumber", phoneNumber)
        val pendingSent = PendingIntent.getBroadcast(context, 0, sentIntent, 0)

        val deliveryReceiver = Intent(context, DeliveryReceiver::class.java)
        deliveryReceiver.putExtra("phoneNumber", phoneNumber)
        val pendingDelivery = PendingIntent.getBroadcast(context, 0, sentIntent, 0)

        if(split.size == 1)
            manager.sendTextMessage(phoneNumber, null, message, pendingSent, pendingDelivery)
        else {
            val sentList = ArrayList<PendingIntent>()
            val deliveryList = ArrayList<PendingIntent>()
            for(i in 0 until split.size){
                sentList.add(pendingSent)
                deliveryList.add(pendingDelivery)
            }
            manager.sendMultipartTextMessage(phoneNumber, null, split, sentList, deliveryList)
        }
    }


    fun getLastestSMSList() : HashMap<String, Message>{
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

        c.use { c ->
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

        val objSms = Message(body, address, state == "1", date, folder, id, person)
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

        val selection = if(threadId != null) "${Telephony.Sms.THREAD_ID}=$threadId" else null

        val c = cr.query(
            uri, columns, selection, null, null)
        val totalSMS = c.count

        c.use { cursor ->
            if (cursor.moveToFirst()) {
                for (i in 0 until totalSMS) {
                    val pair = parse(c)
                    val id = pair.first
                    val objSms = pair.second
                    list.add(objSms)
                    cursor.moveToNext()
                }
            }
        }
        return list
    }

    fun getLatestSMSMessage(threadId : String) : Message?{
        var message : Message? = null

        val cr = context.contentResolver
        val columns = arrayOf(Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.READ, Telephony.Sms.PERSON)

        val c = cr.query(
            Telephony.Sms.Inbox.CONTENT_URI, columns, "${Telephony.Sms.THREAD_ID}=$threadId", null, null)

        c.use { cursor ->
            if (cursor.moveToLast()) {
                val pair = parse(c)
                val objSms = pair.second
                message = objSms
                cursor.moveToNext()
            }
        }
        return message
    }


    fun markMessageRead(number: String, body: String) {
        val cursor = context.contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, null, "${Telephony.Sms.ADDRESS}=$number", null, null)
        cursor.use { c ->
            while (c!!.moveToNext()) {
                if (c.getString(c.getColumnIndex(Telephony.Sms.ADDRESS)) == number
                    && c.getInt(c.getColumnIndex(Telephony.Sms.READ)) == 0
                    && c.getString(c.getColumnIndex(Telephony.Sms.BODY)).startsWith(body)
                ) {
                    val SmsMessageId = c.getString(c.getColumnIndex(Telephony.Sms._ID))
                    val values = ContentValues()
                    values.put(Telephony.Sms.READ, true)
                    context.contentResolver.update(
                        Telephony.Sms.Inbox.CONTENT_URI,
                        values,
                        "${Telephony.Sms._ID}=$SmsMessageId",
                        null
                    )
                    return
                }
            }
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