package jinproject.stepwalk.data.local.datasource.impl

import androidx.datastore.core.DataStore
import jinproject.stepwalk.data.BodyDataPrefs.BodyDataPreferences
import jinproject.stepwalk.data.local.datasource.BodyDataSource
import jinproject.stepwalk.domain.model.BodyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
}