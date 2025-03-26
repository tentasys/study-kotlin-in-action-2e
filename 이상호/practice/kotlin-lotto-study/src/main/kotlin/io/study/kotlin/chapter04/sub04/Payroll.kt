package io.study.kotlin.chapter04.sub04

object Payroll {

    val allEmployees = mutableListOf<Person>()
    fun calculateSalary() {
        for (person in allEmployees) {
            /* ... */
        }
    }
}