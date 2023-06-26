package moe.fuqiuluo.api

import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import moe.fuqiuluo.ext.fetchGet
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.unidbg.pool.work
import moe.fuqiuluo.unidbg.workerPool

fun Routing.configEnergy() {
    get("/energy") {
        val data = fetchGet("data", err = "lack of data") ?: return@get
        val salt = (fetchGet("salt", err = "lack of salt") ?: return@get).hex2ByteArray()
        lateinit var sign: ByteArray
        workerPool.work {
            sign = Dandelion.energy(this, data, salt)
        }
        call.respond(APIResult(0, "success", sign.toHexString()))
    }


}