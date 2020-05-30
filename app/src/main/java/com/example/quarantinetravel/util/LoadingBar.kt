package com.example.quarantinetravel.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout


class LoadingBar(context: Context) {
    private val progressBar: ProgressBar
    private val rl: RelativeLayout
    fun show() {
        rl.setBackgroundColor(Color.parseColor("#80000000"))
        progressBar.visibility = View.VISIBLE
    }

    fun hide() {
        rl.setBackgroundColor(Color.TRANSPARENT)
        progressBar.visibility = View.INVISIBLE
    }

    init {
        val layout = (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        progressBar.isIndeterminate = true

        rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(progressBar)

        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        layout.addView(rl, params)

        hide()
    }
}
