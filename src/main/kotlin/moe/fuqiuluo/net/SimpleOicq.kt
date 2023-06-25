package moe.fuqiuluo.net

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class SimpleOicq(
    val host: String,
    val port: Int
) {

    lateinit var socket: Socket
    lateinit var input: DataInputStream
    lateinit var output: DataOutputStream

    fun connect(): Boolean {
        this.socket = Socket(host, port)
        return this.socket.isConnected
    }

    fun init() {
        if (this.socket.isConnected && !this.socket.isInputShutdown && !this.socket.isOutputShutdown) {
            this.input = DataInputStream(this.socket.getInputStream())
            this.output = DataOutputStream(this.socket.getOutputStream())
        } else {
            this.socket.close()
        }
    }


}