package moe.fuqiuluo.unidbg.env

import com.github.unidbg.Emulator
import com.github.unidbg.file.FileResult
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.linux.file.DirectoryFileIO
import moe.fuqiuluo.ext.hex2ByteArray
import moe.fuqiuluo.ext.newBuilder
import moe.fuqiuluo.unidbg.env.files.fetchStat
import moe.fuqiuluo.unidbg.env.files.fetchStatus
import moe.fuqiuluo.unidbg.ext.ByteBufferFileIO
import java.util.logging.Logger

class FileResolver(
    sdk: Int
): AndroidResolver(sdk) {
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
                // TODO(fix)
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

        if (path == "/system/bin/sh" || path == "/system/bin/ls") {
            return FileResult.success(ByteArrayFileIO(oflags, path, byteArrayOf(0x7f, 0x45, 0x4c, 0x46, 0x02, 0x01, 0x01, 0x00)))
        }

        if (path.contains(".system_android_l2")) {
            val newPath = if (path.startsWith("C:")) path.substring(2) else path
            return FileResult.success(ByteBufferFileIO(oflags, newPath, "619F9042CA821CF91DFAF172D464FFC7A6CB8E024CC053F7438429FA38E86854471D6B0A9DE4C39BF02DC18C0CC54A715C9210E8A32B284366849CBB7F88C634AA".hex2ByteArray()))
        }

        Logger.getLogger("FileResolver").warning("Couldn't find file: $path")
        return def
    }

    //override fun resolveLibrary(emulator: Emulator<*>, libraryName: String): LibraryFile {
    //    logger.info("Try to find library: $libraryName")
    //    return super.resolveLibrary(emulator, libraryName)
    //}
}