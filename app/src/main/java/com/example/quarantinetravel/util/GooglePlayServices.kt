package com.example.quarantinetravel.util

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.example.quarantinetravel.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.Games

class GooglePlayServices(private val activity: Activity) {
    var signedInAccount : GoogleSignInAccount? = null
    val RC_SIGN_IN = 9001
    val RC_LEADERBOARD_UI = 9004
    private var leaderboards = false

    fun signInSilently() {
        val signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            signedInAccount = account!!
            writeSettings(true)
        } else {
            val signInClient = GoogleSignIn.getClient(activity, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    activity
                ) { task ->
                    if (task.isSuccessful) {
                        signedInAccount = task.result!!
                        writeSettings(true)
                    }
                }
        }
    }

    fun startSignInIntent(leaderboards: Boolean = false) {
        this.leaderboards = leaderboards
        val signInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        val intent = signInClient.signInIntent
        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun showLeaderboards() {
        val signedAccount = if (signedInAccount != null) signedInAccount else GoogleSignIn.getLastSignedInAccount(activity)
        if (signedAccount != null) {
            Games.getLeaderboardsClient(activity, signedAccount)
                .getLeaderboardIntent(activity.getString(R.string.leaderboard_id))
                .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_LEADERBOARD_UI) }
        } else {
            startSignInIntent()
        }
    }

    fun addLeaderboardScore (score: Long) {
        val signedAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (signedAccount != null) {
            Games.getLeaderboardsClient(activity, signedAccount)
                .submitScore(activity.getString(R.string.leaderboard_id), score)
        }
    }

    fun signOut() {
        val signInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        signInClient.signOut().addOnCompleteListener(
            activity
        ) {
            signedInAccount = null
            writeSettings(false)
        }
    }

    fun onActivityResult(data: Intent?) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

        if (result!!.isSuccess) {
            // The signed in account is stored in the result.
            signedInAccount = result.signInAccount!!
            writeSettings(true)
            if (leaderboards) {
                leaderboards = false
                showLeaderboards()
            }

        } else {
            signedInAccount = null
            leaderboards = false
            var message = result.status.statusMessage
            if (message == null || message.isEmpty()) {
                message = activity.getString(R.string.error_google_play_games)
            }
            AlertDialog.Builder(activity).setMessage(message)
                .setNeutralButton(R.string.ok, null).show()
        }
    }

    private fun writeSettings (value: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        prefs.edit().putBoolean(activity.getString(R.string.settings_google), value).apply()
    }
}
