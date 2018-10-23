package com.slai.communitymessenger.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.model.Message
import kotlinx.android.synthetic.main.list_message.view.*

class MessagesAdapter(val context : Context, val list : ArrayList<Message>) : RecyclerView.Adapter<MessagesViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        return MessagesViewHolder(LayoutInflater.from(context).inflate(R.layout.list_message, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {


    }

}

class MessagesViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val primary : TextView = view.list_messaage_title
    val secondary : TextView = view.list_message_body
}