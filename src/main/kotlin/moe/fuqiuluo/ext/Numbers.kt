package moe.fuqiuluo.ext

fun String.toInt(range: IntRange, lazyMessage: () -> Any = { "Failed requirement." }): Int {
    val i = toInt()
    require(i in range, lazyMessage)
    return i
}