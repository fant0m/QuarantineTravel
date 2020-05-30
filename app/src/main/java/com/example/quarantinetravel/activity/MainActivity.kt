package com.example.quarantinetravel.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.example.quarantinetravel.R
import com.example.quarantinetravel.util.GooglePlayServices
import com.example.quarantinetravel.util.MusicManager
import com.example.quarantinetravel.util.Permissions
import com.example.quarantinetravel.util.SfxManager


class MainActivity : AppCompatActivity() {
    private var googlePlayServices : GooglePlayServices = GooglePlayServices(this)
    private lateinit var prefs: SharedPreferences
    //private var mp: MediaPlayer? = null
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )
    private val REQUIRED_PERMISSIONS_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyRequiredPermissions(this)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean(getString(R.string.settings_google), false)) {
            googlePlayServices.startSignInIntent()
        }

        SfxManager.init(
            this,
            prefs.getBoolean(getString(R.string.settings_sfx), true)
        )
        MusicManager.init(
            this,
            prefs.getBoolean(getString(R.string.settings_music), true)
        )
        MusicManager.play(R.raw.musicintro, true)

        initTitleAnimation()

        /*mp = MediaPlayer.create(this, R.raw.musicintro)
        mp?.isLooping = true
        mp?.start()*/
    }

    override fun onStop() {
        super.onStop()
        MusicManager.release()
        /*mp?.stop()
        mp?.release()
        mp = null*/
    }

    override fun onResume() {
        super.onResume()

        MusicManager.restart(R.raw.musicintro, true)
        /*if (mp === null) {
            mp = MediaPlayer.create(this, R.raw.musicintro)
            mp?.isLooping = true
            mp?.start()
        }*/

        if (prefs.getBoolean(getString(R.string.settings_firstRun), true)) {
            googlePlayServices.startSignInIntent()
            prefs.edit().putBoolean(getString(R.string.settings_firstRun), false).apply()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googlePlayServices.RC_SIGN_IN) {
            googlePlayServices.onActivityResult(data)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, ExitActivity::class.java)
        startActivity(intent)
    }

    private fun initTitleAnimation () {
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

    private fun buttonClick () {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
    }

    fun play(view: View) {
        buttonClick()
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun showLeaderboards(view: View) {
        buttonClick()
        if (prefs.getBoolean(getString(R.string.settings_google), false)) {
            googlePlayServices.showLeaderboards()
        } else {
            googlePlayServices.startSignInIntent(true)
        }
    }

    fun showSettings(view: View) {
        buttonClick()
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun showInfo(view: View) {
        buttonClick()
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }
}
