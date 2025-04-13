package io.study.kotlin.lotto

import java.util.stream.Collectors

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

    companion object {
        fun create(input: List<Int>) : Lotto {
            val numbers = input.stream()
                .map { Number(it) }
                .toList()
            return Lotto(numbers)
        }
    }

    fun toSet(): Set<Number>? {
        return this.lotto.stream()
            .collect(Collectors.toSet())
    }

    fun size() : Int {
        return this.lotto.size
    }

    override fun toString(): String {
        return "[${lotto.joinToString(", ")}]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lotto

        return lotto == other.lotto
    }

    override fun hashCode(): Int {
        return lotto.hashCode()
    }
}