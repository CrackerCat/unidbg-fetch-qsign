package moe.fuqiuluo.unidbg.env

import com.github.unidbg.linux.android.dvm.*
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

    override fun callStaticObjectMethodV(
        vm: BaseVM,
        dvmClass: DvmClass,
        signature: String,
        vaList: VaList
    ): DvmObject<*> {
        if (signature == "com/tencent/mobileqq/dt/app/Dtc->mmKVValue(Ljava/lang/String;)Ljava/lang/String;") {
            val key = vaList.getObjectArg<StringObject>(0).value
            if (key == "o3_switch_Xwid") {
                return StringObject(vm, "1")
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList)
    }
}