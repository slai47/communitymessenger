package com.slai.communitymessenger.ui

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.slai.communitymessenger.R


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
        var version: String = ""
        var code: Int = 0
        try {
            val pInfo = context?.packageManager?.getPackageInfo(activity?.packageName, 0)
            version = pInfo?.versionName!!
            code = pInfo?.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val appInfo = findPreference("appInfo")
        appInfo.summary = "$version ($code)"

        val darkTheme: Preference = findPreference("darkTheme")
        val summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> { preference ->
            if(preference.isChecked) {
                "Dark Theme"
            } else {
                "Light Theme"
            }
        }
        darkTheme.summaryProvider = summaryProvider
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(preferences : SharedPreferences?, key : String?) {
        when (key){
            "darkTheme" -> {
                var mode = AppCompatDelegate.MODE_NIGHT_NO
                val theme = preferenceManager.sharedPreferences.getBoolean("darkTheme", false)
                if(theme)
                    mode = AppCompatDelegate.MODE_NIGHT_YES
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

    }
}