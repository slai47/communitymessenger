package com.slai.communitymessenger.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NavUtils
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.model.events.CollapseConverstionDelayEvent
import com.slai.communitymessenger.model.events.SMSReceivedEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_messages.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_AUTO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = SMSHandler(this)
        if(!manager.isSMSPermissionGranted()){
            val bundle = Bundle()
            bundle.putString(PermissionsFragment.ARG_PERMISSION, PermissionsFragment.EXTRA_SMS)
            findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_messengesFragment_to_permissionsFragment, bundle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {

                Navigation.findNavController(my_nav_host_fragment.view!!).navigate(R.id.action_messengesFragment_to_settingsFragment)
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onSupportNavigateUp()
            = findNavController(R.id.my_nav_host_fragment).navigateUp()

    @Subscribe
    fun onNewMessageRecieved(event : SMSReceivedEvent){
        val currentFrag = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment)
        var showPopup = true
//        if(currentFrag != null && currentFrag is ConversationFragment){
//            val messageFrag = currentFrag as ConversationFragment
//            val currentThread = messageFrag.phoneNumber
//            showPopup = event.sender == currentThread
//        } else if(currentFrag is MessengesFragment){
//            showPopup = true
//        }

        if(showPopup){
            val fragment = ConversationFragment()
            val bundle = Bundle()
            bundle.putString(ConversationFragment.ARG_TYPE, ConversationFragment.TYPE_SHORT)
            bundle.putString(ConversationFragment.ARG_ID, "")
            bundle.putString(ConversationFragment.ARG_NUMBER, event.sender)
            bundle.putString(ConversationFragment.ARG_TITLE, "")
            fragment.arguments = bundle

            val handler = Handler()
            handler.postDelayed({
                EventBus.getDefault().post(CollapseConverstionDelayEvent(event.sender))
            }, 5000)

            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_down, R.anim.slide_up, R.anim.slide_down, R.anim.slide_up).add(R.id.my_nav_host_fragment, fragment).addToBackStack(event.sender).commit()
        }
    }

}
