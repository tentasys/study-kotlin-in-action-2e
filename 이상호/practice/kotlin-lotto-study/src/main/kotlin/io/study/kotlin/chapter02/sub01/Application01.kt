package io.study.kotlin.chapter02

fun printStr(str: String) {
    println(str)
}

/**
 * max 함수
 */
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}

/**
 * 리팩토링 첫번째 max 함수
 * - return 제거
 */
fun max2(a: Int, b: Int): Int = if (a > b) a else b

/**
 * 리팩토링 두번째 max 함수
 * - 반환 type 제거
 */
fun max3(a: Int, b: Int) = if (a > b) a else b

fun saveParameter() {
    //val question = "삶, 우주, 그리고 모든 것에 대한 궁극적인 질문"
    //val answer = 42
    val question: String = "삶, 우주, 그리고 모든 것에 대한 궁극적인 질문"
    val answer: Int = 42
    println(question)
    println(answer)
}

fun template() {
    val input = readln()
    val name = input.ifBlank { "Kotlin" }
    println("안녕하세요, $name!")
}

fun variable() {
    val answer: Int
    answer = 42
    println(answer)
}

fun canPerformOperation(): Boolean {
    return true
}

fun result2() {
    val result: String = if (canPerformOperation()) {
        "Success"
    } else {
        "Can't perform operation"
    }
    println(result)
}

//fun variable2() {
//    var answer = 42
//    answer = "no answer"
//    println(answer)
//}

fun main() {
    printStr("Hello, world!")
    println(max(1, 2))
    println(max2(1, 2))
    println(max3(1, 2))
    saveParameter()
    template()
    variable()
    result2()
//    variable2()
}