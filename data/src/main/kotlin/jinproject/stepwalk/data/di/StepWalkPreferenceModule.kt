package jinproject.stepwalk.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.BodyDataPrefs.BodyDataPreferences
import jinproject.stepwalk.data.AuthPrefs.CurrentAuthPreferences
import jinproject.stepwalk.data.StepwalkPrefs.StepWalkPreferences
import jinproject.stepwalk.data.local.datastore.BodyDataPreferencesSerialize
import jinproject.stepwalk.data.local.datastore.CurrentAuthPreferencesSerializer
import jinproject.stepwalk.data.local.datastore.StepWalkPreferencesSerializer
import javax.inject.Singleton

private const val StepWalkPreferenceFileName = "stepwalk_prefs.pb"
private const val AuthPreferenceFileName = "auth_prefs.pb"
private const val BodyDataPreferenceFileName = "bodyData_prefs.pb"

val Context.stepWalkPreferencesStore: DataStore<StepWalkPreferences> by dataStore(
    fileName = StepWalkPreferenceFileName,
    serializer = StepWalkPreferencesSerializer()
)

val Context.currentAuthPreferencesStore : DataStore<CurrentAuthPreferences> by dataStore(
    fileName = AuthPreferenceFileName,
    serializer = CurrentAuthPreferencesSerializer()
)

val Context.bodyDataPreferencesStore : DataStore<BodyDataPreferences> by dataStore(
    fileName = BodyDataPreferenceFileName,
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