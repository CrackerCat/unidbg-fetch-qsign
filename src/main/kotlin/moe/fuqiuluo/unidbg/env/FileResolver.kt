package moe.fuqiuluo.unidbg.env

import com.github.unidbg.Emulator
import com.github.unidbg.file.FileResult
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.linux.file.DirectoryFileIO
import com.github.unidbg.linux.file.SimpleFileIO
import com.github.unidbg.unix.UnixEmulator
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.unidbg.QSecVM
import moe.fuqiuluo.unidbg.env.files.fetchStat
import moe.fuqiuluo.unidbg.env.files.fetchStatus
import java.util.logging.Logger

class FileResolver(
    sdk: Int,
    vm: QSecVM
): AndroidResolver(sdk) {
    private val tmpFilePath = vm.coreLibPath

    override fun resolve(emulator: Emulator<AndroidFileIO>, path: String, oflags: Int): FileResult<AndroidFileIO>? {
        val result = super.resolve(emulator, path, oflags)
        if (result == null || !result.isSuccess) {
            return this.resolve(emulator, path, oflags, result)
        }
        return result
    }

    private fun resolve(emulator: Emulator<AndroidFileIO>, path: String, oflags: Int, def: FileResult<AndroidFileIO>?): FileResult<AndroidFileIO>? {
        if (path == "/proc/self/status") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchStatus(emulator.pid).toByteArray()))
        }
        if (path == "/proc/stat") {
            return FileResult.success(ByteArrayFileIO(oflags, path, fetchStat()))
        }
        if (path == "/dev/__properties__") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(true, "properties_serial"),
            ))
        }

        if ("/proc/self/maps" == path) {
            return FileResult.success(ByteArrayFileIO(oflags, path, byteArrayOf()))
        }

        if (path == "/data/data/com.tencent.mobileqq") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, "files"),
                DirectoryFileIO.DirectoryEntry(false, "shared_prefs"),
                DirectoryFileIO.DirectoryEntry(false, "cache"),
                DirectoryFileIO.DirectoryEntry(false, "code_cache"),
            ))
        }

        if (path == "/dev/urandom") {
            return FileResult.failed(UnixEmulator.ENOENT)
        }

        if (path == "/sdcard/Android/") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, "data"),
                DirectoryFileIO.DirectoryEntry(false, "obb"),
            ))
        }

        if (path == "/system/lib64/libhoudini.so" || path == "/system/lib/libhoudini.so") {
            return FileResult.failed(UnixEmulator.ENOENT)
        }

        if (path == "/proc") {
            return FileResult.success(DirectoryFileIO(oflags, path,
                DirectoryFileIO.DirectoryEntry(false, emulator.pid.toString()),
            ))
        }

        if (path == "/proc/${emulator.pid}/cmdline"
            || path == "/proc/stat/cmdline" // an error case
            ) {
            return FileResult.success(ByteArrayFileIO(oflags, path, "com.tencent.mobileqq:MSF".toByteArray()))
        }

        if (path == "/system/bin/sh" || path == "/system/bin/ls" || path == "/system/lib/libc.so") {
            return FileResult.success(ByteArrayFileIO(oflags, path, byteArrayOf(0x7f, 0x45, 0x4c, 0x46, 0x02, 0x01, 0x01, 0x00)))
        }

        if (path.startsWith("/data/user/")) {
            if (path != "/data/user/0" && path != "/data/user/999") {
                return FileResult.failed(UnixEmulator.ENOENT)
            } else {
                return FileResult.failed(UnixEmulator.EACCES)
            }
        }

        if (path.contains(".system_android_l2")) {
            val newPath = if (path.startsWith("C:")) path.substring(2) else path
            val file = tmpFilePath.resolve(".system_android_l2")
            if (!file.exists()) {
                file.writeBytes("619F9042CA821CF91DFAF172D464FFC7A6CB8E024CC053F7438429FA38E86854471D6B0A9DE4C39BF02DC18C0CC54A715C9210E8A32B284366849CBB7F88C634AA".hex2ByteArray())
            }
            return FileResult.success(SimpleFileIO(oflags, file, newPath))
        }

        Logger.getLogger("FileResolver").warning("Couldn't find file: $path")
        return def
    }
}