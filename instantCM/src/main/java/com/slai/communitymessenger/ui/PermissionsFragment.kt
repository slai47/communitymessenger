package com.slai.communitymessenger.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.frag_permissions.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder


class PermissionsFragment: Fragment(){

    companion object {
        @JvmField val ARG_PERMISSION = "permission"
        @JvmField val EXTRA_SMS = "sms"
        @JvmField val EXTRA_CAMERA = "camera"
    }

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
        val permission = arguments?.getString("permission")
        val alertMessage : StringBuilder = StringBuilder()
        when(permission){
            "sms" -> {
                alertMessage.append("Community Messenger needs Permissions to see your SMS in order to work.")
            }
            "camera" -> {
                alertMessage.append("Community Messenger needs permission to use your camera to take photo and video.")
            }
        }
        alertMessage.append(" Upon clicking \"Ok\", a new dialog will appear.")

        val manager = SMSHandler(permissionsFragment.context)
        if(!manager.isSMSPermissionGranted()){
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle("Permission Request")
            builder.setMessage(alertMessage.toString())
            builder.setCancelable(false)
            builder.setPositiveButton("Ok"){ dialogInterface: DialogInterface, i: Int ->
                val permission = arguments?.getString("permission")
                when(permission){
                    EXTRA_SMS -> {
                        SMSHandler(permissionsFragment.context).requestReadAndSendSmsPermission(activity)
                    }
                    EXTRA_CAMERA -> {
                        ActivityCompat.requestPermissions(activity!!,
                            arrayOf(Manifest.permission.CAMERA),
                            147
                        )
                    }
                }
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
                    if (SMSHandler(activity!!.applicationContext).isDefaultApp()) {
                        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity?.packageName)
                        startActivityForResult(intent, 14747)
                    } else {
                        Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_messengesFragment)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    cancelled()
                }
                return
            }
            147 -> { // camera permssion
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // SMS related task you need to do.
                    Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_individualMessageFragment)

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    cancelled()
                }
                return
            }
            14747 -> {
                Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_messengesFragment)
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            14747 -> {
                if(resultCode == RESULT_OK) {
                    Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_messengesFragment)
                }
            }
        }
    }

    private fun cancelled() {
        val permission = arguments?.getString("permission")
        when(permission){
            "sms" -> {
                OpenBar.on(permissionsFragment).with("This is required. So... By?").duration(OpenBar.INDEFINITELY).show()
            }
            "camera" -> {
                Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_individualMessageFragment)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


}