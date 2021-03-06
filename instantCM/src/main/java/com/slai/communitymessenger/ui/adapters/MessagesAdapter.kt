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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.ui.ConversationFragment
import kotlinx.android.synthetic.main.list_message.view.*
import java.text.DateFormat
import java.util.*


class MessagesAdapter(val context : Context, var list : List<Message>) : RecyclerView.Adapter<MessagesViewHolder>(){

    val TAG : String = "MessagesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(context).inflate(R.layout.list_message, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val item : Message = list[position]
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
            bundle.putString(ConversationFragment.ARG_ID, item.id)
            bundle.putString(ConversationFragment.ARG_TITLE, item.sender)
            bundle.putString(ConversationFragment.ARG_NUMBER, item.sender)
            bundle.putString(ConversationFragment.ARG_TYPE, ConversationFragment.TYPE_FULL)
            Navigation.findNavController(v).navigate(R.id.action_messengesFragment_to_conversationFragment, bundle)

            list[tempHolder.position!!].readState = true
            notifyItemChanged(tempHolder.position!!)
        }
    }

    fun updateList(newList : List<Message>){
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(MessagesDiffCallback(list, newList), true)
        result.dispatchUpdatesTo(this)
        list = newList
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

class MessagesDiffCallback(val current : List<Message>, val new : List<Message>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return current[oldItemPosition].id == new[newItemPosition].id
    }

    override fun getOldListSize() = current.size

    override fun getNewListSize() = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return current[oldItemPosition] == new[newItemPosition]
    }

}