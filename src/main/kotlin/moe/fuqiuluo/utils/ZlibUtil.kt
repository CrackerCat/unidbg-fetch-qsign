package moe.fuqiuluo.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater

object ZlibUtil {
    @JvmStatic
    fun unCompress(inputByte: ByteArray?): ByteArray {
        var len: Int
        val infill = Inflater()
        infill.setInput(inputByte)
        val bos = ByteArrayOutputStream()
        val outByte = ByteArray(1024)
        try {
            while (!infill.finished()) {
                len = infill.inflate(outByte)
                if (len == 0) {
                    break
                }
                bos.write(outByte, 0, len)
            }
            infill.end()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bos.toByteArray()
    }

    @JvmStatic
    fun compress(inputByte: ByteArray?): ByteArray {
        var len: Int
        val defile = Deflater()
        defile.setInput(inputByte)
        defile.finish()
        val bos = ByteArrayOutputStream()
        val outputByte = ByteArray(1024)
        try {
            while (!defile.finished()) {
                len = defile.deflate(outputByte)
                bos.write(outputByte, 0, len)
            }
            defile.end()
        } finally {
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bos.toByteArray()
    }

    @JvmStatic
    fun gzip(bytes: ByteArray): ByteArray {
        var out = EMPTY_BYTE_ARRAY
        if (bytes.isNotEmpty()) {
            ByteArrayOutputStream().use {
                GZIPOutputStream(it).use {
                    it.write(bytes)
                }
                out = it.toByteArray()
            }
        }
        return out
    }

    @JvmStatic
    fun ungzip(bArr: ByteArray): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            val gZIPInputStream = GZIPInputStream(ByteArrayInputStream(bArr))
            val bArr2 = ByteArray(256)
            while (true) {
                val read = gZIPInputStream.read(bArr2)
                if (read < 0) {
                    break
                }
                byteArrayOutputStream.write(bArr2, 0, read)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return byteArrayOutputStream.toByteArray()
    }
}