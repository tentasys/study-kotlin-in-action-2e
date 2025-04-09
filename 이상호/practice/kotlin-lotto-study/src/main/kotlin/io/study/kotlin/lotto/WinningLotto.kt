package io.study.kotlin.lotto

class WinningLotto(private val winningLotto : List<Number>) {

    companion object WinningLotto {
        fun create(input: String): List<Number>? {
            val inputs = input.split(Regex("[, ]+"))
            return inputs.stream()
                .map { Number(it.toInt()) }
                .toList();
        }
    }
}