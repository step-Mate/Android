package com.stepmate.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.stepmate.data.SettingsPrefs.SettingsPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferencesSerializer @Inject constructor(): Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences = SettingsPreferences.getDefaultInstance().toBuilder().setStepGoal(3000).build()
    override suspend fun readFrom(input: InputStream): SettingsPreferences {
        try {
            return SettingsPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: SettingsPreferences, output: OutputStream) = t.writeTo(output)

}