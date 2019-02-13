package me.liujin.douyinhooker

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment

class PrefFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String) {
        when (key) {
            getString(R.string.settings_region_list_pref) -> {
                findPreference(key).summary = preferences.getString(key, "")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        val key = getString(R.string.settings_region_list_pref)
        val currentRegion =  findPreference(key)
        currentRegion.summary = preferenceManager.sharedPreferences.getString(key, "")
    }

    override fun onStart() {
        super.onStart()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }
}