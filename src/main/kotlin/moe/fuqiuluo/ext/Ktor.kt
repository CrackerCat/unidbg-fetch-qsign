package moe.fuqiuluo.ext

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<Unit, ApplicationCall>.fetchGet(key: String, def: String? = null): String? {
    return call.parameters[key] ?: def
}