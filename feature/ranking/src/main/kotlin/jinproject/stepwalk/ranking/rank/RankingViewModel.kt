package jinproject.stepwalk.ranking.rank

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.MissionComponent
import jinproject.stepwalk.domain.usecase.GetRankBoardUseCase
import jinproject.stepwalk.domain.usecase.GetUserDetailUseCase
import jinproject.stepwalk.ranking.rank.state.asRankBoard
import jinproject.stepwalk.ranking.rank.state.asUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class RankingViewModel @Inject constructor(
    private val getRankBoardUseCase: GetRankBoardUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
) : ViewModel() {

    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    private val _rankBoard: MutableStateFlow<RankBoard> =
        MutableStateFlow(RankBoard.getInitValues())
    val rankBoard: StateFlow<RankBoard> get() = _rankBoard.asStateFlow()

    private val _user: MutableStateFlow<User> = MutableStateFlow(User.getInitValues())
    val user: StateFlow<User> get() = _user.asStateFlow()

    init {
        //TODO 랭킹 화면 진입을 위해서는 로그인이 필요,
        // 토큰이 저장되어 있다면 로그인 되었음(홈화면에서 로그인 처리)
        // 토큰이 없었다면, 로그인이 필요하다는 화면을 띄워주고 로그인 및 회원가입 유도

        fetchRanking("이지훈")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchRanking(userName: String) =
        getUserDetailUseCase(userName).flatMapConcat { userDetailModel ->
            getRankBoardUseCase(1).map { rankBoardModel ->
                val rankBoard = rankBoardModel.asRankBoard(1)
                Ranking(
                    rankBoard = rankBoard,
                    user = userDetailModel.asUser(rankBoard.highestStep),
                )
            }
        }.onStart {
            _uiState.emit(UiState.Loading)
            delay(500) //TODO 없애야함
        }.onEach { ranking ->
            _uiState.emit(UiState.Success)
            _rankBoard.update { ranking.rankBoard }
            _user.update { ranking.user }
        }.catch { e ->
            _uiState.emit(UiState.Error(e))
        }.launchIn(viewModelScope)

    fun fetchMoreRankBoard() {
        fetchRankBoard(rankBoard.value.page + 1)
    }

    private fun fetchRankBoard(page: Int) =
        //TODO 랭킹보드 리스트를 가져오는 api 연결
        getRankBoardUseCase(page).onEach { rankBoardModel ->
            _rankBoard.update { state ->
                state.copy(
                    rankList = state.addNextPage(rankBoardModel.asRankBoard(page).rankList),
                    page = page
                )
            }
        }.catch { e ->
            //TODO 페이지의 끝에 도달했을 때를 처리 또는 그외 에러처리
        }.launchIn(viewModelScope)

    internal data class Ranking(
        val rankBoard: RankBoard,
        val user: User,
    )

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

@Stable
internal data class RankBoard(
    val rankList: List<Rank>,
    val page: Int = 0,
) {
    val highestStep = rankList.firstOrNull()?.step ?: 0

    //TODO: 유저가 3명 이하일 때, 처리 필요
    val top3 = rankList.take(3)
    val remain = rankList.drop(3)

    fun addNextPage(list: List<Rank>): List<Rank> = rankList.toMutableList().apply {
        addAll(list)
    }

    companion object {
        fun getInitValues() = RankBoard(emptyList())
    }
}

/**
 * @param name: 이름
 * @param character: 캐릭터 아이콘
 * @param level: 레벨
 * @param step: 걸음수
 * @param designation: 칭호
 * @param rankNumber: 순위
 * @param dailyIncreasedRank: 전일 대비 순위 상승률
 */
@Stable
data class Rank(
    val name: String,
    val character: String,
    val level: Int,
    val step: Int,
    val designation: String,
    val rankNumber: Int,
    val dailyIncreasedRank: Int,
) : Comparable<Rank> {
    companion object {
        fun getInitValues() = Rank(
            name = "홍길동",
            character = "ic_anim_running_1.json",
            level = 1,
            step = 500,
            designation = "거침없이 걷는 자",
            rankNumber = 1,
            dailyIncreasedRank = 0
        )
    }

    override fun compareTo(other: Rank): Int = when {
        this.rankNumber > other.rankNumber -> 1
        this.rankNumber < other.rankNumber -> -1
        else -> {
            this.level.compareTo(other.level).let { c ->
                if (c == 0)
                    this.name.compareTo(other.name)
                else
                    c
            }
        }
    }
}

@Stable
data class User(
    val info: Rank,
    val steps: List<Long>,
    val maxStep: Int,
    val latestMissions: List<MissionComponent>,
) {
    companion object {
        fun getInitValues() = User(
            info = Rank.getInitValues(),
            steps = emptyList(),
            maxStep = 0,
            latestMissions = emptyList()
        )
    }
}