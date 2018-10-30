package com.slai.communitymessenger.model

data class Message(val body : String, val sender: String, val readState : Boolean,
              val time : Long, val folder : String, val id : String, val person : String?){

    override fun toString(): String {
        return "Message(body='$body', sender='$sender', readState=$readState, time='$time', folder='$folder', id='$id', person=$person)"
    }
}