package moe.fuqiuluo.unidbg.vm

import com.github.unidbg.arm.backend.DynarmicFactory
import com.github.unidbg.arm.backend.HypervisorFactory
import com.github.unidbg.arm.backend.KvmFactory
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.dvm.DalvikModule
import com.github.unidbg.virtualmodule.android.AndroidModule
import debug
import java.io.Closeable
import java.io.File

open class AndroidVM: Closeable {
    protected val emulator = AndroidEmulatorBuilder
        .for64Bit()
        .setProcessName("com.tencent.mobileqq")
        .addBackendFactory(DynarmicFactory(true))
        .addBackendFactory(KvmFactory(true))
        .addBackendFactory(HypervisorFactory(true))
        .build()!!
    protected val memory = emulator.memory!!
    protected val vm = emulator.createDalvikVM()!!

    init {
        vm.setVerbose(debug)
        val syscall = emulator.syscallHandler
        syscall.isVerbose = debug
        syscall.setEnableThreadDispatcher(true)

        AndroidModule(emulator, vm).register(memory)
    }

    fun loadLibrary(soFile: File): DalvikModule {
        val dm = vm.loadLibrary(soFile, false)
        dm.callJNI_OnLoad(emulator)
        return dm
    }

    override fun close() {
        this.emulator.close()
    }
}