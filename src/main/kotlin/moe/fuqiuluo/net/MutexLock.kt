package moe.fuqiuluo.net

import java.io.Serializable
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

/**
 * 互斥锁
 * @author 飞翔的企鹅
 * create 2021-05-30 13:18
 */
class MutexLock : Lock, Serializable {
    // 内部类，自定义同步器
    private class Sync : AbstractQueuedSynchronizer() {
        // 是否处于占用状态
        public override fun isHeldExclusively(): Boolean {
            return state == 1
        }

        // 当状态为0的时候获取锁
        public override fun tryAcquire(acquires: Int): Boolean {
            assert(acquires == 1 // Otherwise unused
            )
            if (compareAndSetState(0, 1)) {
                exclusiveOwnerThread = Thread.currentThread()
                return true
            }
            return false
        }

        // 释放锁，将状态设置为0
        override fun tryRelease(releases: Int): Boolean {
            assert(releases == 1 // Otherwise unused
            )
            if (state == 0) throw IllegalMonitorStateException()
            exclusiveOwnerThread = null
            state = 0
            return true
        }

        // 返回一个Condition，每个condition都包含了一个condition队列
        fun newCondition(): Condition {
            return ConditionObject()
        }
    }

    // 仅需要将操作代理到Sync上即可
    private val sync = Sync()

    override fun lock() {
        sync.acquire(1)
    }

    override fun tryLock(): Boolean {
        return sync.tryAcquire(1)
    }

    override fun unlock() {
        sync.release(1)
    }

    override fun newCondition(): Condition {
        return sync.newCondition()
    }

    val isLocked: Boolean
        get() = sync.isHeldExclusively

    fun hasQueuedThreads(): Boolean {
        return sync.hasQueuedThreads()
    }

    override fun lockInterruptibly() {
        sync.acquireInterruptibly(1)
    }

    override fun tryLock(timeout: Long, unit: TimeUnit): Boolean {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout))
    }
}