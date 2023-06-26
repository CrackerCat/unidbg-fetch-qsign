package com.tencent.mobileqq.qsec.qsecurity
class DeepSleepDetector {
    val lock: Any = Any()
    val checkRunnable = CheckRunnable()
    var stopped: Boolean = false

    init {
        Thread(checkRunnable).start()
    }

    fun getCheckResult(): Float {
        return checkRunnable.c()
    }

    inner class CheckRunnable: Runnable {
        var f: Long = 0
        val st = System.currentTimeMillis()

        fun c(): Float {
            val ela = System.currentTimeMillis() - st
            return (ela / 1000.0f) - f
        }

        override fun run() {
            while (!stopped) {
                synchronized(lock) {
                    f++
                    Thread.sleep(1000)
                }
            }
        }
    }
}