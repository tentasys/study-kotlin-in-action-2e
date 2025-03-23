package io.study.kotlin.chapter03.sub01

fun createCollection() {
    val set = setOf(1, 7, 53)
    val list = listOf(1, 7, 53)
    val map = mapOf(1 to "1", 7 to "7", 53 to "53")

    println(set.javaClass)
    // 클래스 java.util.LinkedHashSet
    println(list.javaClass)
    // 클래스 java.util.Arrays$ArrayList
    println(map.javaClass)
    // 클래스 java.util.LinkedHashMap

    val strings = listOf("first", "second", "14th")
    strings.last() // 열네 번째
    println(strings.shuffled()) // [열네 번째, 두 번째, 첫 번째]
    val numbers = setOf(1, 14, 2)
    println(numbers.sum())// 17
}

fun main() {
    createCollection()
}