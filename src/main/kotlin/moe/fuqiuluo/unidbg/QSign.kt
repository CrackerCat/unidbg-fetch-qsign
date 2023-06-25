@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.unidbg

import com.github.unidbg.worker.Worker
import com.github.unidbg.worker.WorkerPool
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.fuqiuluo.net.SimpleClient
import moe.fuqiuluo.unidbg.env.QSecJni
import moe.fuqiuluo.unidbg.env.QSecModule
import moe.fuqiuluo.unidbg.pool.FixedWorkPool
import moe.fuqiuluo.unidbg.vm.AndroidVM
import org.slf4j.LoggerFactory
import java.io.File
import javax.security.auth.Destroyable

lateinit var workerPool: FixedWorkPool

class QSignWorker(pool: WorkerPool, coreLib: File): Worker(pool) {
    private val instance: QSign = QSign(coreLib)

    fun use(block: QSign.() -> Unit): QSignWorker {
        block.invoke(instance)
        return this
    }

    override fun destroy() {
        instance.destroy()
    }
}

class QSign(
    private val coreLib: File
): Destroyable, AndroidVM() {
    companion object {
        private val logger = LoggerFactory.getLogger(QSign::class.java)!!
    }

    private var destroy: Boolean = false
    private val client = SimpleClient("msfwifi.3g.qq.com", 8080)

    init {
        QSecModule(emulator, vm).register(memory)
        vm.setJni(QSecJni())
    }

    fun init() {
        runCatching {
            GlobalScope.launch {
                client.connect()
                client.initConnection()
            }
            loadLibrary(coreLib)
        }.onFailure {
            logger.error("Failed to init QSign: $it")
            it.printStackTrace()
        }
    }

    override fun isDestroyed(): Boolean = destroy

    override fun destroy() {
        if (isDestroyed) return
        this.destroy = true
        this.client.close()
        this.close()
    }
}