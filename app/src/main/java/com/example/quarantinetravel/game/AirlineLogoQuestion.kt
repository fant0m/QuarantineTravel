package com.example.quarantinetravel.game

import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.api.KiwiApi
import com.example.quarantinetravel.api.ResponseListener
import org.json.JSONObject

class AirlineLogoQuestion(api: KiwiApi) : Question(api) {
    override val type: QuestionType
        get() = QuestionType.AIRLINE_LOGO
    override val answersLength: Int
        get() = 3
    override val correctAnswerScore: Int
        get() = 1
    override val isBonus: Boolean
        get() = false
    lateinit var logoUrl : String

    override fun prepareQuestion(gameListener: GameListener) {
        api.getAirlines(3, object : ResponseListener {
            override fun onFetchResponse(response: JSONObject?) {
                if (response == null) {
                    gameListener.onResponse(GameListener.RESULT_ERROR)
                } else {
                    val airlines = response.getJSONArray("airlines")

                    name = "Which airline has following logo?"
                    logoUrl = KiwiApi.AIRLINES_LOGO_URL + (airlines[0] as JSONObject).getString("id") + ".png"

                    addAnswer((airlines[0] as JSONObject).getString("name"), true)
                    addAnswer((airlines[1] as JSONObject).getString("name"))
                    addAnswer((airlines[2] as JSONObject).getString("name"))

                    gameListener.onResponse(GameListener.RESULT_OK)
                }
            }
        })
    }
}