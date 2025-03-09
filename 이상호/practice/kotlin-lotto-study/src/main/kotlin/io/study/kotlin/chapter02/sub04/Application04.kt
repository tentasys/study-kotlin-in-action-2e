package io.study.kotlin.chapter02

val oneToTen = 1..10

fun fizzBuzz(i: Int) = when {
    i % 15 == 0 -> "FizzBuzz"
    i % 3 == 0 -> "Fizz"
    i % 5 == 0 -> "Buzz"
    else -> "$i"
}

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'e'

fun iteration() {
    val collection = listOf("red", "green", "blue")
    for (color in collection) {
        println("$color ")
    }
}

fun iteration2() {
    val binaryReps = mutableMapOf<Char, String>()
    for (char in 'A'..'F') {
        val binary = char.code.toString(radix = 2)
        binaryReps[char] = binary
    }
    for ((letter, binary) in binaryReps) {
        println("$letter = $binary")
    }
}

fun iteration3() {
    val list = listOf("10", "11", "1001")
    for ((index, element) in list.withIndex()) {
        println("$index: $element")
    }
}
fun main() {
    for (i in 1..100) {
        print(fizzBuzz(i))
    }
    print(isLetter('q')) // true
    print(isNotDigit('x')) // true
    iteration()
    iteration2()
    iteration3()
}