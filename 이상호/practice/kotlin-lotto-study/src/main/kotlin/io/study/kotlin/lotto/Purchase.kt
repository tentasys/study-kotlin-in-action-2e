package io.study.kotlin.lotto

class Purchase(private val _lottoTicket : Int, private val _amount : String) {

    companion object {
        private const val PRICE_OF_LOTTO = 1000
        fun purchase(input: String): Purchase {
            return Purchase(input.toInt().div(PRICE_OF_LOTTO), input)
        }
    }

    val ticket : Int
        get() = _lottoTicket

    val amountToInt : Int
        get() = _amount.toInt()
}