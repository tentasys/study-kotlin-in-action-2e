package io.study.kotlin.lotto

class Rate(private val _rate : Double) {

    val rate: Double
        get() = _rate

    companion object {
        fun calculation(amountOfPriceTotal: Long, purchase: Int): Rate {
            val result = amountOfPriceTotal / purchase.toDouble()
            return Rate(result.roundTo2DecimalPlaces())
        }

        private fun Double.roundTo2DecimalPlaces(): Double {
            return kotlin.math.round(this * 100) / 100.0
        }
    }

    override fun toString(): String {
        return "Rate(_rate=$_rate)"
    }
}