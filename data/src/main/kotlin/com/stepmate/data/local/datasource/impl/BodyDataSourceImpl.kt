package com.stepmate.data.local.datasource.impl

import androidx.datastore.core.DataStore
import com.stepmate.data.BodyDataPrefs.BodyDataPreferences
import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.domain.model.BodyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class BodyDataSourceImpl @Inject constructor(
    private val prefs: DataStore<BodyDataPreferences>
) : BodyDataSource {

    private val data = prefs.data
        .catch { exception ->
            if (exception is IOException) {
                emit(BodyDataPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override fun getBodyData(): Flow<BodyData> =
        data.map { BodyData(it.age, it.height, it.weight) }

    override suspend fun setAge(age: Int) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setAge(age)
                .build()
        }
    }

    override suspend fun setHeight(height: Int) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setHeight(height)
                .build()
        }
    }

    override suspend fun setWeight(weight: Int) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setWeight(weight)
                .build()
        }
    }

    override suspend fun setBodyData(body: BodyData) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setAge(body.age)
                .setHeight(body.height)
                .setWeight(body.weight)
                .build()
        }
    }
    override suspend fun getCalories(step: Int) =
        3.0 * (3.5 * data.map { it.weight }
            .first() * step * 0.0008 * 15) * 5 / 1000
}