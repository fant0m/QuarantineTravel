package com.example.quarantinetravel.api

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

class KiwiApi constructor(context: Context) {
    private var queue = HttpQueue.getInstance(
        context.applicationContext
    ).requestQueue

    companion object {
        const val API_URL = "http://api.skypicker.com"
        const val LOCATION_URL = "/locations?"
    }

    fun getLocations (term: String, responseListener: ResponseListener) {
        val url = API_URL + LOCATION_URL + "term=${term}&locale=en-US&location_types=airport&limit=10&active_only=true&sort=name"

        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            responseListener.onFetchResponse(response)
        }, Response.ErrorListener {
            responseListener.onFetchResponse(null)
        })
        queue.add(request)
    }
}
