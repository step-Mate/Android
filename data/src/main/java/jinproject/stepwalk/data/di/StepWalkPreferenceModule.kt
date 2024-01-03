package jinproject.stepwalk.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.ExperimentalMultiProcessDataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.StepWalkPreferences
import jinproject.stepwalk.data.StepWalkPreferencesSerializer
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

@Module
@InstallIn(SingletonComponent::class)
object StepWalkPreferenceModule {

    @Singleton
    @Provides
    fun providesStepWalkPreference(@ApplicationContext context: Context): DataStore<StepWalkPreferences> {
        return context.stepWalkPreferencesStore
    }
}