package com.slai.communitymessenger.ui.adapters

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.ui.ConversationFragment
import com.slai.communitymessenger.ui.PermissionsFragment
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.list_message.view.*
import java.text.DateFormat
import java.util.*
import kotlin.math.acos


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

        val date = Date(item.time)
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)
        holder.date.text = dateFormat.format(date)

        if(!item.readState){
            holder.unread()
        } else {
            holder.read()
        }

        holder.position = position

        holder.itemView.tag = holder
        holder.itemView.setOnClickListener { v ->
            val tempHolder = v.tag as MessagesViewHolder
            val item = list[tempHolder.position!!]
            Log.d(TAG, item.toString())

            // mark as read
            SMSHandler(v.context).markMessageRead(item.sender, item.body)
            // use thread id or send number for sending to individual messages
            val bundle = Bundle()
            bundle.putString(ConversationFragment.ARG_ID, item!!.id)
            Navigation.findNavController(v).navigate(R.id.action_messengesFragment_to_conversationFragment, bundle)

            list[tempHolder.position!!].readState = true
            notifyItemChanged(tempHolder.position!!)
        }
    }
}

class MessagesViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val primary : TextView = view.list_messaage_title
    val secondary : TextView = view.list_message_body
    val date : TextView = view.list_message_date
    val image : ImageView = view.list_message_image
    var position : Int? = null

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