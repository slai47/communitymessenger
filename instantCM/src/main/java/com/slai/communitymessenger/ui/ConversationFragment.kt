package com.slai.communitymessenger.ui

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.Message
import com.slai.communitymessenger.ui.adapters.ConversationAdapter
import kotlinx.android.synthetic.main.frag_conversation.*
import kotlinx.android.synthetic.main.snippet_conversation_bottom.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ConversationFragment : Fragment(){

    companion object {
        @JvmField val ARG_ID = "id"
        @JvmField val ARG_TITLE = "title"
    }

    private var threadId : String = ""
    private var title : String = ""

    private var stored : List<Message>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_conversation, container, false)
    }

    override fun onStart() {
        super.onStart()
        threadId = arguments!!.getString(ARG_ID)
        title = arguments!!.getString(ARG_TITLE)
        val act = activity as AppCompatActivity
        act.setSupportActionBar(toolbar)
        act.actionBar.setDisplayHomeAsUpEnabled(true)
        act.actionBar.title = title
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

                // update list

                // clear text
                conversation_text.setText("")
            }
        }

        conversation_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                conversation_send.setImageResource(
                    if(!TextUtils.isEmpty(getText())) R.color.colorPrimary
                    else R.color.colorPrimaryDark)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

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

        val adapter = ConversationAdapter(activity as Activity, list)

        val manager = LinearLayoutManager(activity!!.applicationContext, RecyclerView.VERTICAL, true)

        conversation_list.layoutManager = manager
        conversation_list.adapter = adapter

        conversation_progress.visibility = View.GONE
    }

    fun getText() : String {
        return conversation_text.text.trim() as String
    }
}