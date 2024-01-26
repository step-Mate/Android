package jinproject.stepwalk.data.local.datasource.impl

import androidx.datastore.core.DataStore
import jinproject.stepwalk.data.CurrentAuthPreferences
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.domain.model.CurrentAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CurrentAuthDataSourceImpl @Inject constructor(
    private val prefs : DataStore<CurrentAuthPreferences>
) : CurrentAuthDataSource{

    private val data = prefs.data
        .catch { exception ->
            if (exception is IOException) {
                emit(CurrentAuthPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override fun getCurrentAuth(): Flow<CurrentAuth> = data.map { prefs ->
        CurrentAuth(
            id = prefs.id,
            token = prefs.accessToken
        )
    }

    override suspend fun setId(id: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setId(id)
                .build()
        }
    }

    override suspend fun setAccessToken(token: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setAccessToken(token)
                .build()
        }
    }

    override suspend fun setCurrentAuth(auth: CurrentAuth) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setId(auth.id)
                .setAccessToken(auth.token)
                .build()
        }
    }

    override suspend fun clearAuth() {
        prefs.updateData { pref ->
            pref.toBuilder()
                .clearId()
                .clearAccessToken()
                .build()
        }
    }

}