package com.tencent.mobileqq.fe

import CHANNEL_VERSION
import QUA
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.dt.Dtn
import com.tencent.mobileqq.qsec.qsecurity.QSec
import com.tencent.mobileqq.sign.QQSecuritySign
import moe.fuqiuluo.unidbg.QSecVM

object FEKit {
    fun init(vm: QSecVM) {
        if ("fekit" in vm.global) return
        QQSecuritySign.initSafeMode(vm, false)
        QQSecuritySign.dispatchEvent(vm, "Kicked", "0")

        val context = vm.newInstance("android/content/Context", unique = true)
        Dtn.initContext(vm, context)
        Dtn.initLog(vm, vm.newInstance("com/tencent/mobileqq/fe/IFEKitLog"))
        Dtn.initUin(vm, "0")

        ChannelManager.setChannelProxy(vm, vm.newInstance("com/tencent/mobileqq/channel/ChannelProxy"))
        ChannelManager.initReport(vm, QUA, CHANNEL_VERSION)

        QQSecuritySign.requestToken(vm)

        QSec.doSomething(vm, context)
    }

    fun changeUin(vm: QSecVM, uin: String) {
        vm.global["uin"] = uin
        Dtn.initUin(vm, uin)
        QQSecuritySign.dispatchEvent(vm, "Kicked", uin)
    }
}