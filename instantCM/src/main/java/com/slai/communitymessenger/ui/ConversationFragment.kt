package com.slai.communitymessenger.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.ui.adapters.ConversationAdapter
import kotlinx.android.synthetic.main.frag_conversation.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ConversationFragment : Fragment(){

    companion object {
        @JvmField val ARG_ID = "id"
    }

    private var threadId : String = ""

    private var stored : List<Message>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_conversation, container, false)
    }

    override fun onStart() {
        super.onStart()
        threadId = arguments!!.getString(ARG_ID)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        // find SMS per this thread
        if(stored == null) {
            conversation_progress.visibility = View.VISIBLE
            val thread = Thread {
                val list: List<Message> = SMSHandler(activity!!.applicationContext).getIndividualThread(threadId)
                EventBus.getDefault().post(list)
            }
            thread.start()
        } else {
            setupView(stored!!)
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(list : List<Message>){
        if(list.isNotEmpty() && list[0].id == threadId){
            setupView(list)
        }
    }

    fun setupView(list: List<Message>){
        stored = list

        val adapter : ConversationAdapter = ConversationAdapter(activity as Activity, list)

        val manager = LinearLayoutManager(activity!!.applicationContext, RecyclerView.VERTICAL, true)

        conversation_list.layoutManager = manager
        conversation_list.adapter = adapter

        conversation_progress.visibility = View.GONE
    }
}