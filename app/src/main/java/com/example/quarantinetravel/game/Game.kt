package com.example.quarantinetravel.game

import android.content.Context
import com.example.quarantinetravel.api.KiwiApi
import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.util.Generator

class Game constructor(context: Context) {
    private val api = KiwiApi(context)

    companion object {
        const val PLAYER_LIVES = 5
        const val ROUND_SECONDS = 10
    }

    private val questions: MutableList<Question> = ArrayList()

    var time = 0
    var round = 0
        private set
    var score = 0
        private set
    var life = 0
        private set
    var gameOver = false
        private set

    fun init () {
        life = PLAYER_LIVES
    }

    fun newRound (gameListener: GameListener) {
        time = ROUND_SECONDS
        round++

        generateQuestion(gameListener)
    }

    fun timeOut () {
        wrongAnswer()
    }

    private fun wrongAnswer () {
        val question = getCurrentQuestion()
        if (!question.isBonus) {
            life--
            if (life == 0) {
                gameOver = true
            }
        }
    }

    private fun generateQuestion (gameListener: GameListener) {
        val randomQuestionType = Generator.randomDouble()
        val question: Question

        // @todo probabilities
        when {
            randomQuestionType < 0.1 -> {
                question = AirportCodeQuestion(api)
                question.prepareQuestion(gameListener)
                questions.add(question)
            }
            randomQuestionType < 0.9 -> {
                question = AirportCountryQuestion(api)
                question.prepareQuestion(gameListener)
                questions.add(question)
            }
            randomQuestionType < 0.94 -> {
                question = PriceQuestion(api)
                question.prepareQuestion(gameListener)
                questions.add(question)
            }
            randomQuestionType < 0.95 -> {
                question = DistanceQuestion(api)
                question.prepareQuestion(gameListener)
                questions.add(question)
            }
            randomQuestionType < 1 -> {
                question = DirectFlightQuestion(api)
                question.prepareQuestion(gameListener)
                questions.add(question)
            }
        }
    }

    fun getCurrentQuestion (): Question {
        return questions[round - 1]
    }

    fun isAnswerCorrect (answerIndex: Int): Boolean {
        val question = getCurrentQuestion()
        val result = question.answers[answerIndex].correct
        if (result) {
            score += question.correctAnswerScore
        } else {
            wrongAnswer()
        }

        return result;
    }

    fun getCorrectAnswerIndex (): Int {
        return getCurrentQuestion().answers.indexOfFirst { it.correct }
    }
}
