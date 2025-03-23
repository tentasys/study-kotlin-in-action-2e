package io.study.kotlin.chapter04.sub01

class Button2 : View2 {
    override fun getCurrentState(): State = ButtonState()
    override fun restoreState(state: State) {}
    class ButtonState : State {}
}