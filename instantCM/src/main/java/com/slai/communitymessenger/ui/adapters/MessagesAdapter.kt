package com.slai.communitymessenger.ui.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.slai.communitymessenger.R
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.list_message.view.*



class MessagesAdapter(val context : Context, val list : List<Message>) : RecyclerView.Adapter<MessagesViewHolder>(){

    val TAG : String = "MessagesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(context).inflate(R.layout.list_message, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val item : Message = list.get(position)
        holder.primary.text = item.sender
        holder.secondary.text = item.body

        holder.date.text = item.time

        if(!item.readState){
            holder.unread()
        } else {
            holder.read()
        }

        holder.itemView.tag = item
        holder.itemView.setOnClickListener { v ->
            val item = v.tag as Message
            Log.d(TAG, item.toString())
            // mark as read

            // use thread id or send number for sending to individual messages
        }
    }
}

class MessagesViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val primary : TextView = view.list_messaage_title
    val secondary : TextView = view.list_message_body
    val date : TextView = view.list_message_date
    val image : ImageView = view.list_message_image

    fun unread(){
        val boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD)

        primary.typeface = boldTypeface
        secondary.typeface = boldTypeface
        date.typeface = boldTypeface
    }

    fun read(){
        val regularTypeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        primary.typeface = regularTypeface
        secondary.typeface = regularTypeface
        date.typeface = regularTypeface
    }
}