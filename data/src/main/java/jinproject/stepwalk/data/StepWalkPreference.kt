package jinproject.stepwalk.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

private const val StepWalkPreferenceFileName = "stepwalk_prefs.pb"

val Context.stepWalkPreferencesStore: DataStore<StepWalkPreferences> by dataStore(
    fileName = StepWalkPreferenceFileName,
    serializer = StepWalkPreferencesSerializer
)
