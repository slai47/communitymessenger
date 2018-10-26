package com.slai.communitymessenger.model.events

import android.content.Intent

class SMSReceivedEvent(val body : String, val sender : String)

class OnRequestPermissionsResultEvent(val requestCode: Int, val permissions: Array<out String>, val grantResults: IntArray)

class OnActivityResultEvent(val requestCode: Int, val resultCode: Int, val data: Intent?)