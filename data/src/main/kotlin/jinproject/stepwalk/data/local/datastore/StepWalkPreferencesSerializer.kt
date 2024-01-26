package jinproject.stepwalk.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import jinproject.stepwalk.data.StepWalkPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepWalkPreferencesSerializer @Inject constructor(): Serializer<StepWalkPreferences> {
    override val defaultValue: StepWalkPreferences = StepWalkPreferences.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): StepWalkPreferences {
        try {
            return StepWalkPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StepWalkPreferences, output: OutputStream) = t.writeTo(output)

}