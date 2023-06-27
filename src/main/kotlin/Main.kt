import com.tencent.mobileqq.fe.FEKit
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import moe.fuqiuluo.api.configEnergy
import moe.fuqiuluo.api.configIndex
import moe.fuqiuluo.api.configSign
import moe.fuqiuluo.comm.invoke
import moe.fuqiuluo.ext.toInt
import moe.fuqiuluo.unidbg.QSecVMWorker
import moe.fuqiuluo.unidbg.pool.FixedWorkPool
import moe.fuqiuluo.unidbg.pool.work
import moe.fuqiuluo.unidbg.workerPool
import org.slf4j.LoggerFactory
import java.io.File

/*
 * 8.9.63:
 * QUA = V1_AND_SQ_8.9.63_4188_HDBM_T
 * version = 6.100.248
 */


var QQ_VERSION = "8.9.63"
var QQ_CODE = "4186"
var QUA = "V1_AND_SQ_8.9.63_4188_HDBM_T"
var CHANNEL_VERSION = "6.100.248"
var ANDROID_ID = ""

private val logger = LoggerFactory.getLogger(Main::class.java)
var debug: Boolean = false // 调试模式

fun main(args: Array<String>) {
    var port = 0 // API端口
    var workerCount = 5 // UNIDBG实例数量
    var coreLibPath: File // 核心二进制文件路径
    val reloadInterval: Long = 40 // 实例重载间隔（分钟）

    args().also {
        port = it["port", "Lack of server.port."]
            .toInt(1 .. 65535) { "Port is out of range." }
        workerCount = it["count", "Lack of workerCount(count)."]
            .toInt(1 .. 100) { "workerCount is out of range." }
        coreLibPath = File(it["library", "Lack of libfekit.so path."])
        if (!coreLibPath.exists() || !coreLibPath.isDirectory) {
            error("libfekit.so file is illegal. Your path must include libfekit.so and libQSec.so!")
        }

        ANDROID_ID = it["android_id", "Lack of android_id"]

        debug = "debug" in it
    }

    logger.info("Unidbg workerCount: $workerCount")
    logger.info("Debug enable: $debug")

    workerPool = FixedWorkPool(workerCount, {
        logger.info("Try to construct QSignWorker.")
        QSecVMWorker(it, coreLibPath).apply { work {
            init()
            FEKit.init(this)
        } }
    }, reloadInterval)

    embeddedServer(Netty, port = port, module = Application::init)
        .start(wait = true)
}

fun Application.init() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        configIndex()
        configEnergy()
        configSign()
    }
}