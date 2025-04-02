package io.study.kotlin.lotto

class Purchase(private val ticket : Int) {

    companion object Purchase {
        const val priceOfLotto = 1000
        fun purchase(input: String): Int {
            return input.toInt().div(priceOfLotto)
        }
    }
}