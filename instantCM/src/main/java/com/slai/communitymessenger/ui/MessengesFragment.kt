package com.slai.communitymessenger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

    var storedList : List<Message>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_messages, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        if (storedList == null) {
            messages_progress.visibility = View.VISIBLE
            val thread = Thread {
                val list: List<Message> = SMSHandler(activity!!.applicationContext).getSMSList()
                EventBus.getDefault().post(list)
            }
            thread.start()
        } else {
            messages_progress.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSMSReceived(event : SMSReceivedEvent){
        val listener : View.OnClickListener = View.OnClickListener{ v ->
            val bundle = Bundle()
            bundle.putString(IndividualMessageFragment.ARG_ID, event.sender)
            Navigation.findNavController(main_list).navigate(R.id.action_messengesFragment_to_individualMessageFragment, bundle)
        }
        OpenBar.on(main_list).with(event.sender + "\n" + event.body).durationLong().withAction("Reply", listener).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTextLoaded(event : List<Message>) {
        loadMessages(event)
    }

    private fun loadMessages(messages : List<Message>) {
        storedList = messages

        val adapter = MessagesAdapter(activity!!.applicationContext, messages)

        val manager = LinearLayoutManager(main_list.context, RecyclerView.VERTICAL, false)

        main_list.layoutManager = manager
        main_list.adapter = adapter

        messages_progress.visibility = View.GONE
    }
}