package io.study.kotlin.lotto

class Number(private val number : Int) {
    init {
        require(number in 1..45) {
            throw IllegalArgumentException("Value must be between 1 and 45")
        }
    }

    override fun toString(): String {
        return "$number"
    }
}