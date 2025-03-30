package io.study.kotlin.chapter04.sub02

class User5(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println(
                """
		        Address was changed for $name:
    		    "$field" -> "$value".
        		""".trimIndent()
            )
            field = value
        }
}