package moe.fuqiuluo.api

import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.unidbg.QSignWorker
import moe.fuqiuluo.unidbg.workerPool
import java.util.concurrent.TimeUnit

fun Routing.configEnergy() {
    get("/energy") {
        val data = fetchGet("data").also { if (it == null) {
            call.respond(APIResult(1, "lack of data", null))
            return@get
        } }!!
        val salt = fetchGet("salt").also { if (it == null) {
            call.respond(APIResult(1, "lack of salt", null))
            return@get
        } }?.hex2ByteArray()!!

        lateinit var sign: ByteArray
        workerPool.borrow<QSignWorker>(5000, TimeUnit.MILLISECONDS)?.work {
            sign = Dandelion.energy(this, data, salt)
        }?.also { workerPool.release(it) }
            ?: call.respond(APIResult(-1, "failed to get qq_sign_vm", null))

        call.respond(APIResult(0, "success", sign.toHexString()))
    }


}