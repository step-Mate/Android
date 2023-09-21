package jinproject.stepwalk.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.data.StepWalkPreferences
import jinproject.stepwalk.data.stepWalkPreferencesStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StepWalkPreferenceModule {

    @Singleton
    @Provides
    fun providesStepWalkPreference(@ApplicationContext context: Context): DataStore<StepWalkPreferences> {
        return context.stepWalkPreferencesStore
    }
}