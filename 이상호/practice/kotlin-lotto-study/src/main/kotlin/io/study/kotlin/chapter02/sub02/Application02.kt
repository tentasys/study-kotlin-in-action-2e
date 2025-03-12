package io.study.kotlin.chapter02

fun printPerson() {

    val person = Person("Bob", true)
    println(person.name)
    println(person.isStudent)
    person.isStudent = false
    println(person.isStudent)
}

fun printRectangle() {

    val rectangle = Rectangle(5, 5);
    println(rectangle.isSquare)
}

fun main() {

    printPerson()
    printRectangle()
}
