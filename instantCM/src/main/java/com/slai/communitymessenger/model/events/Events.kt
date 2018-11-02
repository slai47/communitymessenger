package com.slai.communitymessenger.model.events


data class SMSReceivedEvent(val body : String, val sender : String)

data class SMSSentEvent(val phonenumber : String)

data class SMSDeliverEvent(val phoneNumber : String)