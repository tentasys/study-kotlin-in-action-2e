package io.study.kotlin.lotto

import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName(name = "상금 비율 테스트")
class RateTest {

    @ParameterizedTest
    @CsvSource(
        "5000, 1000, 5.0"
    )
    fun `구매한 로또 비용 금액과 상금의 비율을 구한다`(amount: String, purchase: String, expected: String) {

        val rate = Rate.calculation(amount.toLong(), purchase.toInt())

        rate.rate shouldBe expected.toDouble()
    }
}
