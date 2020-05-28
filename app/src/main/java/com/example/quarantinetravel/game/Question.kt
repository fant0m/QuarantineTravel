package com.example.quarantinetravel.game

class Question {
    var name : String = ""
    val answers: MutableList<Answer> = ArrayList()

    fun addAnswer (answer: String, correct: Boolean = false) {
       answers.add(Answer(answer, correct))
    }

    fun shuffleAnswers () {
        answers.shuffle()
    }
}