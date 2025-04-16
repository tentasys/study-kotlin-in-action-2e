package io.study.kotlin.lotto

import io.kotest.core.annotation.DisplayName
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

@DisplayName("통계 테스트")
class StatisticsTest {

    @Test
    fun `당첨된 상금 총합을 구한다`() {

        val lottoOfFirst = listOf(Number(1), Number(2), Number(3), Number(4), Number(5), Number(6))
        val lottoOfSecond = listOf(Number(2), Number(3), Number(4), Number(5), Number(6), Number(7))
        val lottos = listOf(Lotto(lottoOfSecond), Lotto(lottoOfFirst))
        val winningLotto = WinningLotto(listOf(Number(1), Number(2), Number(3), Number(4), Number(5), Number(6)))
        val winnings = winningLotto.match(lottos)

        val statistics = Statistics(winnings)

        statistics.amountOfTotalWinning() shouldBe 2_001_500_000
    }
}