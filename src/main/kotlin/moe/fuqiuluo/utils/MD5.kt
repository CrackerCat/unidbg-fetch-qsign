package moe.fuqiuluo.utils

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.UnsupportedEncodingException

class MD5 {
    private val buffer = ByteArray(64)
    private val count = LongArray(2)
    val digest = ByteArray(16)
    private val state = LongArray(4)
    fun getMD5(bArr: ByteArray): ByteArray {
        md5Init()
        md5Update(ByteArrayInputStream(bArr), bArr.size.toLong())
        md5Final()
        return digest
    }

    fun getMD5(inputStream: InputStream, len: Long): ByteArray {
        md5Init()
        if (!md5Update(inputStream, len)) {
            return ByteArray(16)
        }
        md5Final()
        return digest
    }

    init {
        md5Init()
    }

    fun md5Init() {
        count[0] = 0
        count[1] = 0
        state[0] = 0x67452301
        state[1] = 4023233417L
        state[2] = 2562383102L
        state[3] = 0x10325476
    }

    private fun F(j: Long, j2: Long, j3: Long): Long {
        return j and j2 or (j.inv() and j3)
    }

    private fun G(j: Long, j2: Long, j3: Long): Long {
        return j and j3 or (j3.inv() and j2)
    }

    private fun H(j: Long, j2: Long, j3: Long): Long {
        return j xor j2 xor j3
    }

    private fun I(j: Long, j2: Long, j3: Long): Long {
        return j3.inv() or j xor j2
    }

    private fun FF(j: Long, j2: Long, j3: Long, j4: Long, j5: Long, j6: Long, j7: Long): Long {
        val F = F(j2, j3, j4) + j5 + j7 + j
        return ((F.toInt() ushr (32 - j6).toInt()).toLong() or (F.toInt().toLong() shl j6.toInt())) + j2
    }

    private fun GG(j: Long, j2: Long, j3: Long, j4: Long, j5: Long, j6: Long, j7: Long): Long {
        val G = G(j2, j3, j4) + j5 + j7 + j
        return ((G.toInt() ushr (32 - j6).toInt()).toLong() or (G.toInt().toLong() shl j6.toInt())) + j2
    }

    private fun HH(j: Long, j2: Long, j3: Long, j4: Long, j5: Long, j6: Long, j7: Long): Long {
        val H = H(j2, j3, j4) + j5 + j7 + j
        return ((H.toInt() ushr (32 - j6).toInt()).toLong() or (H.toInt().toLong() shl j6.toInt())) + j2
    }

    private fun II(j: Long, j2: Long, j3: Long, j4: Long, j5: Long, j6: Long, j7: Long): Long {
        val I = I(j2, j3, j4) + j5 + j7 + j
        return ((I.toInt() ushr (32 - j6).toInt()).toLong() or (I.toInt().toLong() shl j6.toInt())) + j2
    }

    fun md5Update(inputStream: InputStream, j: Long): Boolean {
        var i: Int
        val bArr = ByteArray(64)
        var i2 = (count[0] ushr 3).toInt() and 63
        val jArr = count
        val j2 = jArr[0] + (j shl 3)
        jArr[0] = j2
        if (j2 < j shl 3) {
            val jArr2 = count
            jArr2[1] = jArr2[1] + 1
        }
        val jArr3 = count
        jArr3[1] = jArr3[1] + (j ushr 29)
        val i3 = 64 - i2
        if (j >= i3.toLong()) {
            val bArr2 = ByteArray(i3)
            try {
                inputStream.read(bArr2, 0, i3)
                md5Memcpy(buffer, bArr2, i2, 0, i3)
                md5Transform(buffer)
                i = i3
                while ((i + 63).toLong() < j) {
                    i += try {
                        inputStream.read(bArr)
                        md5Transform(bArr)
                        64
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return false
                    }
                }
                i2 = 0
            } catch (e2: Exception) {
                e2.printStackTrace()
                return false
            }
        } else {
            i = 0
        }
        val bArr3 = ByteArray((j - i.toLong()).toInt())
        return try {
            inputStream.read(bArr3)
            md5Memcpy(buffer, bArr3, i2, 0, bArr3.size)
            true
        } catch (e3: Exception) {
            e3.printStackTrace()
            false
        }
    }

    private fun md5Update(bArr: ByteArray, i: Int) {
        var i2 = 0
        val bArr2 = ByteArray(64)
        var i3 = (count[0] ushr 3).toInt() and 63
        val jArr = count
        val j = jArr[0] + (i.toLong() shl 3)
        jArr[0] = j
        if (j < i.toLong() shl 3) {
            val jArr2 = count
            jArr2[1] = jArr2[1] + 1
        }
        val jArr3 = count
        jArr3[1] = jArr3[1] + (i ushr 29).toLong()
        var i4 = 64 - i3
        if (i >= i4) {
            md5Memcpy(buffer, bArr, i3, 0, i4)
            md5Transform(buffer)
            while (i4 + 63 < i) {
                md5Memcpy(bArr2, bArr, 0, i4, 64)
                md5Transform(bArr2)
                i4 += 64
            }
            i3 = 0
            i2 = i4
        }
        md5Memcpy(buffer, bArr, i3, i2, i - i2)
    }

    fun md5Final() {
        val bArr = ByteArray(8)
        Encode(bArr, count, 8)
        val i = (count[0] ushr 3).toInt() and 63
        md5Update(PADDING, if (i < 56) 56 - i else 120 - i)
        md5Update(bArr, 8)
        Encode(digest, state, 16)
    }

    private fun md5Memcpy(bArr: ByteArray, bArr2: ByteArray, i: Int, i2: Int, i3: Int) {
        if (i3 >= 0) System.arraycopy(bArr2, i2, bArr, i, i3)
    }

    private fun md5Transform(bArr: ByteArray) {
        val j = state[0]
        val j2 = state[1]
        val j3 = state[2]
        val j4 = state[3]
        val jArr = LongArray(16)
        Decode(jArr, bArr, 64)
        val FF = FF(j, j2, j3, j4, jArr[0], 7, 3614090360L)
        val FF2 = FF(j4, FF, j2, j3, jArr[1], 12, 3905402710L)
        val FF3 = FF(j3, FF2, FF, j2, jArr[2], 17, 0x242070db)
        val FF4 = FF(j2, FF3, FF2, FF, jArr[3], 22, 3250441966L)
        val FF5 = FF(FF, FF4, FF3, FF2, jArr[4], 7, 4118548399L)
        val FF6 = FF(FF2, FF5, FF4, FF3, jArr[5], 12, 0x4787c62a)
        val FF7 = FF(FF3, FF6, FF5, FF4, jArr[6], 17, 2821735955L)
        val FF8 = FF(FF4, FF7, FF6, FF5, jArr[7], 22, 4249261313L)
        val FF9 = FF(FF5, FF8, FF7, FF6, jArr[8], 7, 0x698098d8)
        val FF10 = FF(FF6, FF9, FF8, FF7, jArr[9], 12, 2336552879L)
        val FF11 = FF(FF7, FF10, FF9, FF8, jArr[10], 17, 4294925233L)
        val FF12 = FF(FF8, FF11, FF10, FF9, jArr[11], 22, 2304563134L)
        val FF13 = FF(FF9, FF12, FF11, FF10, jArr[12], 7, 0x6b901122)
        val FF14 = FF(FF10, FF13, FF12, FF11, jArr[13], 12, 4254626195L)
        val FF15 = FF(FF11, FF14, FF13, FF12, jArr[14], 17, 2792965006L)
        val FF16 = FF(FF12, FF15, FF14, FF13, jArr[15], 22, 0x49b40821)
        val GG = GG(FF13, FF16, FF15, FF14, jArr[1], 5, 4129170786L)
        val GG2 = GG(FF14, GG, FF16, FF15, jArr[6], 9, 3225465664L)
        val GG3 = GG(FF15, GG2, GG, FF16, jArr[11], 14, 0x265e5a51)
        val GG4 = GG(FF16, GG3, GG2, GG, jArr[0], 20, 3921069994L)
        val GG5 = GG(GG, GG4, GG3, GG2, jArr[5], 5, 3593408605L)
        val GG6 = GG(GG2, GG5, GG4, GG3, jArr[10], 9, 0x02441453)
        val GG7 = GG(GG3, GG6, GG5, GG4, jArr[15], 14, 3634488961L)
        val GG8 = GG(GG4, GG7, GG6, GG5, jArr[4], 20, 3889429448L)
        val GG9 = GG(GG5, GG8, GG7, GG6, jArr[9], 5, 0x21e1cde6)
        val GG10 = GG(GG6, GG9, GG8, GG7, jArr[14], 9, 3275163606L)
        val GG11 = GG(GG7, GG10, GG9, GG8, jArr[3], 14, 4107603335L)
        val GG12 = GG(GG8, GG11, GG10, GG9, jArr[8], 20, 0x455a14ed)
        val GG13 = GG(GG9, GG12, GG11, GG10, jArr[13], 5, 2850285829L)
        val GG14 = GG(GG10, GG13, GG12, GG11, jArr[2], 9, 4243563512L)
        val GG15 = GG(GG11, GG14, GG13, GG12, jArr[7], 14, 0x676f02d9)
        val GG16 = GG(GG12, GG15, GG14, GG13, jArr[12], 20, 2368359562L)
        val HH = HH(GG13, GG16, GG15, GG14, jArr[5], 4, 4294588738L)
        val HH2 = HH(GG14, HH, GG16, GG15, jArr[8], 11, 2272392833L)
        val HH3 = HH(GG15, HH2, HH, GG16, jArr[11], 16, 0x6d9d6122)
        val HH4 = HH(GG16, HH3, HH2, HH, jArr[14], 23, 4259657740L)
        val HH5 = HH(HH, HH4, HH3, HH2, jArr[1], 4, 2763975236L)
        val HH6 = HH(HH2, HH5, HH4, HH3, jArr[4], 11, 0x4bdecfa9)
        val HH7 = HH(HH3, HH6, HH5, HH4, jArr[7], 16, 4139469664L)
        val HH8 = HH(HH4, HH7, HH6, HH5, jArr[10], 23, 3200236656L)
        val HH9 = HH(HH5, HH8, HH7, HH6, jArr[13], 4, 0x289b7ec6)
        val HH10 = HH(HH6, HH9, HH8, HH7, jArr[0], 11, 3936430074L)
        val HH11 = HH(HH7, HH10, HH9, HH8, jArr[3], 16, 3572445317L)
        val HH12 = HH(HH8, HH11, HH10, HH9, jArr[6], 23, 0x04881d05)
        val HH13 = HH(HH9, HH12, HH11, HH10, jArr[9], 4, 3654602809L)
        val HH14 = HH(HH10, HH13, HH12, HH11, jArr[12], 11, 3873151461L)
        val HH15 = HH(HH11, HH14, HH13, HH12, jArr[15], 16, 0x1fa27cf8)
        val HH16 = HH(HH12, HH15, HH14, HH13, jArr[2], 23, 3299628645L)
        val II = II(HH13, HH16, HH15, HH14, jArr[0], 6, 4096336452L)
        val II2 = II(HH14, II, HH16, HH15, jArr[7], 10, 0x432aff97)
        val II3 = II(HH15, II2, II, HH16, jArr[14], 15, 2878612391L)
        val II4 = II(HH16, II3, II2, II, jArr[5], 21, 4237533241L)
        val II5 = II(II, II4, II3, II2, jArr[12], 6, 0x655b59c3)
        val II6 = II(II2, II5, II4, II3, jArr[3], 10, 2399980690L)
        val II7 = II(II3, II6, II5, II4, jArr[10], 15, 4293915773L)
        val II8 = II(II4, II7, II6, II5, jArr[1], 21, 2240044497L)
        val II9 = II(II5, II8, II7, II6, jArr[8], 6, 0x6fa87e4f)
        val II10 = II(II6, II9, II8, II7, jArr[15], 10, 4264355552L)
        val II11 = II(II7, II10, II9, II8, jArr[6], 15, 2734768916L)
        val II12 = II(II8, II11, II10, II9, jArr[13], 21, 0x4e0811a1)
        val II13 = II(II9, II12, II11, II10, jArr[4], 6, 4149444226L)
        val II14 = II(II10, II13, II12, II11, jArr[11], 10, 3174756917L)
        val II15 = II(II11, II14, II13, II12, jArr[2], 15, 0x2ad7d2bb)
        val II16 = II(II12, II15, II14, II13, jArr[9], 21, 3951481745L)
        val jArr2 = state
        jArr2[0] = jArr2[0] + II13
        val jArr3 = state
        jArr3[1] = II16 + jArr3[1]
        val jArr4 = state
        jArr4[2] = jArr4[2] + II15
        val jArr5 = state
        jArr5[3] = jArr5[3] + II14
    }

    private fun Encode(bArr: ByteArray, jArr: LongArray, i: Int) {
        var i2 = 0
        var i3 = 0
        while (i3 < i) {
            bArr[i3] = (jArr[i2] and 255L).toInt().toByte()
            bArr[i3 + 1] = (jArr[i2] ushr 8 and 255L).toInt().toByte()
            bArr[i3 + 2] = (jArr[i2] ushr 16 and 255L).toInt().toByte()
            bArr[i3 + 3] = (jArr[i2] ushr 24 and 255L).toInt().toByte()
            i2++
            i3 += 4
        }
    }

    private fun Decode(jArr: LongArray, bArr: ByteArray, i: Int) {
        var i2 = 0
        var i3 = 0
        while (i3 < i) {
            jArr[i2] = b2iu(bArr[i3]) or (b2iu(bArr[i3 + 1]) shl 8) or (b2iu(
                bArr[i3 + 2]
            ) shl 16) or (b2iu(bArr[i3 + 3]) shl 24)
            i2++
            i3 += 4
        }
    }

    companion object {
        val PADDING = byteArrayOf(
            Byte.MIN_VALUE,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        )

        fun b2iu(b: Byte): Long {
            return if (b < 0) (b.toInt() and 255).toLong() else b.toLong()
        }

        fun byteHEX(b: Byte): String {
            val cArr = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
            return String(charArrayOf(cArr[b.toInt() ushr 4 and 15], cArr[b.toInt() and 15]))
        }

        fun toMD5Byte(bArr: ByteArray): ByteArray {
            return MD5().getMD5(bArr)
        }

        fun toMD5Byte(str: String): ByteArray {
            val bytes: ByteArray
            bytes = try {
                str.toByteArray(charset("ISO8859_1"))
            } catch (e: UnsupportedEncodingException) {
                str.toByteArray()
            }
            return MD5().getMD5(bytes)
        }

        fun toMD5Byte(inputStream: InputStream, size: Long): ByteArray {
            return MD5().getMD5(inputStream, size)
        }

        fun toMD5(bArr: ByteArray): String {
            val md5 = MD5().getMD5(bArr)
            val str = StringBuilder()
            for (i in 0..15) {
                str.append(byteHEX(md5[i]))
            }
            return str.toString()
        }

        fun toMD5(str: String): String {
            val bytes: ByteArray
            bytes = try {
                str.toByteArray(charset("ISO8859_1"))
            } catch (e: UnsupportedEncodingException) {
                str.toByteArray()
            }
            val md5 = MD5().getMD5(bytes)
            val str2 = StringBuilder()
            for (i in 0..15) {
                str2.append(byteHEX(md5[i]))
            }
            return str2.toString()
        }
    }
}