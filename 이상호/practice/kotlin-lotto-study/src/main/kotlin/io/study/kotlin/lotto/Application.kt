package io.study.kotlin.lotto

import io.study.kotlin.lotto.Winnings.*

fun main() {

    println("구입금액을 입력해 주세요.")
    val purchase = Purchase.purchase(readlnOrNull().toString())
    println("${purchase.ticket}개를 구매했습니다.")

    val autoPicks = AutoPicks.numberGenerate(purchase.ticket)
    listPrintln(autoPicks)

    println("지난 주 당첨 번호를 입력해 주세요.")
    val winningLotto = WinningLotto.generate(readlnOrNull().toString())
    val rank = Statistics(winningLotto.match(autoPicks))
    val rate = Rate.calculation(rank.amountOfTotalWinning(), purchase.amountToInt)
    rankPrintln(rank, rate)
}

fun listPrintln(result: List<Lotto>) {
    result.forEach { fruit ->
        println(fruit)
    }
    println()
}

fun rankPrintln(statistics: Statistics, rate: Rate) {

    println()
    println("당첨 통계")
    println("---------")
    println("${FOURTH_PRIZE.countOfMatch}개 일치 (${FOURTH_PRIZE.amountOfPrize}원)- ${statistics.count(FOURTH_PRIZE)}개")
    println("${THIRD_PRIZE.countOfMatch}개 일치 (${THIRD_PRIZE.amountOfPrize}원)- ${statistics.count(THIRD_PRIZE)}개")
    println("${SECOND_PRIZE.countOfMatch}개 일치 (${SECOND_PRIZE.amountOfPrize}원)- ${statistics.count(SECOND_PRIZE)}개")
    println("${FIRST_PRIZE.countOfMatch}개 일치 (${FIRST_PRIZE.amountOfPrize}원)- ${statistics.count(FIRST_PRIZE)}개")
    if (rate.rate >= 1.0) {
        println("총 수익률은 ${rate.rate}입니다.")
    } else {
        println("총 수익률은 ${rate.rate}입니다.(기준이 1이기 때문에 결과적으로 손해라는 의미임)")
    }
}