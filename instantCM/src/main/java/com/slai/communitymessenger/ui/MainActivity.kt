package com.slai.communitymessenger.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NavUtils
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_messages.*

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
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp()
            = findNavController(R.id.my_nav_host_fragment).navigateUp()
}
