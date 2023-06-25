package moe.fuqiuluo.ext

inline fun Boolean.ifTrue(block: () -> Unit): Boolean {
    if (this)
        block()
    return this
}