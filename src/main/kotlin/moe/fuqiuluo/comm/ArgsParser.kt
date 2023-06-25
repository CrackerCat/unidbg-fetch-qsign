package moe.fuqiuluo.comm

import moe.fuqiuluo.ext.*

class ArgsParser(
    args: StringArray
) {
    private val map = hashMapOf<String, String>()

    init {
        args.forEach {
            it.substring(if (it.startsWith("--")) 2
            else if (it.startsWith("-")) 1
            else error("Not support the expr.")).split("=").also {
                map[it[0]] = it.slice(1 until it.size).joinToString("")
            }
        }
    }

    operator fun get(key: String): String {
        return map[key]!!
    }

    operator fun get(key: String, err: String): String {
        require(key in this) { err }
        return this[key]
    }

    operator fun contains(key: String): Boolean {
        return key in map
    }
}

operator fun StringArray.invoke(): ArgsParser {
    return ArgsParser(this)
}