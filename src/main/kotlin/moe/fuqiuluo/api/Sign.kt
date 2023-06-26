package moe.fuqiuluo.api

import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.unidbg.pool.work
import moe.fuqiuluo.unidbg.workerPool

fun Routing.configSign() {
    @Serializable
    data class Sign(
        val token: String,
        val extra: String,
        val sign: String
    )

    get("/sign") {
        val uin = fetchGet("uin", err = "lack of uin") ?: return@get
        val qua = fetchGet("qua", err = "lack of qua") ?: return@get
        val cmd = fetchGet("cmd", err = "lack of cmd") ?: return@get
        val seq = (fetchGet("seq", err = "lack of seq") ?: return@get).toInt()
        val buffer = (fetchGet("buffer", err = "lack of buffer") ?: return@get).hex2ByteArray()
        val qimei36 = fetchGet("qimei36")

        lateinit var sign: QQSecuritySign.SignResult

        workerPool.work {
            global["qimei36"] = qimei36
            FEKit.changeUin(this, uin)
            sign = QQSecuritySign.getSign(this, qua, cmd, buffer, seq, uin).value
        }

        call.respond(APIResult(0, "", Sign(
            sign.token.toHexString(),
            sign.extra.toHexString(),
            sign.sign.toHexString()
        )))
    }
}