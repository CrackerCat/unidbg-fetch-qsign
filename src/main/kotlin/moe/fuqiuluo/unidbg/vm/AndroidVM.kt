package moe.fuqiuluo.unidbg.vm

import com.github.unidbg.arm.backend.DynarmicFactory
import com.github.unidbg.arm.backend.HypervisorFactory
import com.github.unidbg.arm.backend.KvmFactory
import com.github.unidbg.linux.LinuxModule
import com.github.unidbg.linux.LinuxSymbol
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.dvm.DalvikModule
import com.github.unidbg.linux.android.dvm.DvmClass
import com.github.unidbg.virtualmodule.android.AndroidModule
import debug
import java.io.Closeable
import java.io.File
import java.util.Arrays

open class AndroidVM(packageName: String): Closeable {
    internal val emulator = AndroidEmulatorBuilder
        .for64Bit()
        .setProcessName(packageName)
        .addBackendFactory(DynarmicFactory(true))
        .addBackendFactory(KvmFactory(true))
        // 修复Linux arm设备，不支持的问题
        //.addBackendFactory(HypervisorFactory(true))
        .build()!!
    protected val memory = emulator.memory!!
    internal val vm = emulator.createDalvikVM()!!

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

    fun findClass(name: String, vararg interfaces: DvmClass): DvmClass {
        return vm.resolveClass(name, *interfaces)
    }

    override fun close() {
        this.emulator.close()
    }
}