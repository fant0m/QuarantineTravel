package com.example.quarantinetravel.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout


class LoadingBar(context: Context) {
    private val mProgressBar: ProgressBar
    private val rl: RelativeLayout
    fun show() {
        rl.setBackgroundColor(Color.parseColor("#80000000"))
        mProgressBar.visibility = View.VISIBLE
    }

    fun hide() {
        rl.setBackgroundColor(Color.TRANSPARENT)
        mProgressBar.visibility = View.INVISIBLE
    }

    init {
        val layout = (context as Activity).findViewById<View>(android.R.id.content).rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyleLarge)
        mProgressBar.isIndeterminate = true
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        layout.addView(rl, params)
        hide()
    }
}
