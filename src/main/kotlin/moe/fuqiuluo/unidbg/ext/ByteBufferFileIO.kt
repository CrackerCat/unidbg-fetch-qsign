package moe.fuqiuluo.unidbg.ext

import com.github.unidbg.Emulator
import com.github.unidbg.arm.backend.Backend
import com.github.unidbg.file.linux.BaseAndroidFileIO
import com.github.unidbg.file.linux.StatStructure
import com.sun.jna.Pointer
import java.nio.ByteBuffer

typealias BytesObject = com.github.unidbg.linux.android.dvm.array.ByteArray

class ByteBufferFileIO(
    val oflags: Int,
    val mypath: String,
    var bytes: ByteArray
): BaseAndroidFileIO(oflags) {
    private lateinit var buffer: ByteBuffer
    private var pos = 0

    override fun write(data: ByteArray): Int {
        if (!::buffer.isInitialized) {
            buffer = ByteBuffer.allocate(bytes.size)
        }
        buffer.put(data)
        return data.size
    }

    override fun pread(backend: Backend?, buffer: Pointer, count: Int, offset: Long): Int {
        val tmp = pos
        val size: Int
        try {
            this.pos = offset.toInt()
            size = read(backend, buffer, count)
        } finally {
            this.pos = tmp
        }
        return size
    }

    override fun read(backend: Backend?, buffer: Pointer, count: Int): Int {
        var tmp = count
        return if (pos >= bytes.size) {
            0
        } else {
            val remain = bytes.size - pos
            if (tmp > remain) {
                tmp = remain
            }
            buffer.write(0L, bytes, pos, tmp)
            pos += tmp
            tmp
        }
    }

    override fun lseek(offset: Int, whence: Int): Int {
        return when (whence) {
            0 -> {
                pos = offset
                pos
            }

            1 -> {
                pos += offset
                pos
            }

            2 -> {
                pos = bytes.size + offset
                pos
            }

            else -> super.lseek(offset, whence)
        }
    }

    override fun fstat(emulator: Emulator<*>, stat: StatStructure): Int {
        stat.st_dev = 1L
        stat.st_mode = 32768
        stat.st_uid = 0
        stat.st_gid = 0
        stat.st_size = bytes.size.toLong()
        stat.st_blksize = emulator.pageAlign
        stat.st_blocks = ((bytes.size + emulator.pageAlign - 1) / emulator.pageAlign).toLong()
        stat.st_ino = 1L
        stat.setLastModification(System.currentTimeMillis())
        stat.pack()
        return 0
    }

    override fun getMmapData(addr: Long, offset: Int, length: Int): ByteArray? {
        return if (offset == 0 && length == bytes.size) {
            bytes
        } else {
            val data = ByteArray(length)
            System.arraycopy(bytes, offset, data, 0, data.size)
            data
        }
    }

    override fun ioctl(emulator: Emulator<*>?, request: Long, argp: Long): Int {
        return 0
    }

    override fun toString(): String {
        return mypath
    }

    override fun close() {
        this.pos = 0
    }
}