@file:Suppress("LocalVariableName")
package com.tencent.mobileqq.qsec.qsecdandelionsdk

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object Dandelion {
    @JvmStatic
    fun energy(vm: QSecVM, data: String, salt: ByteArray): ByteArray {
        val Dandelion = vm.newInstance("com.tencent.mobileqq.qsec.qsecdandelionsdk/Dandelion", unique = true)
        return Dandelion.callJniMethodObject<DvmObject<*>>(vm.emulator,
            "energy(Ljava/lang/Object;Ljava/lang/Object;)[B", data, salt).value as ByteArray
    }
}