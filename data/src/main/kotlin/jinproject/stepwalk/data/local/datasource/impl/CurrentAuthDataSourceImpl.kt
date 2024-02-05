package jinproject.stepwalk.data.local.datasource.impl

import androidx.datastore.core.DataStore
import jinproject.stepwalk.data.AuthPrefs.CurrentAuthPreferences
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class CurrentAuthDataSourceImpl @Inject constructor(
    private val prefs: DataStore<CurrentAuthPreferences>
) : CurrentAuthDataSource {
    private val data = prefs.data
        .catch { exception ->
            if (exception is IOException) {
                emit(CurrentAuthPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override fun getAccessToken(): Flow<String> =
        data.map { it.accessToken }

    override fun getRefreshToken(): Flow<String> =
        data.map { it.refreshToken }

    override suspend fun setAccessToken(token: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setAccessToken(token)
                .build()
        }
    }

    override suspend fun setRefreshToken(token: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setRefreshToken(token)
                .build()
        }
    }

    override suspend fun setToken(accessToken: String, refreshToken: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken)
                .build()
        }
    }

    override suspend fun clearAuth() {
        prefs.updateData { pref ->
            pref.toBuilder()
                .clearAccessToken()
                .clearRefreshToken()
                .build()
        }
    }

}