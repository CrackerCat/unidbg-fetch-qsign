package moe.fuqiuluo.unidbg.env

import ANDROID_ID
import com.github.unidbg.linux.android.dvm.*
import com.tencent.mobileqq.channel.ChannelManager
import com.tencent.mobileqq.qsec.qsecurity.DeepSleepDetector
import moe.fuqiuluo.ext.toHexString
import moe.fuqiuluo.net.FromService
import moe.fuqiuluo.net.OnPacketListener
import moe.fuqiuluo.net.SimpleClient
import moe.fuqiuluo.net.SsoPacket
import moe.fuqiuluo.unidbg.QSecVM
import moe.fuqiuluo.unidbg.ext.BytesObject
import moe.fuqiuluo.unidbg.vm.GlobalData
import moe.xinrao.unidbg.env.FEBound
import org.slf4j.LoggerFactory
import java.io.File

private val logger = LoggerFactory.getLogger(QSecJni::class.java)

class QSecJni(
    val vm: QSecVM,
    val client: SimpleClient,
    val global: GlobalData
) : AbstractJni() {
    override fun getStaticIntField(vm: BaseVM, dvmClass: DvmClass, signature: String): Int {
        if (signature == "android/os/Build\$VERSION->SDK_INT:I") {
            return 23
        }
        return super.getStaticIntField(vm, dvmClass, signature)
    }

    override fun getIntField(vm: BaseVM, dvmObject: DvmObject<*>, signature: String): Int {
        if (signature == "android/content/pm/ApplicationInfo->targetSdkVersion:I") {
            return 26
        }
        return super.getIntField(vm, dvmObject, signature)
    }

    override fun callVoidMethodV(vm: BaseVM, dvmObject: DvmObject<*>, signature: String, vaList: VaList) {
        if (signature == "com/tencent/mobileqq/fe/IFEKitLog->i(Ljava/lang/String;ILjava/lang/String;)V") {
            val tag = vaList.getObjectArg<StringObject>(0)
            val msg = vaList.getObjectArg<StringObject>(2)
            println(tag.value + "info: " + msg.value)
            return
        }
        if (signature == "com/tencent/mobileqq/fe/IFEKitLog->e(Ljava/lang/String;ILjava/lang/String;)V") {
            val tag = vaList.getObjectArg<StringObject>(0)
            val msg = vaList.getObjectArg<StringObject>(2)
            println(tag.value + "error: " + msg.value)
            return
        }
        if (signature == "com/tencent/mobileqq/channel/ChannelProxy->sendMessage(Ljava/lang/String;[BJ)V") {
            val cmd = vaList.getObjectArg<StringObject>(0).value
            val data = vaList.getObjectArg<BytesObject>(1).value
            val callbackId = vaList.getLongArg(2)

            println("sendPacket(cmd = $cmd, data = ${data.toHexString()}, id = $callbackId)")

            if (cmd == "trpc.o3.ecdh_access.EcdhAccess.SsoEstablishShareKey") {
                val seq = client.nextSeq()
                client.register(cmd, seq).async(dataListener = object: OnPacketListener {
                    override fun onReceive(from: FromService?) {
                        if (from == null) return
                        ChannelManager
                            .onNativeReceive(this@QSecJni.vm,
                                from.commandName, from.body, callbackId)
                    }
                })
                client.sendPacket(SsoPacket(cmd, seq, data))
            }
            return
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList)
    }

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
                return StringObject(vm, global["o3_switch_Xwid"] as? String ?: "1")
            }
        }
        if (signature == "android/provider/Settings\$System->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;") {
            val key = vaList.getObjectArg<StringObject>(1).value
            if (key == "android_id") {
                return StringObject(vm, ANDROID_ID)
            }
        }
        if (signature == "com/tencent/mobileqq/fe/utils/DeepSleepDetector->getCheckResult()Ljava/lang/String;") {
            if ("DeepSleepDetector" !in global) {
                global["DeepSleepDetector"] = DeepSleepDetector()
            }
            val result = (global["DeepSleepDetector"] as DeepSleepDetector).getCheckResult()
            return StringObject(vm, result.toString())
        }
        if (signature == "com/tencent/mobileqq/dt/model/FEBound->transform(I[B)[B") {
            val mode = vaList.getIntArg(0)
            val data = vaList.getObjectArg<DvmObject<*>>(1).value as ByteArray
            return BytesObject(vm, FEBound.transform(mode, data))
        }
        if (signature == "java/lang/ClassLoader->getSystemClassLoader()Ljava/lang/ClassLoader;") {
            return vm.resolveClass("java/lang/ClassLoader")
                .newObject(ClassLoader.getSystemClassLoader())
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList)
    }

    override fun callStaticVoidMethodV(vm: BaseVM, dvmClass: DvmClass, signature: String, vaList: VaList) {
        if (signature == "com/tencent/mobileqq/fe/utils/DeepSleepDetector->stopCheck()V") {
            if ("DeepSleepDetector" in global) {
                (global["DeepSleepDetector"] as DeepSleepDetector).stopped = true
            }
            return
        }
        if (signature == "com/tencent/mobileqq/dt/app/Dtc->mmKVSaveValue(Ljava/lang/String;Ljava/lang/String;)V") {
            val key = vaList.getObjectArg<StringObject>(0).value
            val value = vaList.getObjectArg<StringObject>(1).value
            global[key] = value
            return
        }
        super.callStaticVoidMethodV(vm, dvmClass, signature, vaList)
    }



    override fun callObjectMethodV(
        vm: BaseVM,
        dvmObject: DvmObject<*>,
        signature: String,
        vaList: VaList
    ): DvmObject<*> {
        if (signature == "android/content/Context->getApplicationInfo()Landroid/content/pm/ApplicationInfo;") {
            return vm.resolveClass("android/content/pm/ApplicationInfo").newObject(null)
        }
        if (signature == "android/content/Context->getFilesDir()Ljava/io/File;") {
            return vm
                .resolveClass("java.io.File")
                .newObject(File("/data/user/0/com.tencent.mobileqq/files"))
                //.newObject(LinuxFile("/com.tencent.mobileqq/files", "/data/user/0/com.tencent.mobileqq"))
        }
        if (signature == "android/content/Context->getContentResolver()Landroid/content/ContentResolver;") {
            return vm.resolveClass("android/content/ContentResolver")
                .newObject(null)
        }
        if (signature == "android/content/Context->getPackageResourcePath()Ljava/lang/String;") {
            return StringObject(vm, "/data/app/~~vbcRLwPxS0GyVfqT-nCYrQ==/com.tencent.mobileqq-xJKJPVp9lorkCgR_w5zhyA==/base.apk")
        }
        if (signature == "android/content/Context->getPackageName()Ljava/lang/String;") {
            return StringObject(vm, "com.tencent.mobileqq")
        }
        if(signature == "java/lang/ClassLoader->loadClass(Ljava/lang/String;)Ljava/lang/Class;") {
            val name = vaList.getObjectArg<StringObject>(0).value
            val loader = dvmObject.value as ClassLoader
            try {
                return vm
                    .resolveClass("java/lang/Class")
                    .newObject(loader.loadClass(name))
            } catch (e: ClassNotFoundException) {
                vm.throwException(vm
                    .resolveClass("java.lang.ClassNotFoundException")
                    .newObject(e)
                )
            }
            return  vm
                .resolveClass("java/lang/Class")
                .newObject(null)
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList)
    }

    override fun newObjectV(vm: BaseVM, dvmClass: DvmClass, signature: String, vaList: VaList): DvmObject<*> {
        if (signature == "java/io/File-><init>(Ljava/lang/String;)V") {
            val path = vaList.getObjectArg<StringObject>(0).value
            return vm
                .resolveClass("java/io/File")
                .newObject(File(path))
        }
        return super.newObjectV(vm, dvmClass, signature, vaList)
    }

    override fun callBooleanMethodV(
        vm: BaseVM,
        dvmObject: DvmObject<*>,
        signature: String,
        vaList: VaList
    ): Boolean {
        if (signature == "java/io/File->canRead()Z") {
            val file = dvmObject.value as File
            if (
                file.toString() == "\\data\\data\\com.tencent.mobileqq\\.." ||
                file.toString() == "/data/data/com.tencent.mobileqq/.." ||
                file.toString() == "/data/data/" ||
                file.toString() == "/data/data"
            ) {
                return false
            }
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList)
    }

    override fun callObjectMethod(
        vm: BaseVM,
        dvmObject: DvmObject<*>,
        signature: String,
        varArg: VarArg
    ): DvmObject<*> {
        return super.callObjectMethod(vm, dvmObject, signature, varArg)
    }
}