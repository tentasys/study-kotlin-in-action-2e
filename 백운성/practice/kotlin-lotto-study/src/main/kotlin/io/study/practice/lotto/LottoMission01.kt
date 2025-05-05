package io.study.practice.lotto

import java.util.Scanner

val PRIZE_MAP = linkedMapOf<Int, Long>(
    Pair(3, 5_000),
    Pair(4, 50_000),
    Pair(5, 1_500_000),
    Pair(6, 2_000_000_000),
)
const val GAME_PRICE: Long = 1_000
const val NUMBER_COUNT = 6

fun main() {
    val sc = Scanner(System.`in`)
    println("구입금액을 입력해 주세요.")
    val money = sc.nextLine()
    val count = money.toLong().div(GAME_PRICE)

    val availableNumbers = mutableListOf<Int>().apply {
        for (n in 1..45) this.add(n)
    }
    val purchasedGames = mutableListOf<LottoGame>()
    println("${count}개를 구매했습니다.")
    for (x in 1..count) {
        val gameNumbers = generateRandomNumbers(availableNumbers, NUMBER_COUNT).sorted()
        println(gameNumbers)
        purchasedGames.add(LottoGame(GAME_PRICE, gameNumbers))
    }

    println()
    println("지난 주 당첨 번호를 입력해 주세요.")
    val inputWinNumbers = sc.nextLine()
//    val inputWinNumbers = "1, 2, 3, 4, 5, 6"
//    println(inputWinNumbers)
    val winNumbers = inputWinNumbers.trim()
        .replace(" ", "")
        .split(",")
        .map { it.toInt() }
        .toList()

    printReport(winNumbers, purchasedGames)
}

fun generateRandomNumbers(availableNumbers: MutableList<Int>, count: Int): List<Int> {
    return availableNumbers.shuffled()
        .slice(1..count)
}

fun printReport(winNumbers: List<Int>, purchasedGames: MutableList<LottoGame>) {

    println()
    println("당첨 통계")
    println("---------")

    var totalGameMoney: Long = 0
    var totalPrizeMoney: Long = 0
    // 등수, 당첨 갯수
    var resultMap = mutableMapOf<Int, Int>()

    for (game in purchasedGames) {
        val union = winNumbers + game.numbers
        val matchedNumbers = union.groupBy { it }
            .filter { it.value.size > 1 }
            .flatMap { it.value }
            .distinct()
        val matchedCount = matchedNumbers.size
        resultMap[matchedCount] = resultMap.getOrDefault(matchedCount, 0) + 1
        totalGameMoney += game.price
        totalPrizeMoney += PRIZE_MAP.getOrDefault<Int, Long>(matchedCount, 0)
    }

    PRIZE_MAP.entries
        .forEach { entry -> println("${entry.key}개 일치 (${entry.value}원)- ${resultMap.getOrDefault(entry.key, 0)}개") }
    println("총 수익률은 ${totalPrizeMoney.toFloat() / totalGameMoney.toFloat()}입니다.(기준이 1이기 때문에 결과적으로 손해라는 의미임)")
}