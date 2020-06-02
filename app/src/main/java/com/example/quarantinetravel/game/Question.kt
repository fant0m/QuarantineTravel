package com.example.quarantinetravel.game

import com.example.quarantinetravel.api.GameListener
import com.example.quarantinetravel.api.KiwiApi

abstract class Question(val api: KiwiApi) {
    var name : String = ""
    val answers: MutableList<Answer> = ArrayList()
    abstract val type : QuestionType
    abstract val answersLength: Int
    abstract val correctAnswerScore: Int

    fun addAnswer (answer: String, correct: Boolean = false) {
       answers.add(Answer(answer, correct))
    }

    fun shuffleAnswers () {
        answers.shuffle()
    }

    abstract fun prepareQuestion (gameListener : GameListener)
}
