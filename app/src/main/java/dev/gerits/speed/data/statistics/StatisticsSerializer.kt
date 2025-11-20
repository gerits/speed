package dev.gerits.speed.data.statistics

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object StatisticsSerializer : Serializer<Statistics> {

    override val defaultValue: Statistics = Statistics(maxSpeed = 0f)

    override suspend fun readFrom(input: InputStream): Statistics =
        try {
            Json.decodeFromString<Statistics>(
                input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            throw CorruptionException("Unable to read Statistics", serialization)
        }

    override suspend fun writeTo(t: Statistics, output: OutputStream) {
        output.write(
            Json.encodeToString(t)
                .encodeToByteArray()
        )
    }
}
