package com.slai.communitymessenger.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.slai.communitymessenger.R
import kotlinx.android.synthetic.main.frag_new_messeges.*
import org.greenrobot.eventbus.EventBus

class NewMessageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_new_messeges, container, false)
    }

    override fun onResume() {
        super.onResume()

        new_message_expand.setOnClickListener { v: View? ->
            val bundle = Bundle()
            bundle.putString(ConversationFragment.ARG_TYPE, ConversationFragment.TYPE_FULL)
            bundle.putString(ConversationFragment.ARG_NUMBER, new_message_text.text.toString())
            findNavController().navigate(R.id.action_messengesFragment_to_conversationFragment, bundle)
        }

        new_message_back.setOnClickListener { v: View? ->
            activity?.onBackPressed()
        }

        setupSearchMethod()
    }

    private fun setupSearchMethod() {
        // get contact list and keep it local in adapter.
        // Use the searchContacts in the ContactHandler
        // Compare and limit the list
        // When one is clicked, grab the phone number as well by using getContact with the id we already have
        // Thread it to move quickly with the cursor
        // Long click should open the profile for that person.

        new_message_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    fun getText() : String {
        return new_message_text.text.toString()
    }

    override fun onPause() {
        super.onPause()
    }

}