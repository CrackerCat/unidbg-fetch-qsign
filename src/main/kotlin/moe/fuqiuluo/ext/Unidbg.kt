package moe.fuqiuluo.ext

import com.github.unidbg.linux.android.dvm.BaseVM
import com.github.unidbg.linux.android.dvm.DvmObject

fun BaseVM.newInstance(className: String, arg: Any? = null): DvmObject<*> {
    return resolveClass(className).newObject(arg)
}