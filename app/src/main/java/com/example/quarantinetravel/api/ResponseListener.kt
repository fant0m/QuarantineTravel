package com.example.quarantinetravel.api

interface ResponseListener {
    companion object {
        const val RESULT_OK = 1
        const val RESULT_ERROR = 2
    }
    fun onFetchResponse(response: Int)
}
