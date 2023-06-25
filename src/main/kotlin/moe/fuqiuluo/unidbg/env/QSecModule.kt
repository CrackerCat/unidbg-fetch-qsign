package moe.fuqiuluo.unidbg.env

import com.github.unidbg.Emulator
import com.github.unidbg.linux.android.dvm.AbstractJni
import com.github.unidbg.linux.android.dvm.VM
import com.github.unidbg.memory.SvcMemory
import com.github.unidbg.pointer.UnidbgPointer
import com.github.unidbg.virtualmodule.VirtualModule

class QSecModule(
    private val emulator: Emulator<*>, private val vm: VM
): VirtualModule<VM>(emulator, vm, "libQSec.so") {
    override fun onInitialize(emulator: Emulator<*>, vm: VM, symbols: Map<String, UnidbgPointer>) {
        val is64Bit: Boolean = emulator.is64Bit
        val svcMemory: SvcMemory = emulator.svcMemory
    }
}