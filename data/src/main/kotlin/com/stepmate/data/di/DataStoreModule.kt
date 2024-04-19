package com.stepmate.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.stepmate.data.AuthPrefs.CurrentAuthPreferences
import com.stepmate.data.BodyDataPrefs.BodyDataPreferences
import com.stepmate.data.SettingsPrefs.SettingsPreferences
import com.stepmate.data.local.datastore.BodyDataPreferencesSerialize
import com.stepmate.data.local.datastore.CurrentAuthPreferencesSerializer
import com.stepmate.data.local.datastore.SettingsPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SettingsPreferenceFileName = "settings_prefs.pb"
private const val AuthPreferenceFileName = "auth_prefs.pb"
private const val BodyDataPreferenceFileName = "bodyData_prefs.pb"

val Context.settingsPreferencesStore: DataStore<SettingsPreferences> by dataStore(
    fileName = SettingsPreferenceFileName,
    serializer = SettingsPreferencesSerializer()
)

val Context.currentAuthPreferencesStore: DataStore<CurrentAuthPreferences> by dataStore(
    fileName = AuthPreferenceFileName,
    serializer = CurrentAuthPreferencesSerializer()
)

val Context.bodyDataPreferencesStore: DataStore<BodyDataPreferences> by dataStore(
    fileName = BodyDataPreferenceFileName,
    serializer = BodyDataPreferencesSerialize()
)

@Module
@InstallIn(SingletonComponent::class)
object StepWalkPreferenceModule {

    @Singleton
    @Provides
    fun providesStepWalkPreference(@ApplicationContext context: Context): DataStore<SettingsPreferences> {
        return context.settingsPreferencesStore
    }

    @Singleton
    @Provides
    fun providesCurrentAuthPreference(@ApplicationContext context: Context): DataStore<CurrentAuthPreferences> {
        return context.currentAuthPreferencesStore
    }

    @Singleton
    @Provides
    fun providesBodyDataPreference(@ApplicationContext context: Context): DataStore<BodyDataPreferences> {
        return context.bodyDataPreferencesStore
    }

}