@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.unidbg

import com.github.unidbg.linux.android.dvm.DvmObject
import com.github.unidbg.worker.Worker
import com.github.unidbg.worker.WorkerPool
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import moe.fuqiuluo.net.SimpleClient
import moe.fuqiuluo.unidbg.env.FileResolver
import moe.fuqiuluo.unidbg.env.QSecJni
import moe.fuqiuluo.unidbg.pool.FixedWorkPool
import moe.fuqiuluo.unidbg.vm.AndroidVM
import moe.fuqiuluo.unidbg.vm.GlobalData
import org.slf4j.LoggerFactory
import java.io.File
import javax.security.auth.Destroyable

lateinit var workerPool: FixedWorkPool

class QSecVMWorker(pool: WorkerPool, coreLibPath: File): Worker(pool) {
    private val instance: QSecVM = QSecVM(coreLibPath)

    fun <T> work(block: QSecVM.() -> T): T {
        return block.invoke(instance)
    }

    override fun destroy() {
        instance.destroy()
    }
}

class QSecVM(
    val coreLibPath: File
): Destroyable, AndroidVM("com.tencent.mobileqq") {
    companion object {
        private val logger = LoggerFactory.getLogger(QSecVM::class.java)!!
    }

    private var destroy: Boolean = false
    private var isInit: Boolean = false
    val global = GlobalData()
    private val client = SimpleClient("msfwifi.3g.qq.com", 8080)

    init {
        //QSecModule(emulator, vm).register(memory)
        runCatching {
            val resolver = FileResolver(23, this@QSecVM)
            memory.setLibraryResolver(resolver)
            emulator.syscallHandler.addIOResolver(resolver)
            vm.setJni(QSecJni(this, client, global))
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun init() {
        if (isInit) return
        runCatching {
            GlobalScope.launch {
                client.connect()
                client.initConnection()
            }
            loadLibrary(coreLibPath.resolve("libQSec.so"))
            loadLibrary(coreLibPath.resolve("libfekit.so"))
            this.isInit = true
        }.onFailure {
            logger.error("Failed to init QSign: $it")
            it.printStackTrace()
        }
    }

    fun newInstance(name: String, value: Any? = null, unique: Boolean = false): DvmObject<*> {
        if (unique && name in global) {
            return global[name] as DvmObject<*>
        }
        val obj = findClass(name).newObject(value)
        if (unique) {
            global[name] = obj
        }
        return obj
    }

    override fun isDestroyed(): Boolean = destroy

    override fun destroy() {
        if (isDestroyed) return
        this.destroy = true
        this.client.close()
        this.close()
    }
}