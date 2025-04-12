package io.study.kotlin.lotto

import io.study.kotlin.chapter03.sub03.joinToString

class AutoLottos(private val lottos : List<Lotto>) {

    companion object AutoLottos {
        const val sizeOfLottoNumber = 6
        fun generate(input: Int): List<Lotto> {
            val autoLottos = mutableListOf<Lotto>()
            (1..input).forEach { _ -> autoLottos.add(createLotto()) }
            return autoLottos
        }

        private fun createLotto() : Lotto {
            val autoLotto = (1..45).toList().shuffled().take(sizeOfLottoNumber)
            return Lotto.create(autoLotto)
        }
    }

    override fun toString(): String {
        return "$lottos"
    }
}