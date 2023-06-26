@file:OptIn(ExperimentalSerializationApi::class)

package moe.fuqiuluo.net

import com.tencent.crypt.Crypt
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.writeIntLittleEndian
import kotlinx.serialization.ExperimentalSerializationApi
import moe.fuqiuluo.ext.*
import moe.fuqiuluo.utils.BytesUtil
import moe.fuqiuluo.utils.EMPTY_BYTE_ARRAY
import kotlinx.serialization.protobuf.ProtoNumber
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

private val session = BytesUtil.randomKey(4)


class SSOReserveField {
    @Serializable
    data class SsoTrpcResponse(
        @ProtoNumber(1) var ret: Int = Int.MIN_VALUE,
        @ProtoNumber(2) var func_ret: Int = Int.MIN_VALUE,
        @ProtoNumber(3) var error_msg: ByteArray = EMPTY_BYTE_ARRAY,
    )

    @Serializable
    data class SsoSecureInfo(
        @ProtoNumber(1) var sec_sig: ByteArray = EMPTY_BYTE_ARRAY,
        @ProtoNumber(2) var sec_device_token: ByteArray = EMPTY_BYTE_ARRAY,
        @ProtoNumber(3) var sec_extra: ByteArray = EMPTY_BYTE_ARRAY,
    )

    @Serializable
    data class SsoMapEntry(
        @ProtoNumber(1) var key: String = "",
        @ProtoNumber(2) var value: ByteArray = EMPTY_BYTE_ARRAY,
    )

    @Serializable
    data class ReserveFields(
        @ProtoNumber(8) var client_ipcookie: ByteArray = EMPTY_BYTE_ARRAY,
        @ProtoNumber(9) var flag: Int = Int.MIN_VALUE,
        @ProtoNumber(10) var env_id: Int = Int.MIN_VALUE,
        @ProtoNumber(11) var locale_id: Int = Int.MIN_VALUE,
        @ProtoNumber(12) var qimei: ByteArray = EMPTY_BYTE_ARRAY,
        @ProtoNumber(13) var env: String = "",
        @ProtoNumber(14) var newconn_flag: Int = Int.MIN_VALUE,
        @ProtoNumber(15) var trace_parent: String = "",
        @ProtoNumber(16) var uid: String = "uid",
        @ProtoNumber(18) var imsi: Int = Int.MIN_VALUE,
        @ProtoNumber(19) var network_type: Int = Int.MIN_VALUE,
        @ProtoNumber(20) var ip_stack_type: Int = Int.MIN_VALUE,
        @ProtoNumber(21) var message_type: Int = Int.MIN_VALUE,
        @ProtoNumber(22) var trpc_rsp: SsoTrpcResponse? = null,
        @ProtoNumber(23) var trans_info: MutableList<SsoMapEntry> = mutableListOf(),
        @ProtoNumber(24) var sec_info: SsoSecureInfo? = null,
        @ProtoNumber(25) var sec_sig_flag: Int = Int.MIN_VALUE,
        @ProtoNumber(26) var nt_core_version: Int = Int.MIN_VALUE,
        @ProtoNumber(27) var sso_route_cost: Int = Int.MIN_VALUE,
        @ProtoNumber(28) var sso_ip_origin: Int = Int.MIN_VALUE,
        @ProtoNumber(30) var presure_token: ByteArray = EMPTY_BYTE_ARRAY,
    )
}

private fun buildData(ssoPacket: SsoPacket) = newBuilder().apply {
    writeBlockWithIntLen({ it + 4 }) {
        this.writeInt(ssoPacket.seq)
        this.writeInt(0x20047989)
        this.writeInt(0x20047989)
        this.writeIntLittleEndian(1)
        this.writeInt(0)
        this.writeInt(0x300)
        this.writeInt(0 + 4)
        this.writeBytes(EMPTY_BYTE_ARRAY)
        ssoPacket.cmd.let {
            this.writeInt(it.length + 4)
            this.writeString(it)
        }
        session.let {
            this.writeInt(it.size + 4)
            this.writeBytes(it)
        }
        "592aa1e9cdef0b6a".let {
            this.writeInt(it.length + 4)
            this.writeString(it)
        }
        EMPTY_BYTE_ARRAY.let {
            this.writeInt(it.size + 4)
            this.writeBytes(it)
        }
        "||A8.9.63.c5ecda36".let {
            this.writeShort(it.length + 2)
            this.writeString(it)
        }
        ProtoBuf.encodeToByteArray(SSOReserveField.ReserveFields().also { reserve ->
            reserve.flag = 1
            reserve.locale_id = 2052
            reserve.qimei = "022eefeab5f927507337089f100015717619".toByteArray()
            reserve.newconn_flag = 0
            reserve.uid = ""
            reserve.imsi = 0
            reserve.network_type = 1
            reserve.ip_stack_type = 1
            reserve.message_type = 8
            reserve.trans_info.add(SSOReserveField.SsoMapEntry(
                "client_conn_seq", (System.currentTimeMillis() / 1000).toString().toByteArray()
            ))
            reserve.nt_core_version = 100
            reserve.sso_ip_origin = 2
        }).let {
            this.writeInt(it.size + 4)
            this.writeBytes(it)
        }
    }
    this.writeBlockWithIntLen({ it + 4 }) {
        this.writeBytes(ssoPacket.data)
    }
}.toByteArray()

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
            val data = buildData(ssoPacket)
            this.writeBytes(Crypt().encrypt(data, DEFAULT_TEA_KEY))
        }
    }.toByteArray()
    //println("Will send ${bytes.toHexString()}")
    this.writeFully(bytes)
    this.flush()
}