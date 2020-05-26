package com.example.quarantinetravel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.quarantinetravel.utils.GooglePlayServices
import com.example.quarantinetravel.utils.Permissions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games


class MainActivity : AppCompatActivity() {
    private var googlePlayServices : GooglePlayServices = GooglePlayServices(this)
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )
    private val REQUIRED_PERMISSIONS_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyRequiredPermissions(this);
        //googlePlayServices = googlePlayServices(this);

        val title1: ImageView = findViewById(R.id.title1)
        val title2: ImageView = findViewById(R.id.title2)

        val slideLeftAnimation = TranslateAnimation(-1000.0f, 0.0f,0.0f, 0.0f)
        slideLeftAnimation.duration = 1000
        slideLeftAnimation.fillBefore = true

        val slideRightAnimation = TranslateAnimation(1000.0f, 0.0f,0.0f, 0.0f)
        slideRightAnimation.duration = 1000
        slideRightAnimation.fillBefore = true

        title1.startAnimation(slideLeftAnimation)
        title2.startAnimation(slideRightAnimation)

        val button2: ImageButton = findViewById(R.id.button2);
        button2.setOnClickListener {
            buttonClick();
            if (googlePlayServices.signedInAccount != null) {
                googlePlayServices.showLeaderboard();
            } else {
                googlePlayServices.startSignInIntent()
            }
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googlePlayServices.RC_SIGN_IN) {
            googlePlayServices.onActivityResult(data);
        }
    }


    override fun onResume() {
        super.onResume()
        googlePlayServices.signInSilently()
    }

    private fun verifyRequiredPermissions(activity: Activity) {
        val permission: Boolean = Permissions.hasPermissions(activity, *REQUIRED_PERMISSIONS)
        if (!permission) {
            ActivityCompat.requestPermissions(
                activity,
                REQUIRED_PERMISSIONS,
                REQUIRED_PERMISSIONS_CODE
            )
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
