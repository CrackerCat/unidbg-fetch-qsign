@file:OptIn(DelicateCoroutinesApi::class)

package moe.fuqiuluo.net

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

interface OnPacketListener {
    fun onReceive(from: FromService?)
}

class Handler(
    val hash: Int,
    private val handlerCenter: PacketHandler,
) {
    private val reentrantLock = MutexLock()
    private var source: FromService? = null
    private var isCallComplete = false
    private var dataListener: OnPacketListener? = null

    fun await(timeout: Long = 1000L * 3): FromService? {
        if (isCallComplete) return null
        reentrantLock.lock()
        return if (reentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
            source
        } else {
            unregister()
            return null
        }
    }

    fun async(timeout: Long = 3 * 1000L, dataListener: OnPacketListener?) {
        if (dataListener == null) {
            unregister()
            return
        }
        this.dataListener = dataListener
        reentrantLock.lock()
        GlobalScope.launch(Dispatchers.IO) {
            if (!reentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                unregister()
                dataListener.onReceive(null)
            }
        }
    }

    fun submitPacket(from: FromService) {
        this.source = from
        this.isCallComplete = true
        unregister()
        reentrantLock.unlock()
        this.dataListener?.onReceive(from)
    }

    private fun unregister() {
        this.isCallComplete = true
        handlerCenter.unregister(hash)
    }
}

open class PacketHandler {
    private val handlers = ConcurrentLinkedQueue<Handler>()

    fun register(cmd: String, seq: Int): Handler? {
        return this.register((cmd + seq).hashCode())
    }

    fun register(hash: Int): Handler {
        return Handler(hash, this).apply {
            handlers.add(this)
        }
    }

    fun unregister(hash: Int) {
        handlers.removeIf { it.hash == hash }
    }

    operator fun invoke(from: FromService) {
        val targetHash = (from.commandName + from.seq).hashCode()
        handlers.forEach {
            if (it.hash == targetHash) {
                it.submitPacket(from)
            }
        }
    }
}