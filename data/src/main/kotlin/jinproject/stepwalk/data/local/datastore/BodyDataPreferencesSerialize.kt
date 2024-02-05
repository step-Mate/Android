package jinproject.stepwalk.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import jinproject.stepwalk.data.BodyDataPrefs.BodyDataPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyDataPreferencesSerialize @Inject constructor() : Serializer<BodyDataPreferences> {
    override val defaultValue: BodyDataPreferences = BodyDataPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): BodyDataPreferences {
        try {
            return BodyDataPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: BodyDataPreferences, output: OutputStream) = t.writeTo(output)
}