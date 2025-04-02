package io.study.kotlin.lotto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class NumberBehaviorSpec : AnnotationSpec() {
    @Test
    fun `1과 45사이의 숫자라면 반환한다`() {
        val result = Number(1)

        result.number shouldBe 1
    }

    @Test
    fun `1과 45사이의 숫자가 아니라면 에러 반환한다`() {

        val exception = shouldThrow<IllegalArgumentException> {
            Number(0)
        }

        exception.message shouldBe "Value must be between 1 and 45"  // 예외 메시지 검증
    }
}