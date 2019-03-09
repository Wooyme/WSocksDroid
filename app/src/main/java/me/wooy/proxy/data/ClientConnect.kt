package me.wooy.proxy.data

import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import me.wooy.proxy.encryption.Aes

class ClientConnect(private val buffer: Buffer) {
  private val json = Buffer.buffer(Aes.decrypt(buffer.getBytes(IntSize,buffer.length()))).toJsonObject()
  val host get() = json.getString("host")
  val port get() = json.getInteger("port")
  val uuid get() = json.getString("uuid")

  fun toBuffer() = buffer
  companion object {
    fun create(uuid:String,host: String, port: Int): ClientConnect {
      val buffer = Buffer.buffer()
        .appendBuffer(JsonObject().put("host", host).put("port", port).put("uuid", uuid).toBuffer())
      val encryptedBuffer = Aes.encrypt(buffer.bytes)
      return ClientConnect(Buffer.buffer()
        .appendIntLE(Flag.CONNECT.ordinal)
        .appendBytes(encryptedBuffer))
    }
  }
}
