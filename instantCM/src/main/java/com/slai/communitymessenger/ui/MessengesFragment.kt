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
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MessengesFragment : Fragment() {

    var storedList : HashMap<String, Message>? = null

    var adapter : MessagesAdapter? = null

    var job = Job()
    val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_messages, container, false)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)

        if (storedList == null) {
            messages_progress.visibility = View.VISIBLE
            job = scope.async {  updateSMSList() }
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

    /**
     * Run in background
     */
    private suspend fun updateSMSList() {
        val list: HashMap<String, Message> = SMSHandler(activity!!.applicationContext).getLastestSMSList()
        withContext(Dispatchers.Main){
            loadMessages(list)
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSMSReceived(event : SMSReceivedEvent){
        job = scope.async { updateSMSList() } // TODO this probably could be optimized. I'm not sure how yet but I feel this could be made better
    }

    override fun onStop() {
        super.onStop()
        if(job.isActive)
            job.cancel()
    }

    private fun loadMessages(messages : HashMap<String, Message>) {
        storedList = messages
        if(main_list.adapter == null) {

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