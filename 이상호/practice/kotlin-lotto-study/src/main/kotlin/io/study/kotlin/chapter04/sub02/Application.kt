package io.study.kotlin.chapter04.sub02

fun main() {
    val alice = User4("Alice")
    println(alice.isSubscribed)
    val bob = User4("Bob", false)
    println(bob.isSubscribed)
    // false
    val carol = User4("Carol", isSubscribed = false)
    println(carol.isSubscribed)
    // false
    val dave = User4(nickname = "Dave", isSubscribed = true)
    println(dave.isSubscribed)
    // true

    val user = User5("Alice")
    user.address = "Christoph-Rapparini-Bogen 23"

    val lengthCounter = LengthCounter()
    println(lengthCounter.counter)
    // lengthCounter.counter = 0; // Cannot assign to 'counter': the setter is private in 'LengthCounter'
}