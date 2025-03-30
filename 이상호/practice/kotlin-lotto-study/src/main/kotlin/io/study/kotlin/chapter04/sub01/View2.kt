package io.study.kotlin.chapter04.sub01

interface View2 {
    fun getCurrentState(): State
    fun restoreState(state: State) {}
}