package com.example.quarantinetravel.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.quarantinetravel.R
import com.example.quarantinetravel.util.SfxManager
import kotlinx.android.synthetic.main.activity_game_result.*

class GameResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        val bundle = intent.extras
        val score = bundle!!.get("score")
        if (score != null) {
            resultValue.text = score.toString()
        }

        this.title = getString(R.string.congratulations)
    }

    fun close(view: View) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
