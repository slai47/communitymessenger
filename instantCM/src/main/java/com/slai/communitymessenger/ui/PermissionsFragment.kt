package com.slai.communitymessenger.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.frag_permissions.*
import org.greenrobot.eventbus.EventBus

class PermissionsFragment: Fragment(){

    // might need to make this one that will accept many different permissions for future use

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_permissions, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        initView()
    }

    private fun initView() {
        val manager = SMSHandler(permissionsFragment.context)
        if(!manager.isSMSPermissionGranted()){
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Permission Request")
            builder.setMessage("Community Messenger needs Permissions to see your SMS in order to work. Upon clicking \"Ok\", a new dialog will appear")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok"){ dialogInterface: DialogInterface, i: Int ->
                val manager = SMSHandler(permissionsFragment.context)
                manager.requestReadAndSendSmsPermission(activity)
            }

            builder.setNegativeButton("No"){ dialogInterface: DialogInterface, i: Int ->
                cancelled()
            }
            builder.show()
        } else {
            Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_messengesFragment)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMSHandler.PERMISSION_SEND_SMS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // SMS related task you need to do.
                    Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_messengesFragment)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    cancelled()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun cancelled() {
        OpenBar.on(permissionsFragment).with("This is required. So... By?").duration(OpenBar.INDEFINITELY).show()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


}