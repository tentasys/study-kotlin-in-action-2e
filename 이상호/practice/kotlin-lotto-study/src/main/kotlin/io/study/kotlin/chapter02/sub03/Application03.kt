package io.study.kotlin.chapter02

fun printColor() {

    println(Color.BLUE.rgb()) // 255
    Color.GREEN.printColor() // 녹색은 65280
}

fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num // 불필요한 중복
        return n.value
    }
    if (e is Sum) {
        return eval(e.left) + eval(e.right)
    }
    throw IllegalArgumentException("Unknown expression")
}

fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("num: ${e.value}")
            e.value
        }
        is Sum -> {
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("sum: $left + $right")
            left + right
        }
        else -> throw IllegalArgumentException("Unknown expression")
    }

fun main() {

    printColor()
    println(getMnemonic(Color.BLUE))
    println(getWarmthFromSensor())
    println(mix(Color.BLUE, Color.YELLOW))
    println(mixOptimized(Color.BLUE, Color.YELLOW))
    println(eval(Num(5)))
    println(evalWithLogging(Num(5)))
}