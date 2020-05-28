package com.example.quarantinetravel.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quarantinetravel.*
import com.example.quarantinetravel.api.HttpQueue
import com.example.quarantinetravel.game.Question
import com.example.quarantinetravel.utils.Generator
import com.example.quarantinetravel.utils.GooglePlayServices
import com.example.quarantinetravel.utils.LoadingBar
import kotlinx.android.synthetic.main.activity_game.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList


class GameActivity : AppCompatActivity() {
    private var googlePlayServices : GooglePlayServices = GooglePlayServices(this)
    private lateinit var queue : RequestQueue
    private lateinit var timer : CountDownTimer
    private lateinit var loadingBar : LoadingBar
    private val answers = arrayOfNulls <Button>(3)
    // @todo class na questions? game /game board or something, outside of activity everything
    private val questions: MutableList<Question> = ArrayList()
    private var round = 0
    private var time = 0
    private var score = 0
    private var life = 0
    private var init = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boardBg.z = 2F
        }
        loadingBar = LoadingBar(this)
        loadingBar.show()

        queue = HttpQueue.getInstance(
            applicationContext
        ).requestQueue

        answer1.setOnClickListener { checkAnswer(0) }
        answer2.setOnClickListener { checkAnswer(1) }
        answer3.setOnClickListener { checkAnswer(2) }
        answers[0] = answer1;
        answers[1] = answer2;
        answers[2] = answer3;

        question.setInAnimation(this, android.R.anim.slide_in_left);
        question.setOutAnimation(this, android.R.anim.slide_out_right);

        life = 5
        drawLife()
        newRound()
    }

    private fun prepareQuestion (draw: Boolean = false) {
        val randomTerm =
            Generator.randomTerm()
        println("Got term: ${randomTerm}")
        val url = "https://api.skypicker.com/locations?term=${randomTerm}&locale=en-US&location_types=airport&limit=10&active_only=true&sort=name";
        val request = JsonObjectRequest(url, null, Response.Listener { response ->
            if (null != response) {
                try {
                    val locations = response.getJSONArray("locations")
                    println("Response: ${response}")
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
                                filled++;
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
            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("Response.ErrorListener..")
            builder1.setCancelable(true)

            val alert11 = builder1.create()
            alert11.show()
        })
        queue.add(request)
    }

    private fun newRound () {
        println("starting new round")
        time = 5;
        round++;

        answers.forEach {
            it!!.background = ContextCompat.getDrawable(this,
                R.drawable.text_button
            )
        }

        prepareQuestion(true)
    }

    private fun drawRound () {
        val question = questions[round - 1]

        answers.forEach {
            it!!.isEnabled = true
        }

        val questionText: TextSwitcher = findViewById(R.id.question)
        questionText.setText(round.toString() + ". " + question.name)

        answer1.text = question.answers[0].answer
        answer2.text = question.answers[1].answer
        answer3.text = question.answers[2].answer

        val ROUND_SECONDS = 500
        timer = object : CountDownTimer((ROUND_SECONDS * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time = (millisUntilFinished / 1000).toInt();
                drawTime()
            }

            override fun onFinish() {
                wrongAnswer(-1)
            }
        }.start()
    }

    private fun checkAnswer (answerIndex: Int) {
        answers[answerIndex]!!.background = ContextCompat.getDrawable(this,
            R.drawable.longbuttonhover2
        )
        for (answer in answers) {
            answer?.isEnabled = false
        }

        timer.cancel()
        val question = questions[round - 1]
        if (question.answers[answerIndex].correct) {
            correctAnswer(answerIndex)
        } else {
            wrongAnswer(answerIndex)
        }
    }

    private fun setAnswerBox (answer: Boolean, answerIndex: Int) {
        object : CountDownTimer(500, 500) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                val question = questions[round - 1]

                val correctIndex = question.answers.indexOfFirst { it.correct }
                if (answerIndex === correctIndex) {
                    answers[answerIndex]!!.background = ContextCompat.getDrawable(applicationContext,
                        R.drawable.longbuttonsuccess
                    )
                } else {
                    if (answerIndex !== -1) {
                        answers[answerIndex]!!.background = ContextCompat.getDrawable(applicationContext,
                            R.drawable.longbuttondanger
                        )
                    }
                    answers[correctIndex]!!.background = ContextCompat.getDrawable(applicationContext,
                        R.drawable.longbuttonsuccess
                    )
                }

                feedbackTextCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
                imageCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
                feedbackTextWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE
                imageWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE
                feedback.visibility = View.VISIBLE

                drawLife()
            }
        }.start()

        object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                feedback.visibility = View.INVISIBLE
                newRound()
            }
        }.start()
    }

    private fun correctAnswer (answerIndex: Int) {
        score++
        drawScore()
        setAnswerBox(true, answerIndex)
    }

    private fun wrongAnswer (answerIndex: Int) {
        life--
        if (life > 0) {
            setAnswerBox(false, answerIndex)
        } else {
            gameOver()
        }
    }

    private fun gameOver () {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Game over.. Score $score")
        builder1.setCancelable(true)

        googlePlayServices.addLeaderboardScore(score.toLong());

        val alert11 = builder1.create()
        alert11.show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun drawScore () {
        scoreValue.text = score.toString()
    }

    private fun drawLife () {
        ratingBar.numStars = life;
        ratingBar.rating = life.toFloat();
    }

    private fun drawTime () {
        timeValue.text = (time + 1).toString()
    }
}
