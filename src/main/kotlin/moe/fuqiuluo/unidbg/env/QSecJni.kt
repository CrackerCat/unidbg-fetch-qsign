package moe.fuqiuluo.unidbg.env

import com.github.unidbg.linux.android.dvm.AbstractJni
import com.github.unidbg.linux.android.dvm.BaseVM
import com.github.unidbg.linux.android.dvm.DvmObject
import com.github.unidbg.linux.android.dvm.VaList
import moe.fuqiuluo.unidbg.vm.GlobalData
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(QSecJni::class.java)

class QSecJni(val global: GlobalData) : AbstractJni() {
    override fun callIntMethodV(vm: BaseVM, dvmObject: DvmObject<*>, signature: String, vaList: VaList): Int {
        if ("java/lang/String->hashCode()I" == signature) {
            return (dvmObject.value as String).hashCode()
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList)
    }

}