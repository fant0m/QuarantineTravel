package com.example.quarantinetravel.game

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quarantinetravel.api.HttpQueue
import com.example.quarantinetravel.api.KiwiApi
import com.example.quarantinetravel.api.ResponseListener
import com.example.quarantinetravel.util.Generator
import org.json.JSONException
import org.json.JSONObject

class Game constructor(context: Context) {
    private val api = KiwiApi(context)
    private var queue = HttpQueue.getInstance(
        context.applicationContext
    ).requestQueue

    companion object {
        const val PLAYER_LIVES = 5
        const val ROUND_SECONDS = 50
    }

    private val questions: MutableList<Question> = ArrayList()
    var round = 0
        private set
    var time = 0
        //private set
    var score = 0
        private set
    var life = 0
        private set
    var gameOver = false
        private set

    fun init () {
        life = PLAYER_LIVES
    }

    fun newRound (responseListener: ResponseListener) {
        time = ROUND_SECONDS
        round++

        prepareQuestion(responseListener)
    }

    fun timeOut () {
        wrongAnswer()
    }

    private fun wrongAnswer () {
        life--
        if (life == 0) {
            gameOver = true
        }
    }

    private fun prepareQuestion (responseListener: ResponseListener) {
        val randomTerm =
            Generator.randomTerm()
        val url = "http://api.skypicker.com/locations?term=${randomTerm}&locale=en-US&location_types=airport&limit=10&active_only=true&sort=name"
        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            if (null != response) {
                try {
                    val locations = response.getJSONArray("locations")
                    val locationsLength = locations.length()
                    if (locationsLength >= 3) {
                        val OPTIONS = 3
                        val chosenIndexes = IntArray(OPTIONS)

                        var filled = 0
                        val question =
                            Question()

                        while (filled < OPTIONS) {
                            val randomIndex =
                                Generator.randomNumber(
                                    0,
                                    locationsLength - 1
                                )
                            if (!chosenIndexes.contains(randomIndex + 1)) {
                                val location: JSONObject = locations[randomIndex] as JSONObject
                                if (filled == 0) {
                                    question.name = "Which airport has code ${location.getString("code")}?"
                                    question.addAnswer(location.getString("name"), true)
                                } else {
                                    question.addAnswer(location.getString("name"))
                                }

                                chosenIndexes[filled] = randomIndex + 1
                                filled++
                            }
                        }


                        question.shuffleAnswers()
                        questions.add(question)

                        responseListener.onFetchResponse(ResponseListener.RESULT_OK)
                    } else {
                        prepareQuestion(responseListener)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    responseListener.onFetchResponse(ResponseListener.RESULT_ERROR)
                }
            }
        }, Response.ErrorListener {
            it.printStackTrace()
            responseListener.onFetchResponse(ResponseListener.RESULT_ERROR)
        })
        queue.add(request)
    }

    fun getCurrentQuestion (): Question {
        return questions[round - 1]
    }

    fun isAnswerCorrect (answerIndex: Int): Boolean {
        val result = getCurrentQuestion().answers[answerIndex].correct
        if (result) {
            // @todo get dynamic score from question
            score++;
        } else {
            wrongAnswer()
        }

        return result;
    }

    fun getCorrectAnswerIndex (): Int {
        return getCurrentQuestion().answers.indexOfFirst { it.correct }
    }
}
