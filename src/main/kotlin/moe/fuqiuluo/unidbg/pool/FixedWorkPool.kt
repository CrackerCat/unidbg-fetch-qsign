package moe.fuqiuluo.unidbg.pool

import org.scijava.nativelib.NativeLibraryUtil
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import com.github.unidbg.worker.*
import moe.fuqiuluo.unidbg.QSecVM
import moe.fuqiuluo.unidbg.QSecVMWorker
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class FixedWorkPool(
    private var workerCount: Int = 1,
    private val factory: WorkerFactory,
    reloadInternal: Long = 40 // 40分钟后重载
): Runnable, WorkerPool {
    init {
        if (NativeLibraryUtil.getArchitecture() == NativeLibraryUtil.Architecture.OSX_ARM64 && workerCount > 1) {
            workerCount = 1
        }
        if (reloadInternal > 0) {
            val timer = Timer()
            val interval = TimeUnit.MINUTES.toMillis(reloadInternal)
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    this@FixedWorkPool.reload()
                }
            }, interval, interval)
        }
    }

    private var stopped = false
    private var created = AtomicInteger(0)
    private var reloadLock = AtomicBoolean(false)
    private var releaseQueue = LinkedBlockingQueue<Worker>()
    private var workers = LinkedBlockingQueue<Worker>(if(workerCount != 1) workerCount - 1 else 1)
    
    init {
        Thread(this, "WorkerPool: $factory").start()
    }

    fun reload() {
        if (!reloadLock.get()) {
            reloadLock.lazySet(true)
            val tmp = releaseQueue
            val tmp2 = workers
            workers = LinkedBlockingQueue<Worker>(if(workerCount != 1) workerCount - 1 else 1)
            releaseQueue = LinkedBlockingQueue<Worker>()
            created.lazySet(0)
            reloadLock.lazySet(false)
            closeWorkers(tmp)
            closeWorkers(tmp2)
        }
    }

    override fun run() {
        while(!stopped) {
            runCatching {
                if (!reloadLock.get()) {
                    val release = if(this.created.get() >= this.workerCount)
                        this.releaseQueue.poll(10L, TimeUnit.MILLISECONDS)
                    else this.releaseQueue.poll()
                    if (release != null) {
                        this.workers.put(release)
                    } else if (this.created.get() < this.workerCount) {
                        this.workers.put(this.factory.createWorker(this))
                        this.created.getAndIncrement()
                    }
                }
            }
        }
        closeWorkers(this.releaseQueue)
        closeWorkers(this.workers)
    }
    
    override fun <T: Worker> borrow(timeout: Long, unit: TimeUnit): T? {
        if (this.stopped) {
            return null
        }
        return try {
            val release = this.workers.poll(timeout, unit) as? T
            release
        } catch (e: InterruptedException) {
            null
        }
    }
    
    override fun release(worker: Worker) {
        if (this.stopped) {
            worker.destroy()
        } else {
            if (reloadLock.get()) {
                worker.destroy()
            } else if (!this.releaseQueue.offer(worker)) {
                throw IllegalStateException("Release worker failed.")
            }
        }
    }
    
    fun reload(worker: Worker) {
        this.created.decrementAndGet()
        worker.destroy()
    }
    
    private fun closeWorkers(queue: BlockingQueue<Worker>) {
        while(true) {
            val worker = queue.poll()
            if(worker != null) {
                worker.destroy()
            } else {
                return
            }
        }
    }
    
    override fun close() {
        this.stopped = true
        closeWorkers(this.workers)
    }
}

fun <T> FixedWorkPool.work(block: QSecVM.() -> T): T? {
    val worker = borrow<QSecVMWorker>(5000, TimeUnit.MILLISECONDS) ?: return null
    val ret = worker.work(block)
    release(worker)
    return ret
}