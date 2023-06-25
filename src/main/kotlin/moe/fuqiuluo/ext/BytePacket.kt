package moe.fuqiuluo.ext

import com.tencent.crypt.Crypt
import io.ktor.utils.io.core.*
import moe.fuqiuluo.utils.BytesUtil
import kotlin.text.Charsets.UTF_8
import kotlin.text.toByteArray

inline fun newBuilder() = BytePacketBuilder()

fun BytePacketBuilder.writeBytes(bytes: ByteArray) = this.writeFully(bytes)

fun BytePacketBuilder.toByteArray(): ByteArray = use { it.build().readBytes() }

fun ByteReadPacket.toByteArray(): ByteArray = use { it.readBytes() }

/**
 * 补充功能代码
 * @receiver BytePacketBuilder
 * @param packet BytePacketBuilder
 */
fun BytePacketBuilder.writePacket(packet: BytePacketBuilder) = this.writePacket(packet.build())

/**
 * 写布尔型
 * @receiver BytePacketBuilder
 * @param z Boolean
 */
fun BytePacketBuilder.writeBoolean(z: Boolean) = this.writeByte(if (z) 1 else 0)

/**
 * 自动转换类型
 * @receiver BytePacketBuilder
 * @param i Int
 */
fun BytePacketBuilder.writeShort(i: Int) = this.writeShort(i.toShort())

fun BytePacketBuilder.writeLongToBuf32(v: Long) {
    this.writeBytes(BytesUtil.int64ToBuf32(v))
}

fun BytePacketBuilder.writeStringWithIntLen(str: String) {
    writeBytesWithIntLen(str.toByteArray())
}

fun BytePacketBuilder.writeStringWithShortLen(str: String) {
    writeBytesWithShortLen(str.toByteArray())
}

fun BytePacketBuilder.writeBytesWithIntLen(bytes: ByteArray) {
    writeInt(bytes.size)
    writeBytes(bytes)
}

fun BytePacketBuilder.writeBytesWithShortLen(bytes: ByteArray) {
    check(bytes.size <= Short.MAX_VALUE) { "byteArray length is too long" }
    writeShort(bytes.size.toShort())
    writeBytes(bytes)
}

inline fun BytePacketBuilder.writeBlockWithIntLen(len : (Int) -> Int = { it }, block: BytePacketBuilder.() -> Unit) {
    val builder = newBuilder()
    builder.block()
    this.writeInt(len(builder.size))
    this.writePacket(builder)
    builder.close()
}

inline fun BytePacketBuilder.writeBlockWithShortLen(len : (Int) -> Int = { it }, block: BytePacketBuilder.() -> Unit) {
    val builder = newBuilder()
    builder.block()
    this.writeShort(len(builder.size))
    this.writePacket(builder)
    builder.close()
}


inline fun BytePacketBuilder.writeTeaEncrypt(key: ByteArray, block: BytePacketBuilder.() -> Unit) {
    val body = newBuilder()
    body.block()
    this.writeBytes(Crypt().encrypt(body.toByteArray(), key))
    body.close()
}

fun BytePacketBuilder.writeString(str: String) {
    this.writeBytes(str.toByteArray(UTF_8))
}

fun BytePacketBuilder.writeHex(uHex: String) {
    writeBytes(uHex.hex2ByteArray())
}

fun ByteReadPacket.readString(length: Int) = readBytes(length).decodeToString()

fun ByteArray.toByteReadPacket() = ByteReadPacket(this)

inline fun ByteArray.reader(block: ByteReadPacket.() -> Unit) {
    this.toByteReadPacket().use(block)
}

fun ByteReadPacket.readByteReadPacket(length: Int): ByteReadPacket {
    return readBytes(length).toByteReadPacket()
}