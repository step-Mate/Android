package jinproject.stepwalk.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.ExperimentalMultiProcessDataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.BodyDataPreferences
import jinproject.stepwalk.data.CurrentAuthPreferences
import jinproject.stepwalk.data.StepWalkPreferences
import jinproject.stepwalk.data.local.datastore.BodyDataPreferencesSerialize
import jinproject.stepwalk.data.local.datastore.CurrentAuthPreferencesSerializer
import jinproject.stepwalk.data.local.datastore.StepWalkPreferencesSerializer
import java.io.File
import javax.inject.Singleton

private const val StepWalkPreferenceFileName = "stepwalk_prefs.pb"

@OptIn(ExperimentalMultiProcessDataStore::class)
val Context.stepWalkPreferencesStore: DataStore<StepWalkPreferences> get() = MultiProcessDataStoreFactory.create(
    serializer = StepWalkPreferencesSerializer(),
    produceFile =  {
        File("${this.cacheDir.path}/$StepWalkPreferenceFileName")
    }
)

val Context.currentAuthPreferencesStore : DataStore<CurrentAuthPreferences> by dataStore(
    fileName = StepWalkPreferenceFileName,
    serializer = CurrentAuthPreferencesSerializer()
)

val Context.bodyDataPreferencesStore : DataStore<BodyDataPreferences> by dataStore(
    fileName = StepWalkPreferenceFileName,
    serializer = BodyDataPreferencesSerialize()
)

@Module
@InstallIn(SingletonComponent::class)
object StepWalkPreferenceModule {

    @Singleton
    @Provides
    fun providesStepWalkPreference(@ApplicationContext context: Context): DataStore<StepWalkPreferences> {
        return context.stepWalkPreferencesStore
    }

    @Singleton
    @Provides
    fun providesCurrentAuthPreference(@ApplicationContext context: Context) : DataStore<CurrentAuthPreferences> {
        return context.currentAuthPreferencesStore
    }

    @Singleton
    @Provides
    fun providesBodyDataPreference(@ApplicationContext context: Context) : DataStore<BodyDataPreferences> {
        return context.bodyDataPreferencesStore
    }
}