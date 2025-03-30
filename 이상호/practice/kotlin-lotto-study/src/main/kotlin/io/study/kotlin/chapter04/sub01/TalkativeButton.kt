package io.study.kotlin.chapter04.sub01

internal open class TalkativeButton {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

//fun TalkativeButton.giveSpeech() { // 'public' member exposes its 'internal' receiver type TalkativeButton
//    yell() // Cannot access 'yell': it is private in 'TalkativeButton'
//    whisper()  // Cannot access 'whisper': it is protected in 'TalkativeButton'
//}