package com.example.quarantinetravel.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.quarantinetravel.*
import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.game.Game
import com.example.quarantinetravel.util.*
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity() {
    private var googlePlayServices : GooglePlayServices = GooglePlayServices(this)
    private lateinit var timer : CountDownTimer
    private lateinit var loadingBar : LoadingBar
    private lateinit var game : Game

    private val answers = arrayOfNulls <Button>(3)
    private var init = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boardBg.z = 2F
        }
        loadingBar = LoadingBar(this)
        loadingBar.show()

        game = Game(applicationContext)

        answer1.setOnClickListener { checkAnswer(0) }
        answer2.setOnClickListener { checkAnswer(1) }
        answer3.setOnClickListener { checkAnswer(2) }
        answers[0] = answer1
        answers[1] = answer2
        answers[2] = answer3

        question.setInAnimation(this, android.R.anim.slide_in_left)
        question.setOutAnimation(this, android.R.anim.slide_out_right)

        game.init()
        drawLife()
        drawScore()
        newRound()
        game.newRound(responseListener())

        MusicManager.play(R.raw.musicgame, true)
    }

    override fun onStop() {
        super.onStop()
        MusicManager.release()
    }

    private fun newRound () {
        answers.forEach {
            it!!.background = ContextCompat.getDrawable(this,
                R.drawable.text_button
            )
        }
    }

    private fun drawRound () {
        val question = game.getCurrentQuestion()

        answers.forEach {
            it!!.isEnabled = true
        }

        val questionText: TextSwitcher = findViewById(R.id.question)
        questionText.setText(game.round.toString() + ". " + question.name)

        answer1.text = question.answers[0].answer
        answer2.text = question.answers[1].answer
        answer3.text = question.answers[2].answer

        timer = object : CountDownTimer((Game.ROUND_SECONDS * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                game.time = (millisUntilFinished / 1000).toInt()
                drawTime()
            }

            override fun onFinish() {
                game.timeOut()
                wrongAnswer(-1)
            }
        }.start()
    }

    private fun checkAnswer (answerIndex: Int) {
        SfxManager.play(resources.getInteger(R.integer.sfx_click))
        answers[answerIndex]!!.background = ContextCompat.getDrawable(this,
            R.drawable.longbuttonhover
        )
        for (answer in answers) {
            answer?.isEnabled = false
        }

        timer.cancel()
        if (game.isAnswerCorrect(answerIndex)) {
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
                val correctIndex = game.getCorrectAnswerIndex()
                if (answerIndex == correctIndex) {
                    SfxManager.play(resources.getInteger(R.integer.sfx_success))
                    answers[answerIndex]!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.longbuttonsuccess)
                } else {
                    SfxManager.play(resources.getInteger(R.integer.sfx_error))
                    if (answerIndex != -1) {
                        answers[answerIndex]!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.longbuttondanger)
                    }
                    answers[correctIndex]!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.longbuttonsuccess)
                }

                feedbackTextCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
                imageCorrect.visibility = if (answer) View.VISIBLE else View.INVISIBLE
                feedbackTextWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE
                imageWrong.visibility = if (!answer) View.VISIBLE else View.INVISIBLE
                feedback.visibility = View.VISIBLE

                drawLife()
                drawScore()
            }
        }.start()

        object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                feedback.visibility = View.INVISIBLE
                game.newRound(responseListener())
            }
        }.start()
    }

    private fun responseListener () : GameListener {
        return object : GameListener {
            override fun onResponse(response: Int) {
                if (response == GameListener.RESULT_OK) {
                    if (!init) {
                        init = true
                        loadingBar.hide()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            boardBg.z = 0F
                        }
                    }

                    newRound()
                    drawRound()
                } else {
                   // @todo error message
                   val intent = Intent(applicationContext, MainActivity::class.java)
                   startActivity(intent)
                }
            }
        }
    }

    private fun correctAnswer (answerIndex: Int) {
        setAnswerBox(true, answerIndex)
    }

    private fun wrongAnswer (answerIndex: Int) {
        if (!game.gameOver) {
            setAnswerBox(false, answerIndex)
        } else {
            SfxManager.play(resources.getInteger(R.integer.sfx_tada))
            gameOver()
        }
    }

    private fun gameOver () {
        if (game.score > 0) {
            googlePlayServices.addLeaderboardScore(game.score.toLong())
        }

        MusicManager.release()

        val intent = Intent(this, GameResultActivity::class.java)
        intent.putExtra("score", game.score)
        startActivity(intent)
    }

    private fun drawScore () {
        scoreValue.text = game.score.toString()
    }

    private fun drawLife () {
        ratingBar.numStars = game.life
        ratingBar.rating = game.life.toFloat()
    }

    private fun drawTime () {
        timeValue.text = (game.time + 1).toString()
    }
}
