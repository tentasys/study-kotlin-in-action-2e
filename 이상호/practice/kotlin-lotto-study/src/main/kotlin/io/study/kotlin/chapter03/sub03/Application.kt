package io.study.kotlin.chapter03.sub03

fun String.lastChar(): Char = this.get(this.length - 1)

fun String.lastChar2(): Char = get(length - 1)

fun <T> Collection<T>.joinToString( // Collection<T>에 대한 확장 함수 선언
    separator: String = ", ", // 파라미터 기본값 지정
    prefix: String = "",
    postfix: String = ""
) : String {
    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

fun Collection<String>.join(
    separator: String = ", ", // 파라미터 기본값 지정
    prefix: String = "",
    postfix: String = ""
) = joinToString(separator, prefix, postfix)

// 변경 가능한 확장 프로퍼티 선언하기
var StringBuilder.lastChar: Char
    get() = get(length - 1)       // 프로퍼티 게터
    set(value: Char) {            // 프로퍼티 세터
        this.setCharAt(length - 1, value)
    }

fun main() {
    println("Kotlin".lastChar())
    // n
    println("Kotlin".lastChar2())
    // n

    val list = listOf(1, 2, 3)
    println(
        list.joinToString(
            separator = "; ",
            prefix = "(",
            postfix = ")"
        )
    )
    // (1; 2; 3)

    println(listOf("one", "two", "eight").join(" "))
    // one two eight

    // listOf(1, 2, 8).join()
    // Kotlin: Unresolved reference. None of the following candidates is applicable because of receiver type mismatch:

    val sb = StringBuilder("Kotlin?")
    println(sb.lastChar)
    // ?
    sb.lastChar = '!'
    println(sb)
    // Kotlin!
}