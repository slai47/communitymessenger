package com.slai.communitymessenger.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp()
            = findNavController(R.id.my_nav_host_fragment).navigateUp()

    override fun onResume() {
        super.onResume()
        val manager = SMSHandler(this)
        if(!manager.isSMSPermissionGranted()){
            findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_messengesFragment_to_permissionsFragment)
        }
    }
}
