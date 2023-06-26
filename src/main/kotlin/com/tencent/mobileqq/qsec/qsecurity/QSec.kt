package com.tencent.mobileqq.qsec.qsecurity

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object QSec {
    fun doSomething(vm: QSecVM, context: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/qsec/qsecurity/QSec", unique = true)
            .callJniMethodInt(vm.emulator, "doSomething(Landroid/content/Context;I)I", context, 1)
    }
}