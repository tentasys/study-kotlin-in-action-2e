package io.study.kotlin.lotto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName(name = "로또 숫자 번호 테스트")
class NumberTest {

    @ParameterizedTest
    @CsvSource(
        "1, 1",
        "10, 10",
        "45, 45"
    )
    fun `1과 45사이의 숫자라면 반환한다`(input: Int, expected: Int) {

        val result = Number(input)

        result.number shouldBe expected
    }

    @Test
    fun `1과 45사이의 숫자가 아니라면 에러 반환한다`() {

        val exception = shouldThrow<IllegalArgumentException> {
            Number(0)
        }

        exception.message shouldBe "로또 숫자는 1 ~ 45 사이여야 한다."  // 예외 메시지 검증
    }
}