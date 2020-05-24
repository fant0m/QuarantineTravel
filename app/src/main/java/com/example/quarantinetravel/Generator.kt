package com.example.quarantinetravel

class Generator {
    companion object Factory {
        fun randomTerm (length: Int = 3): String {
            val stringLength = randomNumber(1, length)
            var term = "";
            repeat (stringLength) {
                term += randomLetter()
            }
            return term
        }

        fun randomLetter (): Char {
            return ('a' until 'z').random()
        }

        fun randomNumber (from: Int, to: Int): Int {
            return (from until to + 1).random()
        }
    }

}