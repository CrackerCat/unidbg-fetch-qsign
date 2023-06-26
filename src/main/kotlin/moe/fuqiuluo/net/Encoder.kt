package moe.fuqiuluo.net

import com.tencent.crypt.Crypt
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.writeIntLittleEndian
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.utils.BytesUtil
import moe.fuqiuluo.utils.EMPTY_BYTE_ARRAY

suspend fun ByteWriteChannel.encode(ssoPacket: SsoPacket) {
    val bytes = newBuilder().also { main ->
        main.writeBlockWithIntLen({ it + 4 }) {
            this.writeInt(0xA)
            this.writeByte(0x2)
            this.writeInt(0 + 4)
            this.writeBytes(EMPTY_BYTE_ARRAY)
            this.writeByte(0)
            ssoPacket.uin.let {
                this.writeInt(it.length + 4)
                this.writeString(it)
            }
            val data = newBuilder().apply {
                writeBlockWithIntLen({ it + 4 }) {
                    this.writeInt(ssoPacket.seq)
                    this.writeInt(0x2004786b)
                    this.writeInt(0x2004786b)
                    this.writeIntLittleEndian(1)
                    this.writeInt(0)
                    this.writeInt(0x300)
                    this.writeInt(0 + 4)
                    this.writeBytes(EMPTY_BYTE_ARRAY)
                    ssoPacket.cmd.let {
                        this.writeInt(it.length + 4)
                        this.writeString(it)
                    }
                    BytesUtil.randomKey(4).let {
                        this.writeInt(it.size + 4)
                        this.writeBytes(it)
                    }
                    "592aa1e9cdef0b6a".let {
                        this.writeInt(it.length + 4)
                        this.writeString(it)
                    }
                    "f08b688a5caf7adbb2333b13da3d98ff".hex2ByteArray().let {
                        this.writeInt(it.size + 4)
                        this.writeBytes(it)
                    }
                    "||A8.9.63.5e7092bd".let {
                        this.writeShort(it.length + 2)
                        this.writeString(it)
                    }
                    //writeInt(4)
                    "a6d47134f4bdecfa18e8f02f10001e515311".let {
                        this.writeInt(it.length + 4)
                        this.writeString(it)
                    }
                }
                // =============================================
                this.writeBlockWithIntLen({ it + 4 }) {
                    this.writeBytes(ssoPacket.data)
                }
            }.toByteArray()
            this.writeBytes(Crypt().encrypt(data, DEFAULT_TEA_KEY))
        }
    }.toByteArray()
    //println("Will send ${bytes.toHexString()}")
    this.writeFully(bytes)
    this.flush()
}