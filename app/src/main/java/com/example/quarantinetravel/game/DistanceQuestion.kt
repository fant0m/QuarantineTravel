package com.example.quarantinetravel.game

import android.annotation.SuppressLint
import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.api.KiwiApi
import com.example.quarantinetravel.api.ResponseListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DistanceQuestion(api: KiwiApi) : Question(api) {
    override val type: QuestionType
        get() = QuestionType.DISTANCE
    override val answersLength: Int
        get() = 3
    override val correctAnswerScore: Int
        get() = 1
    override val isBonus: Boolean
        get() = false

    override fun prepareQuestion(gameListener: GameListener) {
        api.getPopularLocations(2, object : ResponseListener {
            override fun onFetchResponse(response: JSONObject?) {
                if (response == null) {
                    gameListener.onResponse(GameListener.RESULT_ERROR)
                } else {
                    val airports = response.getJSONArray("airports")
                    fetchFlights (airports, gameListener)
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun fetchFlights(airports: JSONArray, gameListener: GameListener) {
        val startAirport = airports[0] as JSONObject
        val endAirport = airports[1] as JSONObject

        val format = SimpleDateFormat("dd/MM/yyyy")
        val cal = Calendar.getInstance()

        cal.add(Calendar.MONTH, 4)
        val dateFrom = format.format(cal.time)

        cal.add(Calendar.MONTH, 1)
        val dateTo = format.format(cal.time)

        // find direct flights between chosen airports
        api.getFlights(
            startAirport.getString("code"),
            endAirport.getString("code"),
            dateFrom,
            dateTo,
            0,
            1,
            object : ResponseListener {
                override fun onFetchResponse(response: JSONObject?) {
                    if (response == null) {
                        gameListener.onResponse(GameListener.RESULT_ERROR)
                    } else {
                        try {
                            val data = response.getJSONArray("data")
                            val dataLength = data.length()
                            if (dataLength == 0) {
                                return prepareQuestion(gameListener)
                            } else {
                                name = "What is the distance between ${startAirport.getString("name")} and ${endAirport.getString("name")} airports?"

                                val distance = (data[0] as JSONObject).getDouble("distance")

                                addAnswer("$distance km", true)
                                addAnswer(String.format("%.2f", distance * 0.33) + " km")
                                addAnswer(String.format("%.2f", distance * 1.66) + " km")

                                shuffleAnswers()

                                gameListener.onResponse(GameListener.RESULT_OK)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            gameListener.onResponse(GameListener.RESULT_ERROR)
                        }
                    }
                }
            }
        )
    }
}
