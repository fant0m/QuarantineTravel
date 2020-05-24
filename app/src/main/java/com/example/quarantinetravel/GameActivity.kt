package com.example.quarantinetravel

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject


class GameActivity : AppCompatActivity() {
    private lateinit var queue : RequestQueue
    private lateinit var timer : CountDownTimer
    // @todo class na questions? game /game board or something, outside of activity everything
    private val questions: MutableList<Question> = ArrayList()
    private var round = 0
    private var time = 0
    private var score = 0
    private var life = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        queue = HttpQueue.getInstance(this.applicationContext).requestQueue

        val answer1: Button = findViewById(R.id.answer1)
        answer1.setOnClickListener { checkAnswer(0) }
        val answer2: Button = findViewById(R.id.answer2)
        answer2.setOnClickListener { checkAnswer(1) }
        val answer3: Button = findViewById(R.id.answer3)
        answer3.setOnClickListener { checkAnswer(2) }

        life = 5
        drawLife()
        newRound()
        prepareQuestion()
    }

    private fun prepareQuestion (draw: Boolean = false) {
        val randomTerm = Generator.randomTerm()
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
                        val question = Question()

                        println(chosenIndexes.count())

                        while (filled < OPTIONS) {
                            val randomIndex = Generator.randomNumber(0, locationsLength - 1)
                            if (!chosenIndexes.contains(randomIndex)) {
                                val location: JSONObject = locations[randomIndex] as JSONObject
                                if (filled === 0) {
                                    question.name = "Which airport has code ${location.getString("code")}?"
                                    question.addAnswer(location.getString("name"), true)
                                } else {
                                    question.addAnswer(location.getString("name"))
                                }

                                chosenIndexes[filled] = randomIndex
                                filled++;
                            }
                        }


                        question.shuffleAnswers()
                        questions.add(question)
                        if (draw) {
                            drawRound()
                        }
                    } else {
                        prepareQuestion(draw)
                    }
                } catch (e: JSONException) {
                    // @todo error message
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    e.printStackTrace()
                }
            }
        }, Response.ErrorListener { })
        queue.add(request)
    }

    private fun newRound () {
        time = 5;
        round++;

        prepareQuestion(true)
    }

    private fun drawRound () {
        val question = questions[round - 1]

        val questionText: TextView = findViewById(R.id.question)
        questionText.text = round.toString() + ". " + question.name

        val answer1: Button = findViewById(R.id.answer1)
        answer1.text = question.answers[0].answer

        val answer2: Button = findViewById(R.id.answer2)
        answer2.text = question.answers[1].answer

        val answer3: Button = findViewById(R.id.answer3)
        answer3.text = question.answers[2].answer

        val ROUND_SECONDS = 500
        timer = object : CountDownTimer((ROUND_SECONDS * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                time = (millisUntilFinished / 1000).toInt();
                drawTime()
            }

            override fun onFinish() {
                wrongAnswer()
            }
        }.start()
    }

    private fun checkAnswer (answerIndex: Int) {
        timer.cancel()
        val question = questions[round - 1]
        println(answerIndex)
        println(question.answers[answerIndex].correct)
        if (question.answers[answerIndex].correct) {
            correctAnswer()
        } else {
            wrongAnswer()
        }
    }

    private fun setAnswerBox (answer: Boolean) {
        val textCorrect: TextView = findViewById(R.id.feedbackTextCorrect)
        val textWrong: TextView = findViewById(R.id.feedbackTextWrong)
        val imageCorrect: ImageView = findViewById(R.id.imageCorrect)
        val imageWrong: ImageView = findViewById(R.id.imageWrong)

        textCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
        imageCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
        textWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE
        imageWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE

        val feedbackLayout: ConstraintLayout = findViewById(R.id.feedback)
        feedbackLayout.visibility = View.VISIBLE

        object : CountDownTimer(1500, 1500) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                feedbackLayout.visibility = View.INVISIBLE
                newRound()
            }
        }.start()
    }

    private fun correctAnswer () {
        score++
        drawScore()
        setAnswerBox(true)
    }

    private fun wrongAnswer () {
        life--
        drawLife()
        if (life > 0) {
            setAnswerBox(false)
        } else {
            gameOver()
        }
    }

    private fun gameOver () {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Game over.. Score $score")
        builder1.setCancelable(true)

        val alert11 = builder1.create()
        alert11.show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun drawScore () {
        val scoreTextView: TextView = findViewById(R.id.scoreValue)
        scoreTextView.text = score.toString()
    }

    private fun drawLife () {
        val lifeTextView: TextView = findViewById(R.id.lifeValue)
        lifeTextView.text = life.toString()
    }

    private fun drawTime () {
        val timeTextView: TextView = findViewById(R.id.timeValue)
        timeTextView.text = (time + 1).toString()
    }
}
