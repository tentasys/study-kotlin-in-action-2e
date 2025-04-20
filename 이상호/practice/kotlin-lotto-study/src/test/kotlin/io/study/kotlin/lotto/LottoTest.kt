package io.study.kotlin.lotto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName(name = "로또 테스트")
class LottoTest : FunSpec({

    test("로또가 6자리 숫자로 이루어지지 않았다면 에러를 반환한다.") {

        val exception = shouldThrow<IllegalArgumentException> {
            Lotto(listOf(Number(1), Number(2), Number(3), Number(4), Number(5)
                , Number(6), Number(7)))
        }

        exception.message shouldBe "로또는 6자리 숫자로 이루어져 있습니다. 6자리가 맞는지 확인해주세요."  // 예외 메시지 검증
    }

    test("로또가 비어 있다면 에러를 반환한다.") {

        val exception = shouldThrow<IllegalArgumentException> {
            Lotto(emptyList())
        }

        exception.message shouldBe "로또가 비어 있습니다."  // 예외 메시지 검증
    }
})