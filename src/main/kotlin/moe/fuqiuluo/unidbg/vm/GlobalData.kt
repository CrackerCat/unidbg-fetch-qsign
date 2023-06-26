package moe.fuqiuluo.unidbg.vm

class GlobalData {
    private val cacheMap = hashMapOf<String, Any?>()

    operator fun get(key: String): Any? {
        return cacheMap[key]
    }

    operator fun set(key: String, any: Any?) {
        cacheMap[key] = any
    }

    fun remove(key: String) = cacheMap.remove(key)

    operator fun contains(key: String) = key in cacheMap
}