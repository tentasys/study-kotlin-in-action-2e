package io.study.kotlin.lotto

class AutoPicks(private val lottos: Int) {

    companion object AutoPicks {

        private const val NUMBER_START = 1
        private const val NUMBER_END = 45
        private const val SIZE_LOTTO_NUMBER = 6
        fun numberGenerate(input: Int): List<Lotto> {
            val autoPicks = mutableListOf<Lotto>()
            (NUMBER_START..input)
                .forEach { _ ->
                    autoPicks.add(createLotto()
                    )
                }
            return autoPicks
        }

        private fun createLotto() : Lotto {
            val autoPicks = (NUMBER_START..NUMBER_END)
                .toList()
                .shuffled()
                .take(SIZE_LOTTO_NUMBER)
                .sorted()
            return Lotto.create(autoPicks)
        }
    }

    override fun toString(): String {
        return "$lottos"
    }
}