package io.study.kotlin.lotto

import java.util.EnumMap

class Statistics(private val rank : EnumMap<Winnings, Int>) {

    fun count(winning : Winnings) : Int? {
        if (this.rank[winning] == null) {
            return 0
        }
        return this.rank[winning]
    }

    fun amountOfTotalWinning() : Long {
        var sum = 0L
        this.rank.filterValues { it >= 1 }
            .forEach { (key, count) ->
                sum += key.amountOfPrize * count
            }
        return sum
    }
}