package com.slai.communitymessenger.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.slai.communitymessenger.R
import com.slai.communitymessenger.handlers.SMSHandler
import com.slai.communitymessenger.utils.OpenBar
import kotlinx.android.synthetic.main.frag_permissions.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class PermissionsFragment : Fragment(){

    companion object {
        @JvmField val ARG_PERMISSION = "permission"
        @JvmField val EXTRA_SMS = "sms"
        @JvmField val EXTRA_CAMERA = "camera"
    }

    // might need to make this one that will accept many different permissions for future use

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater.inflate(R.layout.frag_permissions, container, false)
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        val permission = arguments?.getString("permission")
        val alertMessage = StringBuilder()
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

            frag_permission_message.text = alertMessage.toString()

            frag_permission_ok.setOnClickListener {
                when(permission){
                    EXTRA_SMS -> {
                        requestPermissions(
                            arrayOf(Manifest.permission.SEND_SMS),
                            SMSHandler.PERMISSION_SEND_SMS
                        )
                    }
                    EXTRA_CAMERA -> {
                        requestPermissions(
                            arrayOf(Manifest.permission.CAMERA),
                            147
                        )
                    }
                }
            }

            frag_permission_cancel.setOnClickListener {
                cancelled()
            }

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
                    Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_conversationFragment)

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
                OpenBar.on(permissionsFragment).with("This is required. So... By?").durationInfinite().show()
            }
            "camera" -> {
                Navigation.findNavController(permissionsFragment).navigate(R.id.action_permissionsFragment_to_conversationFragment)
            }
        }
    }
}