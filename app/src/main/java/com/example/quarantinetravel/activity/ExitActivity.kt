package com.example.quarantinetravel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.quarantinetravel.R
import com.example.quarantinetravel.util.SfxManager

class ExitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exit)

        this.title = getString(R.string.exit_title)
    }

    fun cancel(view: View) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        this.finish()
    }

    fun exit(view: View) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        SfxManager.release()
        this.finishAffinity()
    }
}
