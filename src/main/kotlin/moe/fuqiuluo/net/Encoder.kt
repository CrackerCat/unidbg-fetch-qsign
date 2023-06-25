package moe.fuqiuluo.net

import com.tencent.crypt.Crypt
import io.ktor.utils.io.*
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.utils.BytesUtil
import moe.fuqiuluo.utils.EMPTY_BYTE_ARRAY

suspend fun ByteWriteChannel.encode(ssoPacket: SsoPacket) {
    val bytes = newBuilder().apply {
        writeBlockWithIntLen({ it + 4 }) {
            writeInt(0xA)
            writeByte(0x2)
            writeInt(0 + 4)
            writeBytes(EMPTY_BYTE_ARRAY)
            writeByte(0)
            "0".let {
                writeInt(it.length + 4)
                writeString(it)
            }
            val data = newBuilder().apply {
                writeBlockWithIntLen({ it + 4 }) {
                    writeInt(ssoPacket.seq)
                    writeInt(0x2004786b)
                    writeInt(0x2004786b)
                    writeIntLittleEndian(1)
                    writeInt(0)
                    writeInt(0x300)
                    writeInt(0 + 4)
                    writeBytes(EMPTY_BYTE_ARRAY)
                    ssoPacket.cmd.let {
                        writeInt(it.length + 4)
                        writeString(it)
                    }
                    BytesUtil.randomKey(4).let {
                        writeInt(it.size + 4)
                        writeBytes(it)
                    }
                    "592aa1e9cdef0b6a".let {
                        writeInt(it.length + 4)
                        writeString(it)
                    }
                    "f08b688a5caf7adbb2333b13da3d98ff".hex2ByteArray().let {
                        writeInt(it.size + 4)
                        writeBytes(it)
                    }
                    "||A8.9.63.5e7092bd".let {
                        writeShort(it.length + 2)
                        writeString(it)
                    }
                    //writeInt(4)
                    "a6d47134f4bdecfa18e8f02f10001e515311".let {
                        writeInt(it.length + 4)
                        writeString(it)
                    }
                }
                // =============================================
                writeBlockWithIntLen({ it + 4 }) {
                    writeBytes(ssoPacket.data)
                }
            }.toByteArray()
            writeBytes(Crypt().encrypt(data, DEFAULT_TEA_KEY))
        }
    }.toByteArray()
    this.writeFully(bytes)
}