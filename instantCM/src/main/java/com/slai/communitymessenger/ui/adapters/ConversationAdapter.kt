package com.slai.communitymessenger.ui.adapters

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.model.Message
import kotlinx.android.synthetic.main.list_conversation.view.*
import java.text.DateFormat
import java.util.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import com.slai.communitymessenger.utils.OpenBar
import kotlin.collections.ArrayList


class ConversationAdapter(val context : Activity, val list : ArrayList<Message>) : RecyclerView.Adapter<ConversationViewHolder>() {

    var width : Int

    init {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels / 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(LayoutInflater.from(context).inflate(R.layout.list_conversation, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val message = list[position]
        holder.addSpacing(message.folder == "1", width)

        holder.text.text = message.body

        val date : Date = Date(message.time)
        val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US)
        holder.date.text = dateFormat.format(date)

        holder.itemView.tag = message
        holder.itemView.setOnClickListener {
            val message : Message = it.getTag() as Message
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText(context.getString(R.string.clipboard_copied), message.body)
            clipboard!!.primaryClip = clip
            OpenBar.on(it.rootView).with(context.getString(R.string.clipboard_notification)).durationShort().show()
        }
    }
}

class ConversationViewHolder(viewItem : View) : RecyclerView.ViewHolder(viewItem) {

    var text : TextView = viewItem.list_conversation_text
    var date : TextView = viewItem.list_conversation_date

    fun addSpacing(right : Boolean, width : Int) {
        val params : LinearLayout.LayoutParams = text.layoutParams as LinearLayout.LayoutParams
        if(right) {
            params.leftMargin = 0
            params.rightMargin = width
        } else {
            params.leftMargin = width
            params.rightMargin = 0
        }
        text.layoutParams = params
    }

}