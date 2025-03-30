package io.study.kotlin.chapter04.sub04

import kotlin.random.Random

fun nextIntPlus(from : Int, until : Int) : Int = Random.nextInt(from = 0, until = 100).plus(1)

fun main() {
    Payroll.allEmployees.add(Person("name"))
    Payroll.calculateSalary()

    println(MyClass.CONSTANT_VALUE)
    // 42
    MyClass.callMe()
    // Companion object called
    // val myobject = MyClass() myobject.callMe()  // 오류: Unresolved reference: myobject

    val chance = nextIntPlus(0, 100)
    val coin = Random.Default.nextBoolean()

}