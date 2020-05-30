package com.example.quarantinetravel.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quarantinetravel.R
import com.example.quarantinetravel.util.SfxManager


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        this.title = getString(R.string.about_title)
    }

    fun close(view: View) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        this.finish()
    }
}
