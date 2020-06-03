package com.example.quarantinetravel.activity

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.quarantinetravel.R
import com.example.quarantinetravel.fragment.SettingsFragment
import com.example.quarantinetravel.util.GooglePlayServices
import com.example.quarantinetravel.util.MusicManager
import com.example.quarantinetravel.util.SfxManager


class SettingsActivity : AppCompatActivity() {
    private var googlePlayServices : GooglePlayServices = GooglePlayServices(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.title = getString(R.string.settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        val prefListener = OnSharedPreferenceChangeListener { prefs, key ->
            println(key + " changed")
            if (key == getString(R.string.settings_sfx)) {
                println("before" + SfxManager.sfxEnabled)
                SfxManager.sfxEnabled = prefs.getBoolean(key, false)
                println("after" + SfxManager.sfxEnabled)
            } else if (key == getString(R.string.settings_music)) {
                val value = prefs.getBoolean(key, false)
                MusicManager.musicEnabled = value
                if (value) {
                    MusicManager.play(R.raw.musicintro, true)
                } else if (!value) {
                    MusicManager.release()
                }
            } else if (key == getString(R.string.settings_google)) {
                val value = prefs.getBoolean(key, false)
                if (value) {
                    googlePlayServices.startSignInIntent()
                } else {
                    googlePlayServices.signOut()
                }
            }
        }

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener (prefListener)
    }

    fun close(view: View) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        this.finish()
    }
}
