package com.example.quarantinetravel.api

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quarantinetravel.activity.MainActivity
import com.example.quarantinetravel.game.Question
import com.example.quarantinetravel.util.Generator
import org.json.JSONException
import org.json.JSONObject

class KiwiApi constructor(context: Context) {

    companion object {
        const val API_URL = "http://api.skypicker.com"
        const val LOCATION_URL = "/locations?"
    }

    fun getLocation (term: String) {
        val url = API_URL + LOCATION_URL + "term=${term}&locale=en-US&location_types=airport&limit=10&active_only=true&sort=name"

        /*val request = JsonObjectRequest(url, null, Response.Listener { response ->
            return response;

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
                                if (filled === 0) {
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
                        if (!init) {
                            init = true
                            loadingBar.hide()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                boardBg.z = 0F
                            }
                        }
                        if (draw) {
                            drawRound()
                        }
                    } else {
                        prepareQuestion(draw)
                    }
                } catch (e: JSONException) {
                    val builder1 = AlertDialog.Builder(this)
                    builder1.setMessage("catch error..")
                    builder1.setCancelable(true)

                    val alert11 = builder1.create()
                    alert11.show()
                    // @todo error message
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    e.printStackTrace()
                }
            }
        }, Response.ErrorListener {
            it.printStackTrace()
            // @todo error message
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
        queue.add(request)*/
    }
}
