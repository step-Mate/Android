package jinproject.stepwalk.data.local.datasource.impl

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import jinproject.stepwalk.data.UserPrefs.UserPreferences
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val prefs: DataStore<UserPreferences>
) : UserDataSource {

    private val data = prefs.data
        .catch { exception ->
            if (exception is IOException)
                emit(UserPreferences.getDefaultInstance())
            else
                throw exception
        }

    override fun getUserData(): Flow<User> =
        data.map {
            User(
                name = it.nickname,
                character = "ic_anim_running_1.json",
                level = it.level,
                designation = it.designation
            )
        }

    override suspend fun setNickname(nickname: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setNickname(nickname)
                .build()
        }
    }

    override suspend fun setLevel(level: Int) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setLevel(level)
                .build()
        }
    }

    override suspend fun setDesignation(designation: String) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setDesignation(designation)
                .build()
        }
    }

    override suspend fun setUserData(user: User) {
        prefs.updateData { pref ->
            pref.toBuilder()
                .setNickname(user.name)
                .setLevel(user.level)
                .setDesignation(user.designation)
                .build()
        }
    }

    override suspend fun clearUser() {
        prefs.updateData { pref ->
            pref.toBuilder()
                .clearNickname()
                .clearLevel()
                .clearDesignation()
                .build()
        }
    }

}