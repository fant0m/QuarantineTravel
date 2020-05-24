package com.example.quarantinetravel

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val title1: ImageView = findViewById(R.id.title1)
        val title2: ImageView = findViewById(R.id.title2)

        val slideLeftAnimation = TranslateAnimation(
            -1000.0f, 0.0f,
            0.0f, 0.0f
        )
        slideLeftAnimation.duration = 1000
        slideLeftAnimation.fillBefore = true

        val slideRigtAnimation = TranslateAnimation(
            1000.0f, 0.0f,
            0.0f, 0.0f
        )
        slideRigtAnimation.duration = 1000
        slideRigtAnimation.fillBefore = true


        title1.startAnimation(slideLeftAnimation)
        title2.startAnimation(slideRigtAnimation)

        val button2: ImageButton = findViewById(R.id.button2);
        button2.setOnClickListener {
            buttonClick();
        }
        val button3: ImageButton = findViewById(R.id.button3);
        button3.setOnClickListener {
            buttonClick();
        }
        val button4: ImageButton = findViewById(R.id.button4);
        button4.setOnClickListener {
            buttonClick();
        }
    }

    fun buttonClick () {
        //val mp: MediaPlayer = MediaPlayer.create(this, R.raw.click2)
        //mp.start();
    }

    fun play(view: View) {
        buttonClick();

        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }


}
