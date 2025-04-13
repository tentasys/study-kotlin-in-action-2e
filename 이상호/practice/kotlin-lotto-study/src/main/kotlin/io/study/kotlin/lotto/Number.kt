package io.study.kotlin.lotto

open class Number(private var _number : Int) {

    val number: Int
        get() = _number

    // 내부에서만 값을 변경하는 메서드 제공
    fun setValue(number: Int) {
        _number = number
    }

    init {
        require(_number in 1..45) {
            throw IllegalArgumentException("로또 숫자는 1 ~ 45 사이여야 한다.")
        }
    }

    override fun toString(): String {
        return "$_number"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Number

        return _number == other._number
    }

    override fun hashCode(): Int {
        return _number
    }
}