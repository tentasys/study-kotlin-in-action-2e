package io.study.kotlin.lotto

import io.study.kotlin.chapter03.sub03.joinToString

class Lotto(private val lotto : List<Number>) {

    init {
        require( lotto.isNotEmpty() ) {
            throw IllegalArgumentException("로또가 비어 있습니다.")
        }
    }
    init {
        require( lotto.size == 6) {
            throw IllegalArgumentException("로또는 6자리 숫자로 이루어져 있습니다. 6자리가 맞는지 확인해주세요.")
        }
    }

    fun match(input : List<Number>) : Boolean {
        return this.lotto == input
    }

    companion object {
        fun create(input: List<Int>) : Lotto {
            val numbers = input.stream()
                .map { Number(it) }
                .toList()
            return Lotto(numbers)
        }
    }

    override fun toString(): String {
        return "[${lotto.joinToString(", ")}]"
    }
}