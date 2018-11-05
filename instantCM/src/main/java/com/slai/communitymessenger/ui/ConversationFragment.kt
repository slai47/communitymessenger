package com.slai.communitymessenger.ui

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.model.events.SMSDeliverEvent
import com.slai.communitymessenger.model.events.SMSSentEvent
import com.slai.communitymessenger.ui.adapters.ConversationAdapter
import kotlinx.android.synthetic.main.frag_conversation.*
import kotlinx.android.synthetic.main.snippet_conversation_bottom.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class ConversationFragment : Fragment(){

    private val TAG: String =
        "ConversationFragment"

    companion object {
        @JvmField val ARG_ID = "id"
        @JvmField val ARG_TITLE = "title"
        @JvmField val ARG_TYPE = "type"
        @JvmField val ARG_NUMBER = "number"

        @JvmField val TYPE_FULL = "full"
        @JvmField val TYPE_SHORT = "short"
    }

    private var type : String? = ""
    private var threadId : String? = ""
    private var title : String? = ""
    var phoneNumber : String = ""

    private var stored : ArrayList<Message>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_conversation, container, false)
    }

    override fun onStart() {
        super.onStart()
        threadId = arguments!!.getString(ARG_ID)
        title = arguments!!.getString(ARG_TITLE)
        type = arguments!!.getString(ARG_TYPE)
        phoneNumber = arguments!!.getString(ARG_NUMBER)
        if(type == TYPE_SHORT) {
            conversation.layoutParams.height = 300 // should be dynamic in the future
            conversation.gravity = Gravity.BOTTOM
            conversation_top_area.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        conversation_title.text = title

        // find SMS per this thread
        if(stored == null) {
            conversation_progress.visibility = View.VISIBLE
            val thread = Thread {
                val list: ArrayList<Message> = SMSHandler(activity!!.applicationContext).getIndividualThread(threadId) as ArrayList<Message>
                EventBus.getDefault().post(list)
            }
            thread.start()
        } else {
            conversation_progress.visibility = View.GONE
            setupView(stored!!)
        }

        setupBottomArea()

        setupScrolling()
    }

    private fun setupScrolling() {
        conversation_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    // Scrolling up
                    conversation_top_area.visibility = View.GONE
                    conversation_bottom_area.visibility = View.GONE

                } else {
                    // Scrolling down
                    conversation_top_area.visibility = View.VISIBLE
                    conversation_bottom_area.visibility = View.VISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

        })

    }

    private fun setupBottomArea() {
        conversation_send.setOnClickListener {
            if(!TextUtils.isEmpty(getText())){
                // Send Text
                SMSHandler(it.context).sendSMS(phoneNumber, getText())
                // update list
                val adapter : ConversationAdapter = conversation_list.adapter as ConversationAdapter
                adapter.list.add(Message(getText(), "", true, Calendar.getInstance().timeInMillis, "sent", "", ""))
                adapter.notifyItemInserted(adapter.list.size)
                // clear text
                conversation_text.setText("")
            }
        }

        conversation_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                conversation_send.setImageResource(
                    if(!TextUtils.isEmpty(getText())) R.drawable.ic_send
                    else R.drawable.ic_photo)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        if(type == TYPE_SHORT){
            conversation_send.setImageResource(R.drawable.ic_expand)
        } else {
            conversation_send.setImageResource(
                if(!TextUtils.isEmpty(getText())) R.drawable.ic_send
                else R.drawable.ic_photo)
        }

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(list : ArrayList<Message>){
        if(list.isNotEmpty() && list[0].id == threadId){
            setupView(list)
        }
    }

    fun setupView(list: ArrayList<Message>){
        stored = list

        val adapter = ConversationAdapter(activity as Activity, list)

        val manager = LinearLayoutManager(activity!!.applicationContext, RecyclerView.VERTICAL, true)

        conversation_list.layoutManager = manager
        conversation_list.adapter = adapter

        conversation_progress.visibility = View.GONE
    }

    fun getText() : String {
        return conversation_text.text.trim() as String
    }

    @Subscribe
    fun onSentEvent(event : SMSSentEvent){
        Log.d(TAG, "onSentEvent")
    }

    @Subscribe
    fun onDeliverEvent(event : SMSDeliverEvent){
        Log.d(TAG, "onDeliverEvent")

    }
}