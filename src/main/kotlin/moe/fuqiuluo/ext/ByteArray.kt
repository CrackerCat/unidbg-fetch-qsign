package moe.fuqiuluo.ext

import moe.fuqiuluo.utils.BytesUtil
import java.util.*
import kotlin.experimental.xor

@JvmOverloads fun String.hex2ByteArray(replace: Boolean = false): ByteArray {
    val s = if (replace) this.replace(" ", "")
        .replace("\n", "")
        .replace("\t", "")
        .replace("\r", "") else this
    val bs = ByteArray(s.length / 2)
    for (i in 0 until s.length / 2) {
        bs[i] = s.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
    return bs
}

@JvmOverloads fun ByteArray.toHexString(uppercase: Boolean = true): String = this.joinToString("") {
    (it.toInt() and 0xFF).toString(16)
        .padStart(2, '0')
        .let { s -> if (uppercase) s.uppercase(Locale.getDefault()) else s }
}

fun ByteArray.xor(key: ByteArray): ByteArray {
    val result = ByteArray(this.size)
    for (i in 0 until this.size) {
        result[i] = (this[i] xor key[i % key.size] xor ((i and 0xFF).toByte()))
    }
    return result
}

fun ByteArray.sub(offset: Int, length: Int) = BytesUtil.subByte(this, offset, length)

fun ByteArray.toAsciiHexString() = joinToString("") {
    if (it in 32..127) it.toInt().toChar().toString() else "{${
        it.toUByte().toString(16).padStart(2, '0').uppercase(
            Locale.getDefault())
    }}"
}