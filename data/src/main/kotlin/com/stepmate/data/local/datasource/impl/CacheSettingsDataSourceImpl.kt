package com.stepmate.data.local.datasource.impl

import androidx.datastore.core.DataStore
import com.stepmate.data.SettingsPrefs.SettingsPreferences
import com.stepmate.data.local.datasource.CacheSettingsDataSource
import com.stepmate.data.local.datastore.SettingsPreferencesSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CacheSettingsDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<SettingsPreferences>
): CacheSettingsDataSource {

    private val data = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(SettingsPreferencesSerializer().defaultValue)
            } else {
                throw exception
            }
        }

    override suspend fun setStepGoal(step: Int) {
        dataStore.updateData { prefs ->
            prefs.toBuilder().setStepGoal(step).build()
        }
    }

    override fun getStepGoal(): Flow<Int> =
        data.map { prefs -> prefs.stepGoal }

    override suspend fun setTodayStep(todayStep: Long) {
        dataStore.updateData { pref ->
            pref
                .toBuilder()
                .setTodayStep(todayStep)
                .build()
        }
    }

    override fun getTodayStep(): Flow<Long> = data.map { prefs -> prefs.todayStep }
}