package io.study.kotlin.lotto

import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DisplayName(name = "자동 로또 생성 테스트")
class AutoPicksTest : FunSpec({

    test("입력된 수 만큼 로또를 생성하며, 로또는 6개의 숫자로 생성한다.") {
        val result = AutoPicks.numberGenerate(2)

        result.size shouldBe 2
        result[0].size() shouldBe 6
    }
})