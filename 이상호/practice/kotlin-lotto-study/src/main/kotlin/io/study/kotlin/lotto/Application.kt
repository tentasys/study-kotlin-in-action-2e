package io.study.kotlin.lotto

fun main() {

    println("구입금액을 입력해 주세요.")
    val number = readlnOrNull().toString()
    val ticket = Purchase.purchase(number)
    println("$ticket 개를 구매했습니다.")
    val auttoLottos = AutoLottos.generate(ticket)
    println(auttoLottos)

    println("지난 주 당첨 번호를 입력해 주세요.")
    val winnings = readlnOrNull().toString()
    println(winnings)
}