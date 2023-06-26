package com.tencent.mobileqq.dt

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object Dtn {
    fun initContext(vm: QSecVM, context: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/dt/Dtn", unique = true)
            .callJniMethod(vm.emulator, "initContext(Landroid/content/Context;)V", context)
    }

    fun initLog(vm: QSecVM, logger: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/dt/Dtn", unique = true)
            .callJniMethod(vm.emulator, "initLog(Lcom/tencent/mobileqq/fe/IFEKitLog;)V", logger)
    }

    fun initUin(vm: QSecVM, uin: String) {
        vm.newInstance("com/tencent/mobileqq/dt/Dtn", unique = true)
            .callJniMethod(vm.emulator, "initUin(Ljava/lang/String;)V", uin)
    }
}