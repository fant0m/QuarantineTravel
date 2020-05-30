package com.example.quarantinetravel.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.quarantinetravel.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
