package com.example.quarantinetravel.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
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

    fun signInSilently() {
        println("sing in silent?");
        val signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            signedInAccount = account!!
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(activity, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    activity
                ) { task ->
                    if (task.isSuccessful) {
                        // The signed in account is stored in the task's result.
                        signedInAccount = task.result!!
                    } else {
                        println("hmm not possible");
                        // Player will need to sign-in explicitly using via UI.
                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                        // Interactive Sign-in.
                    }
                }
        }
    }

    fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        val intent = signInClient.signInIntent
        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun showLeaderboard() {
        Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
            .getLeaderboardIntent(activity.getString(R.string.leaderboard_id))
            .addOnSuccessListener { intent -> activity.startActivityForResult(intent, RC_LEADERBOARD_UI) }
    }

    fun addLeaderboardScore (score: Long) {
        Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
            .submitScore(activity.getString(R.string.leaderboard_id), score);
    }

    private fun signOut() {
        val signInClient = GoogleSignIn.getClient(
            activity,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        signInClient.signOut().addOnCompleteListener(
            activity
        ) {
            // at this point, the user is signed out.
        }
    }

    fun onActivityResult(data: Intent?) {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

        if (result!!.isSuccess) {
            // The signed in account is stored in the result.
            signedInAccount = result.signInAccount!!
        } else {
            var message = result.status.statusMessage
            if (message == null || message.isEmpty()) {
                message = activity.getString(R.string.error);
            }
            println(result.status)
            AlertDialog.Builder(activity).setMessage(message)
                .setNeutralButton(R.string.ok, null).show()
        }
    }

}