package io.study.kotlin.chapter03.sub02

val UNIX_LINE_SEPARATOR = "\n"

fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {

    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

fun <T> joinToString2(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
) : String {
    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = listOf(1, 2, 3)
    println(joinToString(list, "; ", "(", ")"))
    // (1; 2; 3)
    println(joinToString(list, " ", " ", "."))
    // 1 2 3.
    println(joinToString(list, separator = " ", prefix = " ", postfix = "."))
    // 1 2 3.

    var list2 = setOf(1, 2, 3)
    println(joinToString2(list2, ", ", "", ""))
    // 1, 2, 3
    println(joinToString2(list2))
    // 1, 2, 3
    println(joinToString2(list2, "; "))
    // 1; 2; 3
    println(joinToString2(list2, postfix = ";", prefix = "# "))
    // # 1, 2, 3;

    println(UNIX_LINE_SEPARATOR)
    // \n
}