package com.slai.communitymessenger.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.model.events.SMSDeliverEvent
import com.slai.communitymessenger.model.events.SMSReceivedEvent
import com.slai.communitymessenger.model.events.SMSSentEvent
import com.slai.communitymessenger.ui.adapters.ConversationAdapter
import kotlinx.android.synthetic.main.frag_conversation.*
import kotlinx.android.synthetic.main.snippet_conversation_bottom.*
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlinx.coroutines.*

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

    var job = Job()
    val scopeContext = CoroutineScope(Dispatchers.IO + job)

    private var stored : ArrayList<Message>? = null

    private var adapter: ConversationAdapter? = null

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
                grabAllMessages()
        } else {
            conversation_progress.visibility = View.GONE
            setupView(stored!!)
        }

        setupBottomArea()

        setupScrolling()
    }

    private fun grabAllMessages() {
         job = scopeContext.async {
            val list: ArrayList<Message> =
                SMSHandler(activity!!.applicationContext).getIndividualThread(threadId) as ArrayList<Message>
            EventBus.getDefault().post(list)
        }
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
            sendText()
        }

        conversation_text.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEND){
                sendText()
                true
            } else {
                false
            }
        }

        conversation_back.setOnClickListener {
            conversation_text.findNavController().navigateUp()
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

    fun sendText() {
        if (!TextUtils.isEmpty(getText())) {
            // Send Text
            SMSHandler(conversation_list.context).sendSMS(phoneNumber, getText())
            // update list
            val message = Message(getText(), "", true, Calendar.getInstance().timeInMillis, "sent", "", "")
            message.sending = true
            adapter?.list?.add(0, message)
            adapter?.notifyItemInserted(0)
            val handler = Handler()
            handler.postDelayed({
                onSentEvent(SMSSentEvent(phoneNumber))
            }, 3000)
            // clear text
            conversation_text.setText("")
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        super.onStop()
        if(job.isActive)
            job.cancel()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(list : ArrayList<Message>){
        if(list.isNotEmpty() && list[0].id == threadId){
            setupView(list)
        }
    }

    fun setupView(list: ArrayList<Message>){
        stored = list
        if(adapter == null) {

            adapter = ConversationAdapter(activity as Activity, list)
            val manager = LinearLayoutManager(activity!!.applicationContext, RecyclerView.VERTICAL, true)

            conversation_list.layoutManager = manager
            conversation_list.adapter = adapter

        } else {
            adapter?.updateList(list)

        }
        conversation_progress.visibility = View.GONE
    }

    fun getText() : String {
        return conversation_text.text.toString().trim()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentEvent(event : SMSSentEvent){
        Log.d(TAG, "onSentEvent")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeliverEvent(event : SMSDeliverEvent){
        Log.d(TAG, "onDeliverEvent")

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveEvent(event: SMSReceivedEvent){
        if(event.sender == phoneNumber){
            val message = Message(event.body, event.sender, true, Calendar.getInstance().timeInMillis, "sent", "$threadId", "")
            stored?.add(0, message)
            adapter?.notifyItemInserted(0)
        }
    }
}