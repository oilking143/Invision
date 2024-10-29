package com.intecular.invis.data.datastore

import androidx.datastore.core.Serializer
import com.google.crypto.tink.Aead
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

@ExperimentalSerializationApi
class AppSettingsSerializer(private val aead: Aead) : Serializer<AppSettings> {

    override val defaultValue: AppSettings
        get() = AppSettings()

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            val encryptedInput = input.readBytes()
            val decryptedInput = if (encryptedInput.isNotEmpty()) {
                aead.decrypt(encryptedInput, null)
            } else {
                encryptedInput
            }
            ProtoBuf.decodeFromByteArray(AppSettings.serializer(), decryptedInput)
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        val byteArray = ProtoBuf.encodeToByteArray(AppSettings.serializer(), t)
        val encryptedBytes = aead.encrypt(byteArray, null)
        withContext(Dispatchers.IO) {
            output.write(encryptedBytes)
        }
    }
}