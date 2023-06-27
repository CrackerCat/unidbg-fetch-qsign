package moe.fuqiuluo.net

import com.tencent.crypt.Crypt
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import moe.fuqiuluo.ext.readString
import moe.fuqiuluo.ext.reader
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.utils.EMPTY_BYTE_ARRAY
import moe.fuqiuluo.utils.ZlibUtil
import java.nio.ByteBuffer

val DEFAULT_TEA_KEY = ByteArray(16)

data class FromService(
    val seq: Int, val commandName: String, val body: ByteArray
) {
    //var msgCookie: ByteArray = "02B05B8B".hex2ByteArray()
    var uin: String = ""
    lateinit var msgCookie: ByteArray

    fun handlerName() = "$commandName:$seq"

    internal fun isSSOLoginMerge() = "SSO.LoginMerge" == commandName

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FromService) return false

        if (seq != other.seq) return false
        if (commandName != other.commandName) return false
        if (!body.contentEquals(other.body)) return false
        if (!msgCookie.contentEquals(other.msgCookie)) return false

        return true
    }

    override fun toString(): String {
        return "FromService(uin=$uin, seq=$seq, commandName=$commandName, body= " + body.toHexString() + " )"
    }

    override fun hashCode(): Int {
        var result = seq
        result = 31 * result + commandName.hashCode()
        result = 31 * result + body.contentHashCode()
        result = 31 * result + msgCookie.contentHashCode()
        return result
    }
}


suspend fun ByteReadChannel.decode(length: Int, callback: (FromService) -> Unit) {
    require(length > 0)
    val buf = ByteArray(length).also { this.readFully(it, 0, length) }
    //println("Receive ${buf.array().toHexString()}")
    buf.decodePacket(callback)
}

private data class PacketState(
    var seq: Int = 0,
    var cmd: String = "",
    var sessionId: ByteArray = EMPTY_BYTE_ARRAY,
    var body: ByteArray = EMPTY_BYTE_ARRAY,
    var compressType: Int = 0
)

private fun ByteArray.decodePacket(callback: (FromService) -> Unit) { reader {
    if (readInt() == 20140601) return // MSF PING
    val cipherType = readByte().toInt()
    if (cipherType == 0) return // HEARTBEAT
    discardExact(1)
    val uin = readString(readInt() - 4)
    requireNotNull(Crypt().decrypt(
        ByteArray(remaining.toInt()).apply { readAvailable(this) },
        when(cipherType) {
            2 -> DEFAULT_TEA_KEY
            else -> error("Not support the cipherType: $cipherType")
        }
    )) { "Unable to decrypt packet." }.reader {
        val state = PacketState()
        readBytes(readInt() - 4).decodeHead(state)
        val from = readBytes(readInt() - 4)
            .decodeBody(state)
            .also { it.uin = uin }
        callback.invoke(from)
    }
} }

private fun ByteArray.decodeBody(state: PacketState): FromService {
    when (state.compressType) {
        0, 4 -> this
        1 -> ZlibUtil.unCompress(this)
        else -> error("unknown encode type.")
    }.let {
        val from = FromService(state.seq, state.cmd, it)
        from.msgCookie = state.sessionId
        return from
    }
}

private fun ByteArray.decodeHead(state: PacketState) { reader {
    state.seq = readInt()
    if (readInt() != 0) {
        discardExact(readInt() - 4)
    } else {
        discardExact(4)
    }
    state.cmd = readString(readInt() - 4)
    state.sessionId = readBytes(readInt() - 4)
    state.compressType = readInt()
} }

