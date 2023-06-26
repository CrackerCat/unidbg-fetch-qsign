package moe.xinrao.unidbg.env

object FEBound {
    private const val LEVEL1 = 32
    private const val Type_Decode = 2
    private const val Type_Encode = 1

    var mConfigEnCode = arrayOf(
        byteArrayOf(2, 5, 0, 14, 15, 3, 10, 1, 13, 12, 7, 11, 6, 8, 4, 9),
        byteArrayOf(14, 13, 12, 5, 7, 15, 10, 11, 4, 6, 2, 3, 0, 8, 9, 1),
        byteArrayOf(10, 5, 6, 13, 15, 3, 11, 8, 2, 9, 0, 14, 12, 4, 7, 1),
        byteArrayOf(6, 14, 3, 0, 9, 10, 8, 13, 4, 11, 15, 5, 2, 1, 12, 7),
        byteArrayOf(2, 6, 11, 12, 15, 7, 5, 8, 1, 13, 4, 0, 3, 14, 9, 10),
        byteArrayOf(14, 0, 6, 3, 2, 12, 4, 15, 8, 1, 5, 9, 10, 7, 11, 13),
        byteArrayOf(10, 7, 0, 13, 2, 4, 1, 15, 5, 12, 14, 6, 8, 9, 11, 3),
        byteArrayOf(5, 1, 15, 7, 2, 10, 11, 12, 13, 0, 14, 6, 3, 4, 8, 9),
        byteArrayOf(2, 7, 5, 10, 14, 3, 1, 12, 4, 11, 9, 6, 15, 0, 13, 8),
        byteArrayOf(13, 2, 6, 7, 9, 1, 5, 4, 8, 10, 12, 15, 0, 14, 11, 3),
        byteArrayOf(9, 10, 6, 15, 2, 12, 5, 13, 0, 3, 1, 8, 7, 11, 14, 4),
        byteArrayOf(5, 3, 11, 15, 8, 14, 1, 9, 6, 0, 4, 7, 13, 2, 12, 10),
        byteArrayOf(1, 11, 5, 6, 15, 8, 7, 13, 2, 4, 9, 14, 3, 12, 0, 10),
        byteArrayOf(13, 3, 15, 14, 7, 0, 2, 5, 12, 8, 4, 10, 9, 6, 1, 11),
        byteArrayOf(9, 12, 10, 5, 15, 2, 7, 13, 4, 11, 3, 14, 6, 1, 0, 8),
        byteArrayOf(5, 4, 1, 13, 7, 2, 12, 8, 14, 0, 3, 11, 9, 15, 10, 6),
        byteArrayOf(1, 12, 15, 5, 2, 0, 10, 7, 14, 8, 3, 4, 13, 9, 6, 11),
        byteArrayOf(13, 5, 9, 12, 7, 4, 14, 10, 3, 0, 2, 8, 11, 6, 1, 15),
        byteArrayOf(9, 13, 3, 4, 15, 1, 14, 0, 5, 11, 10, 8, 7, 12, 6, 2),
        byteArrayOf(5, 0, 14, 11, 7, 10, 2, 9, 4, 6, 3, 1, 15, 8, 12, 13),
        byteArrayOf(1, 14, 8, 3, 0, 6, 10, 7, 9, 15, 2, 5, 11, 13, 4, 12),
        byteArrayOf(13, 6, 3, 10, 1, 15, 9, 0, 7, 12, 2, 8, 5, 14, 4, 11),
        byteArrayOf(9, 14, 13, 2, 15, 5, 4, 11, 0, 6, 1, 12, 8, 10, 3, 7),
        byteArrayOf(4, 9, 14, 7, 8, 12, 2, 5, 10, 13, 3, 6, 0, 1, 11, 15),
        byteArrayOf(0, 1, 9, 15, 12, 11, 4, 14, 5, 10, 6, 8, 3, 7, 13, 2),
        byteArrayOf(12, 9, 3, 7, 8, 1, 10, 6, 11, 0, 5, 2, 13, 4, 14, 15),
        byteArrayOf(8, 2, 13, 14, 5, 15, 3, 7, 10, 4, 6, 11, 12, 1, 0, 9),
        byteArrayOf(4, 10, 8, 6, 2, 5, 15, 9, 14, 0, 13, 7, 3, 12, 1, 11),
        byteArrayOf(0, 2, 1, 13, 15, 11, 10, 4, 7, 9, 14, 8, 3, 6, 5, 12),
        byteArrayOf(12, 11, 13, 5, 7, 0, 15, 6, 4, 3, 2, 10, 8, 1, 9, 14),
        byteArrayOf(8, 3, 7, 12, 15, 9, 0, 1, 14, 5, 4, 2, 13, 11, 10, 6),
        byteArrayOf(3, 14, 8, 2, 0, 1, 11, 6, 15, 12, 13, 10, 9, 4, 7, 5)
    )
    var mConfigDeCode = arrayOf(
        byteArrayOf(11, 0, 9, 6, 14, 8, 4, 1, 2, 7, 10, 3, 15, 12, 13, 5),
        byteArrayOf(11, 9, 3, 2, 6, 15, 5, 13, 12, 4, 10, 14, 8, 0, 7, 1),
        byteArrayOf(12, 10, 14, 7, 2, 13, 11, 5, 0, 3, 1, 15, 4, 6, 8, 9),
        byteArrayOf(8, 13, 1, 15, 0, 7, 11, 6, 14, 5, 3, 10, 4, 12, 2, 9),
        byteArrayOf(0, 13, 3, 12, 9, 11, 7, 6, 2, 5, 1, 15, 4, 14, 10, 8),
        byteArrayOf(12, 2, 10, 1, 5, 7, 14, 9, 8, 6, 4, 11, 0, 15, 13, 3),
        byteArrayOf(13, 11, 14, 10, 8, 5, 4, 3, 0, 15, 9, 1, 12, 2, 7, 6),
        byteArrayOf(15, 6, 12, 1, 0, 10, 13, 14, 2, 11, 4, 7, 5, 8, 3, 9),
        byteArrayOf(0, 6, 2, 8, 9, 10, 3, 7, 12, 5, 11, 15, 1, 13, 14, 4),
        byteArrayOf(2, 7, 10, 5, 1, 9, 12, 15, 13, 6, 4, 8, 0, 3, 14, 11),
        byteArrayOf(15, 14, 7, 12, 13, 3, 6, 2, 0, 4, 8, 10, 1, 9, 5, 11),
        byteArrayOf(14, 9, 7, 11, 0, 1, 4, 13, 15, 3, 5, 12, 2, 10, 8, 6),
        byteArrayOf(0, 8, 12, 11, 3, 14, 15, 13, 7, 4, 10, 9, 2, 1, 6, 5),
        byteArrayOf(3, 5, 11, 9, 12, 1, 2, 8, 14, 4, 13, 15, 0, 6, 7, 10),
        byteArrayOf(9, 10, 2, 14, 6, 4, 8, 13, 0, 12, 5, 3, 11, 7, 1, 15),
        byteArrayOf(13, 8, 3, 11, 0, 14, 1, 12, 7, 4, 5, 2, 10, 6, 15, 9),
        byteArrayOf(0, 14, 6, 4, 3, 2, 9, 10, 8, 5, 13, 11, 15, 7, 1, 12),
        byteArrayOf(10, 4, 6, 9, 12, 7, 15, 1, 3, 2, 13, 5, 0, 14, 11, 8),
        byteArrayOf(6, 9, 12, 10, 5, 8, 15, 11, 0, 7, 2, 14, 13, 4, 3, 1),
        byteArrayOf(2, 6, 8, 4, 0, 12, 15, 1, 9, 3, 10, 14, 7, 5, 11, 13),
        byteArrayOf(0, 2, 10, 6, 12, 8, 11, 7, 3, 4, 13, 9, 5, 15, 1, 14),
        byteArrayOf(5, 14, 7, 11, 6, 2, 9, 10, 4, 3, 1, 0, 8, 15, 12, 13),
        byteArrayOf(7, 11, 1, 12, 9, 4, 6, 0, 5, 10, 2, 8, 14, 15, 3, 13),
        byteArrayOf(9, 10, 3, 0, 14, 2, 7, 6, 8, 11, 4, 1, 12, 15, 5, 13),
        byteArrayOf(2, 9, 4, 1, 6, 11, 13, 8, 10, 14, 12, 15, 5, 3, 7, 0),
        byteArrayOf(1, 6, 4, 8, 3, 14, 13, 9, 15, 12, 2, 0, 5, 11, 7, 10),
        byteArrayOf(5, 2, 9, 8, 1, 12, 10, 0, 4, 7, 6, 11, 3, 15, 14, 13),
        byteArrayOf(8, 11, 0, 9, 5, 2, 10, 6, 4, 15, 3, 7, 12, 14, 1, 13),
        byteArrayOf(5, 14, 8, 11, 9, 4, 15, 1, 7, 3, 10, 12, 6, 13, 0, 2),
        byteArrayOf(2, 5, 3, 4, 9, 15, 8, 11, 6, 10, 0, 14, 12, 7, 13, 1),
        byteArrayOf(14, 15, 8, 6, 2, 10, 0, 1, 3, 13, 5, 11, 7, 9, 4, 12),
        byteArrayOf(15, 6, 0, 8, 7, 13, 12, 9, 10, 14, 3, 4, 5, 11, 1, 2)
    )

    fun transform(i: Int, bArr: ByteArray): ByteArray? {
        return try {
            val bArr2 = ByteArray(bArr.size)
            val bArr3 = mConfigEnCode
            if (bArr3.size == LEVEL1 && i == Type_Encode) {
                transformInner(bArr, bArr2, bArr3)
            } else {
                val bArr4 = mConfigDeCode
                if (bArr4.size == LEVEL1 && i == Type_Decode) {
                    transformInner(bArr, bArr2, bArr4)
                } else {
                    println("transform error!")
                }
            }
            bArr2
        } catch (th: Throwable) {
            println("encode error!$th")
            null
        }
    }

    private fun transformInner(bArr: ByteArray, bArr2: ByteArray, bArr3: Array<ByteArray>) {
        var i = 0
        while (i < bArr.size) {
            val i2 = i * Type_Decode
            bArr2[i] = (bArr3[(i2 + Type_Encode) % LEVEL1][(bArr[i].toInt() and 15).toByte()
                .toInt()].toInt() or (bArr3[i2 % LEVEL1][(bArr[i].toInt() shr 4 and 15).toByte()
                .toInt()].toInt() shl 4)).toByte()
            i += Type_Encode
        }
    }
}