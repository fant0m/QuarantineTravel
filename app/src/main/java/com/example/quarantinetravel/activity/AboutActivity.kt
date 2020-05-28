package com.example.quarantinetravel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.quarantinetravel.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        this.title = getString(R.string.about_title);
    }

    fun close(view: View) {
        this.finish()
    }
}
