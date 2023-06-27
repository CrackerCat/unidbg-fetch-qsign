package com.tencent.mobileqq.channel

import com.github.unidbg.linux.android.dvm.DvmObject
import moe.fuqiuluo.unidbg.QSecVM

object ChannelManager {
    fun setChannelProxy(vm: QSecVM, proxy: DvmObject<*>) {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "setChannelProxy(Lcom/tencent/mobileqq/channel/ChannelProxy;)V", proxy)
    }

    fun initReport(vm: QSecVM, qua: String, version: String, androidOs: String = "12", brand: String = "Redmi", model: String = "23013RK75C",
                   qimei36: String = "", guid: String = "35bf20e2a3e25bf715479d6ab76b146d") {
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            // [ChannelManager.initReport]: V1_AND_SQ_8.9.63_4188_HDBM_T, 6.100.248, 12, HONORLGE-AN10, 022eefeab5f927507337089f100015717619, 35bf20e2a3e25bf715479d6ab76b146d
            .callJniMethod(vm.emulator, "initReport(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                qua, version, androidOs, brand + model, qimei36, guid
            )
    }

    fun onNativeReceive(vm: QSecVM, cmd: String, data: ByteArray, callbackId: Long) {
        while ("onNativeReceive" in vm.global) { }
        vm.global["onNativeReceive"] = true
        vm.newInstance("com/tencent/mobileqq/channel/ChannelManager", unique = true)
            .callJniMethod(vm.emulator, "onNativeReceive(Ljava/lang/String;[BZJ)V",
                cmd, data, true, callbackId)
        vm.global.remove("onNativeReceive")
    }
}