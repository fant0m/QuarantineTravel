package com.example.quarantinetravel.api

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class HttpQueue constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: HttpQueue? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: HttpQueue(context).also {
                    INSTANCE = it
                }
            }
    }
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        req.retryPolicy = DefaultRetryPolicy(
            10 * 1000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(req)
    }
}
