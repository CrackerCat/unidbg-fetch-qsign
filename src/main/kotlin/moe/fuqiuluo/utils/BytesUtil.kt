package moe.fuqiuluo.utils

import kotlin.random.Random

val EMPTY_BYTE_ARRAY = byteArrayOf()

object BytesUtil {
    @JvmStatic
    fun byteMerger(first: ByteArray, second: ByteArray): ByteArray {
        val result = first.copyOf(first.size + second.size)
        System.arraycopy(second, 0, result, first.size, second.size)
        return result
    }

    @JvmStatic
    fun int16ToBuf(i: Int): ByteArray {
        val out = ByteArray(2)
        out[1] = i.toByte()
        out[0] = (i shr 8).toByte()
        return out
    }

    @JvmStatic
    fun int32ToBuf(i: Int): ByteArray {
        val out = ByteArray(4)
        out[3] = i.toByte()
        out[2] = (i shr 8).toByte()
        out[1] = (i shr 16).toByte()
        out[0] = (i shr 24).toByte()
        return out
    }

    @JvmStatic
    fun int64ToBuf(j: Long): ByteArray {
        val out = ByteArray(8)
        out[7] = j.toInt().toByte()
        out[6] = ((j shr 8).toInt()).toByte()
        out[5] = ((j shr 16).toInt()).toByte()
        out[4] = ((j shr 24).toInt()).toByte()
        out[3] = ((j shr 32).toInt()).toByte()
        out[2] = ((j shr 40).toInt()).toByte()
        out[1] = ((j shr 48).toInt()).toByte()
        out[0] = ((j shr 56).toInt()).toByte()
        return out
    }

    @JvmStatic
    fun int64ToBuf32(j: Long): ByteArray {
        val out = ByteArray(4)
        out[3] = j.toInt().toByte()
        out[2] = ((j shr 8).toInt()).toByte()
        out[1] = ((j shr 16).toInt()).toByte()
        out[0] = ((j shr 24).toInt()).toByte()
        return out
    }

    @JvmStatic
    fun float2byte(f: Float): ByteArray {
        val fbit = java.lang.Float.floatToIntBits(f)
        val b = ByteArray(4)
        for (i in 0..3) {
            b[i] = (fbit shr 24 - i * 8).toByte()
        }
        val len = b.size
        val dest = ByteArray(len)
        System.arraycopy(b, 0, dest, 0, len)
        var temp: Byte
        for (i in 0 until len / 2) {
            temp = dest[i]
            dest[i] = dest[len - i - 1]
            dest[len - i - 1] = temp
        }
        return dest
    }

    @JvmStatic
    fun doubleToByte(num: Double): ByteArray {
        val b = ByteArray(8)
        var l = java.lang.Double.doubleToLongBits(num)
        for (i in 0..7) {
            b[i] = l.toByte()
            l = l shr 8
        }
        return b
    }

    @JvmStatic
    fun getDouble(b: ByteArray): Double {
        var m: Long = b[0].toLong()
        m = m and 0xff
        m = m or (b[1].toLong() shl 8)
        m = m and 0xffff
        m = m or (b[2].toLong() shl 16)
        m = m and 0xffffff
        m = m or (b[3].toLong() shl 24)
        m = m and 0xffffffffL
        m = m or (b[4].toLong() shl 32)
        m = m and 0xffffffffffL
        m = m or (b[5].toLong() shl 40)
        m = m and 0xffffffffffffL
        m = m or (b[6].toLong() shl 48)
        m = m and 0xffffffffffffffL
        m = m or (b[7].toLong() shl 56)
        return java.lang.Double.longBitsToDouble(m)
    }

    @JvmStatic
    fun byte2float(b: ByteArray, index: Int): Float {
        var l = b[index + 0].toInt()
        l = l and 0xff
        l = l or (b[index + 1].toLong() shl 8).toInt()
        l = l and 0xffff
        l = l or (b[index + 2].toLong() shl 16).toInt()
        l = l and 0xffffff
        l = l or (b[index + 3].toLong() shl 24).toInt()
        return java.lang.Float.intBitsToFloat(l)
    }

    @JvmStatic
    fun bufToInt8(bArr: ByteArray, i: Int): Int {
        return bArr[i].toInt() and 255
    }

    @JvmStatic
    fun bufToInt16(bArr: ByteArray, i: Int): Int {
        return (bArr[i].toInt() shl 8 and 65280) + (bArr[i + 1].toInt() and 255)
    }

    @JvmStatic
    fun bufToInt32(bArr: ByteArray, i: Int = 0): Int {
        return (bArr[i].toInt() shl 24 and -16777216) +
                (bArr[i + 1] .toInt() shl 16 and 16711680) +
                (bArr[i + 2].toInt() shl 8 and 65280) +
                (bArr[i + 3].toInt() and 255)
    }

    @JvmStatic
    fun bufToInt64(bArr: ByteArray, i: Int): Long {
        return (bArr[i].toLong() shl 56 and -72057594037927936L) +
                (bArr[i + 1].toLong() shl 48 and 71776119061217280L) +
                (bArr[i + 2].toLong() shl 40 and 280375465082880L) +
                (bArr[i + 3].toLong() shl 32 and 1095216660480L) +
                (bArr[i + 4].toLong() shl 24 and 4278190080L) +
                (bArr[i + 5].toLong() shl 16 and 16711680) +
                (bArr[i + 6].toLong() shl 8 and 65280) +
                (bArr[i + 7].toLong() and 255)
    }

    @JvmStatic
    fun subByte(b: ByteArray, off: Int, length: Int): ByteArray? {
        if (b.isNotEmpty()) {
            val b1 = ByteArray(length)
            System.arraycopy(b, off, b1, 0, length)
            return b1
        }
        return null
    }

    @JvmStatic
    fun randomKey(size: Int) = Random.nextBytes(size)
}