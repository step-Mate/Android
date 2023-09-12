package jinproject.stepwalk.data

import androidx.datastore.core.DataStore
import jinproject.stepwalk.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val prefs: DataStore<StepWalkPreferences>
) : StepRepository {

    private val data = prefs.data
        .catch { exception ->
            if (exception is IOException) {
                emit(StepWalkPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    override fun getStep(): Flow<Int> = data.map { prefs ->
        prefs.step
    }
}