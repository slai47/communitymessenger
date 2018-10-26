package com.slai.communitymessenger.ui

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.slai.communitymessenger.R
import android.R.attr.versionName
import android.content.pm.PackageManager



class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_settings, container, false)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        initView()
    }

    private fun initView() {
        var version : String = ""
        var code : Int = 0
        try {
            val pInfo = context?.packageManager?.getPackageInfo(activity?.packageName, 0)
            version = pInfo?.versionName!!
            code = pInfo?.versionCode!!
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var appInfo = findPreference("appInfo")
        appInfo.summary = "$version ($code)"
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences : SharedPreferences?, key : String?) {
        when (key){
            "darkTheme" -> {
                var mode = AppCompatDelegate.MODE_NIGHT_NO
                var theme = preferenceManager.sharedPreferences.getBoolean("darkTheme", false)
                if(theme)
                    mode = AppCompatDelegate.MODE_NIGHT_YES
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

    }
}