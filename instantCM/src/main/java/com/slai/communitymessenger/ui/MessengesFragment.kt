package com.slai.communitymessenger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.model.events.SMSReceivedEvent
import com.slai.communitymessenger.ui.adapters.MessagesAdapter
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.frag_messages.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MessengesFragment : Fragment() {

    var storedList : HashMap<String, Message>? = null

    var adapter : MessagesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_messages, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        if (storedList == null) {
            messages_progress.visibility = View.VISIBLE
            updateSMSList()
        } else {
            messages_progress.visibility = View.GONE
            loadMessages(storedList!!)
        }

        messages_fab.setOnClickListener {
            val transaction = fragmentManager!!.beginTransaction()
            transaction.add(R.id.my_nav_host_fragment, NewMessageFragment())
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            transaction.commit()
        }
    }

    private fun updateSMSList() {
        val thread = Thread {
            val list: HashMap<String, Message> = SMSHandler(activity!!.applicationContext).getLastestSMSList()
            EventBus.getDefault().post(list)
        }
        thread.start()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSMSReceived(event : SMSReceivedEvent){
        updateSMSList() // TODO this probably could be optimized. I'm not sure how yet but I feel this could be made better
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTextLoaded(event : HashMap<String, Message>) {
        loadMessages(event)
    }

    private fun loadMessages(messages : HashMap<String, Message>) {
        storedList = messages
        if(adapter == null) {

            adapter = MessagesAdapter(activity!!.applicationContext, ArrayList(messages.values))

            val manager = LinearLayoutManager(main_list.context, RecyclerView.VERTICAL, false)

            main_list.layoutManager = manager
            main_list.adapter = adapter

            messages_progress.visibility = View.GONE
        } else {

            val array = ArrayList(messages.values)
            array.sortBy { message -> message.time }
            adapter?.updateList(array)
        }
    }
}