package io.study.kotlin.lotto

import java.util.EnumMap
import java.util.stream.Collectors

class WinningLotto(private val winningLotto : List<Number>) {

    companion object {
        fun generate(input: String): WinningLotto {
            val inputs = input.split(Regex("[, ]+"))
            return WinningLotto(inputs.stream()
                .map { Number(it.toInt()) }
                .toList())
        }
    }

    fun match(inputLottos: List<Lotto>): EnumMap<Winnings, Int> {
        val prizeMap = EnumMap<Winnings, Int>(Winnings::class.java)
        val setOfWinningLotto = this.winningLotto.stream()
            .collect(Collectors.toSet())
        inputLottos.forEachIndexed { _, numbers ->
            val setOfLottos = numbers.toSet()
            val countOfMatch = setOfLottos!!.count { it in setOfWinningLotto }
            addMap(prizeMap, rank(countOfMatch))
        }
        return prizeMap
    }

    private fun rank(countOfMatch: Int): Winnings {
        return when (countOfMatch) {
            6 -> Winnings.FIRST_PRIZE
            5 -> Winnings.SECOND_PRIZE
            3 -> Winnings.THIRD_PRIZE
            4 -> Winnings.FOURTH_PRIZE
            else -> Winnings.NO_PRIZE
        }
    }

    private fun addMap(prizeMap : EnumMap<Winnings, Int>, countOfMatch: Winnings) {
        val exist = prizeMap[countOfMatch]
        if (exist == null || exist == 0) {
            prizeMap[countOfMatch] = 1
        } else {
            val add = exist + 1
            prizeMap[countOfMatch] = add
        }
    }
    override fun toString(): String {
        return "WinningLotto(winningLotto=$winningLotto)"
    }
}