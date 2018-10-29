package com.slai.communitymessenger.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.slai.communitymessenger.R

class IndividualMessageFragment : Fragment(){

    companion object {
        @JvmField val ARG_ID = "id"
    }

    private var threadId : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_single_messages, container, false)
    }

    override fun onStart() {
        super.onStart()
        threadId = arguments!!.getInt(ARG_ID)
    }

    override fun onResume() {
        super.onResume()
//        EventBus.getDefault().register(this)

        // find SMS per this thread
    }

    override fun onPause() {
        super.onPause()
//        EventBus.getDefault().unregister(this)
    }
}