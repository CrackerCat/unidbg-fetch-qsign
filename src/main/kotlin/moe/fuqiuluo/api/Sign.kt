package moe.fuqiuluo.api

import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.sign.QQSecuritySign
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.ext.fetchPost
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.unidbg.pool.work
import moe.fuqiuluo.unidbg.workerPool



fun Routing.configSign() {
    @Serializable
    data class Sign(
        val token: String,
        val extra: String,
        val sign: String,
        val o3did: String
    )

    get("/sign") {
        val uin = fetchGet("uin", err = "lack of uin") ?: return@get
        val qua = fetchGet("qua", err = "lack of qua") ?: return@get
        val cmd = fetchGet("cmd", err = "lack of cmd") ?: return@get
        val seq = (fetchGet("seq", err = "lack of seq") ?: return@get).toInt()
        val buffer = (fetchGet("buffer", err = "lack of buffer") ?: return@get).hex2ByteArray()
        val qimei36 = fetchGet("qimei36")

        var o3did = ""
        val sign = workerPool.work {
            global["qimei36"] = qimei36
            FEKit.changeUin(this, uin)
            val sign = QQSecuritySign.getSign(this, qua, cmd, buffer, seq, uin).value
            o3did = global["o3did"] as? String ?: ""
            return@work sign
        }

        if (sign == null) {
            call.respond(APIResult(-1, "The instance is occupied and there are no idle instances", null))
        } else {
            call.respond(APIResult(0, "", Sign(
                sign.token.toHexString(),
                sign.extra.toHexString(),
                sign.sign.toHexString(),
                o3did
            )))
        }
    }

    post("/sign") {
        val parameters = call.receiveParameters()

        val uin = fetchPost(parameters, "uin", err = "lack of uin") ?: return@post
        val qua = fetchPost(parameters, "qua", err = "lack of qua") ?: return@post
        val cmd = fetchPost(parameters, "cmd", err = "lack of cmd") ?: return@post
        val seq = (fetchPost(parameters, "seq", err = "lack of seq") ?: return@post).toInt()
        val buffer = (fetchPost(parameters, "buffer", err = "lack of buffer") ?: return@post).hex2ByteArray()
        val qimei36 = fetchPost(parameters, "qimei36")

        var o3did = ""
        val sign = workerPool.work {
            global["qimei36"] = qimei36
            FEKit.changeUin(this, uin)
            val sign = QQSecuritySign.getSign(this, qua, cmd, buffer, seq, uin).value
            o3did = global["o3did"] as? String ?: ""
            return@work sign
        }

        if (sign == null) {
            call.respond(APIResult(-1, "The instance is occupied and there are no idle instances", null))
        } else {
            call.respond(APIResult(0, "", Sign(
                sign.token.toHexString(),
                sign.extra.toHexString(),
                sign.sign.toHexString(),
                o3did
            )))
        }
    }
}