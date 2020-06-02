package com.example.quarantinetravel.api

import org.json.JSONObject

interface ResponseListener {
    fun onFetchResponse(response: JSONObject?)
}
