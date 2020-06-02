package com.example.quarantinetravel.game

import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.api.KiwiApi
import com.example.quarantinetravel.api.ResponseListener
import com.example.quarantinetravel.util.Generator
import org.json.JSONException
import org.json.JSONObject

class AirportCodeQuestion(api: KiwiApi) : Question(api) {
    override val type: QuestionType
        get() = QuestionType.AIRPORT_CODE
    override val answersLength: Int
        get() = 3
    override val correctAnswerScore: Int
        get() = 1
    override val isBonus: Boolean
        get() = false

    override fun prepareQuestion(gameListener: GameListener) {
        val randomTerm = Generator.randomTerm()
        api.getLocations(randomTerm, object : ResponseListener {
            override fun onFetchResponse(response: JSONObject?) {
                if (response == null) {
                    gameListener.onResponse(GameListener.RESULT_ERROR)
                } else {
                    try {
                        val locations = response.getJSONArray("locations")
                        val locationsLength = locations.length()
                        if (locationsLength >= answersLength) {
                            val chosenIndexes = IntArray(answersLength)
                            var filled = 0

                            while (filled < answersLength) {
                                val randomIndex = Generator.randomNumber(0, locationsLength - 1)
                                if (!chosenIndexes.contains(randomIndex + 1)) {
                                    val location: JSONObject = locations[randomIndex] as JSONObject
                                    if (filled == 0) {
                                        name = "Which airport has code ${location.getString("code")}?"
                                        addAnswer(location.getString("name"), true)
                                    } else {
                                        addAnswer(location.getString("name"))
                                    }

                                    chosenIndexes[filled] = randomIndex + 1
                                    filled++
                                }
                            }

                            shuffleAnswers()

                            gameListener.onResponse(GameListener.RESULT_OK)
                        } else {
                            prepareQuestion(gameListener)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        gameListener.onResponse(GameListener.RESULT_ERROR)
                    }
                }
            }
        })
    }
}
