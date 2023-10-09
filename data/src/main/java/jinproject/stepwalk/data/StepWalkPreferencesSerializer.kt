package jinproject.stepwalk.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object StepWalkPreferencesSerializer: Serializer<StepWalkPreferences> {
    override val defaultValue: StepWalkPreferences = StepWalkPreferences.getDefaultInstance().toBuilder().setCurrent(0L).setLast(0L).build()
    override suspend fun readFrom(input: InputStream): StepWalkPreferences {
        try {
            return StepWalkPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: StepWalkPreferences, output: OutputStream) = t.writeTo(output)

}