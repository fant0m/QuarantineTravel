package com.example.quarantinetravel.api

interface GameListener {
    companion object {
        const val RESULT_OK = 1
        const val RESULT_ERROR = 2
    }
    fun onResponse(response: Int)
}
