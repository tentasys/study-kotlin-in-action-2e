package io.study.kotlin.lotto

enum class Winnings (
    val countOfMatch: Int,
    val amountOfPrize: Long
) {
    NO_PRIZE(0, 0),
    SIXTH_PRIZE(1, 0),
    FIFTH_PRIZE(2, 0),
    FOURTH_PRIZE(3, 5_000),
    THIRD_PRIZE(4, 50_000),
    SECOND_PRIZE(5, 1_500_000),
    FIRST_PRIZE(6, 2_000_000_000);
}