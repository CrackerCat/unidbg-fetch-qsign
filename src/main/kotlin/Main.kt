import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import moe.fuqiuluo.api.configIndex
import moe.fuqiuluo.comm.invoke
import moe.fuqiuluo.ext.toInt
import moe.fuqiuluo.unidbg.QSignWorker
import moe.fuqiuluo.unidbg.pool.FixedWorkPool
import moe.fuqiuluo.unidbg.workerPool
import org.slf4j.LoggerFactory
import java.io.File

var debug: Boolean = false // 调试模式

fun main(args: Array<String>) {
    var port = 0 // API端口
    var workerCount = 0 // UNIDBG实例数量
    var coreLib: File // 核心二进制文件
    val reloadInterval: Long = 40 // 实例重载间隔（分钟）

    args().also {
        port = it["port", "Lack of server.port."]
            .toInt(1 .. 65535) { "Port is out of range." }
        workerCount = it["count", "Lack of workerCount(count)."]
            .toInt(1 .. 100) { "workerCount is out of range." }
        coreLib = File(it["library", "Lack of libfekit.so path."])
        if (!coreLib.exists() || coreLib.isDirectory) {
            error("libfekit.so file is illegal.")
        }
        debug = "debug" in it
    }

    workerPool = FixedWorkPool(workerCount, {
        LoggerFactory.getLogger(Main::class.java)
            .info("Try to construct QSignWorker.")
        QSignWorker(it, coreLib).use { init() }
    }, reloadInterval)

    embeddedServer(Netty, port = port, module = Application::init)
        .start(wait = true)
}

fun Application.init() {
    routing {
        configIndex()
    }
}