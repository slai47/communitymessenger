package com.slai.communitymessenger.model

data class Message(var body : String, var sender: String, var readState : Boolean,
              var time : Long, var folder : String, var id : String, var person : String?){

    override fun toString(): String {
        return "Message(body='$body', sender='$sender', readState=$readState, time='$time', folder='$folder', id='$id', person=$person)"
    }
}