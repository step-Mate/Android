package jinproject.stepwalk.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import jinproject.stepwalk.data.CurrentAuthPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentAuthPreferencesSerializer @Inject constructor() : Serializer<CurrentAuthPreferences> {
    override val defaultValue: CurrentAuthPreferences = CurrentAuthPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CurrentAuthPreferences {
        try {
            return CurrentAuthPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: CurrentAuthPreferences, output: OutputStream) = t.writeTo(output)
}