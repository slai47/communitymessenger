package com.slai.communitymessenger.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.slai.communitymessenger.R
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.list_message.view.*

class MessagesAdapter(val context : Context, val list : List<Message>) : RecyclerView.Adapter<MessagesViewHolder>(){

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
        holder.itemView.setOnClickListener { v ->
            OpenBar.on(v).with("hello").duration(Snackbar.LENGTH_SHORT).show()
        }
    }
}

class MessagesViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val primary : TextView = view.list_messaage_title
    val secondary : TextView = view.list_message_body
}