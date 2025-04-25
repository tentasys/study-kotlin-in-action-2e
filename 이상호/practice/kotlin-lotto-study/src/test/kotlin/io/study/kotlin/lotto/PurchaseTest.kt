package io.study.kotlin.lotto

import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName(name = "로또 티켓 구매 테스트")
class PurchaseTest {

    @ParameterizedTest
    @CsvSource(
        "999, 0",
        "1000, 1",
        "1999, 1",
        "2001, 2"
    )
    fun `로또 한장의 가격은 1000원이다`(amount: String, expected: String) {

        val purchase = Purchase.purchase(amount)

        purchase.ticket shouldBe expected.toInt()
    }
}