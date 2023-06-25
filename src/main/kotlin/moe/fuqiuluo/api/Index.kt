package moe.fuqiuluo.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configIndex() {
    get("/") {
        call.respondText("Just try me!")
    }
}