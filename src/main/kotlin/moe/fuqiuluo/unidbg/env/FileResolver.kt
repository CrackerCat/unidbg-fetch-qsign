package moe.fuqiuluo.unidbg.env

import com.github.unidbg.Emulator
import com.github.unidbg.file.FileResult
import com.github.unidbg.file.linux.AndroidFileIO
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.file.ByteArrayFileIO
import com.github.unidbg.linux.file.DirectoryFileIO
import moe.fuqiuluo.unidbg.env.files.fetchStat
import moe.fuqiuluo.unidbg.env.files.fetchStatus
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

        Logger.getLogger("FileResolver").warning("Couldn't find file: $path")
        return def
    }

    //override fun resolveLibrary(emulator: Emulator<*>, libraryName: String): LibraryFile {
    //    logger.info("Try to find library: $libraryName")
    //    return super.resolveLibrary(emulator, libraryName)
    //}
}