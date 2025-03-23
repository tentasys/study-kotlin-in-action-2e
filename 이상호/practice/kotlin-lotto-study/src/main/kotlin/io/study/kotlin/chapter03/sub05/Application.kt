package io.study.kotlin.chapter03.sub05

import org.intellij.lang.annotations.Language

fun parsePath(path: String) {
    val directory = path.substringBeforeLast("/")
    val fullName = path.substringAfterLast("/")
    val extension = fullName.substringAfterLast(".")
    println("Dir: $directory, name: $fullName, ext: $extension")
}

fun parsePathRegex(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}

val kotlinLogo =
    """
    | //
    |//
    |/ \
    """.trimIndent()

@Language("JSON")
val expectedObject: String = """
    {
        "name": "Sebastian",
        "age": 27,
        "homeTown": "Munich"
    }
""".trimIndent()

fun main() {
    println("12.356-6.A".split("\\.|-".toRegex()))
    println("12.356-6.A".split('.', '-'))

    parsePath("/Users/yole/kotlin-book/chapter.adoc")
    // Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc

    parsePathRegex("/Users/yole/kotlin-book/chapter.adoc")
    // Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc

    println(kotlinLogo)
    // | //
    // |//
    // |/ \

    println(expectedObject)
    // {
    //      "name": "Sebastian",
    //      "age": 27,
    //      "homeTown": "Munich"
    // }
}