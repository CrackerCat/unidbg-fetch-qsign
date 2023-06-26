package moe.fuqiuluo.ext

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import moe.fuqiuluo.api.APIResult

suspend fun PipelineContext<Unit, ApplicationCall>.fetchGet(key: String, def: String? = null, err: String? = null): String? {
    val data = call.parameters[key] ?: def
    if (data == null && err != null) {
        call.respond(APIResult(1, err, "failed"))
    }
    return data
}