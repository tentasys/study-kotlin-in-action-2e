package io.study.kotlin.lotto

import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

@DisplayName(name = "당첨번호 매칭 테스트")
class WinningLottoTest {

    @Test
    fun `자동 로또로 구매한 로또와 지난주 당첨번호 결과를 확인한다`() {

        val lottoOfFirst = listOf(Number(1), Number(2), Number(3), Number(4), Number(5), Number(6))
        val lottoOfSecond = listOf(Number(2), Number(3), Number(4), Number(5), Number(6), Number(7))
        val lottos = listOf(Lotto(lottoOfSecond), Lotto(lottoOfFirst))
        val winningLotto = WinningLotto(listOf(Number(1), Number(2), Number(3), Number(4), Number(5), Number(6)))

        val winnings = winningLotto.match(lottos)

        winnings[Winnings.FIRST_PRIZE] shouldBe 1
        winnings[Winnings.SECOND_PRIZE] shouldBe 1
    }
}