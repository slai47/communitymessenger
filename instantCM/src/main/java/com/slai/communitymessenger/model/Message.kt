package com.slai.communitymessenger.model

class Message(val body : String, val sender: String, val readState : Boolean,
              val time : String, val folder : String, val id : String, val person : String)