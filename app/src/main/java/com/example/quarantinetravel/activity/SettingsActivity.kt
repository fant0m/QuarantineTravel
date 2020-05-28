package com.example.quarantinetravel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.quarantinetravel.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.title = getString(R.string.settings);
    }

    fun save(view: View) {
        // @todo save and load settings
        this.finish()
    }
}
