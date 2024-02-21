package jinproject.stepwalk.ranking.rank

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.UserStepRank
import jinproject.stepwalk.domain.model.mission.MissionComponent
import jinproject.stepwalk.domain.usecase.GetRankBoardUseCase
import jinproject.stepwalk.domain.usecase.GetUserDetailUseCase
import jinproject.stepwalk.domain.usecase.auth.CheckHasTokenUseCase
import jinproject.stepwalk.ranking.rank.state.asRank
import jinproject.stepwalk.ranking.rank.state.asRankBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class RankingViewModel @Inject constructor(
    private val getRankBoardUseCase: GetRankBoardUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val checkHasTokenUseCase: CheckHasTokenUseCase,
) : ViewModel() {

    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    private val _rankBoard: MutableStateFlow<RankBoard> =
        MutableStateFlow(RankBoard.getInitValues())
    val rankBoard: StateFlow<RankBoard> get() = _rankBoard.asStateFlow()

    private val _friendRankBoard: MutableStateFlow<RankBoard> =
        MutableStateFlow(RankBoard.getInitValues())
    val friendRankBoard: StateFlow<RankBoard> get() = _friendRankBoard.asStateFlow()

    private val _rankTab: MutableStateFlow<RankTab> =
        MutableStateFlow(RankTab.MONTH)
    val rankTab: StateFlow<RankTab> get() = _rankTab.asStateFlow()

    private val _user: MutableStateFlow<User> = MutableStateFlow(User.getInitValues())
    val user: StateFlow<User> get() = _user.asStateFlow()

    init {
        //TODO 토큰을 이용해서 내정보(유저정보)를 가져오는 api 연결 필요
        checkHasTokenUseCase().onEach { token ->
            if (token)
                flow {
                    emit("이지훈")
                }.flatMapLatest { userName ->
                    fetchRanking().map { list ->
                        Ranking(
                            rankBoard = list.first(),
                            friendRankBoard = list.last(),
                            userName = userName,
                        )
                    }
                }.transformLatest { ranking ->
                    rankTab.collectLatest { tab ->
                        val rank = when (tab) {
                            RankTab.MONTH -> ranking.rankBoard.rankList.find { it.name == ranking.userName }
                                ?: throw IllegalStateException("전체 랭킹 보드에서 유저 ${ranking.userName} 의 정보를 찾을 수 없음")

                            RankTab.FRIEND -> ranking.friendRankBoard.rankList.find { it.name == ranking.userName }
                                ?: throw IllegalStateException("친구 랭킹 보드에서 유저 ${ranking.userName} 의 정보를 찾을 수 없음")
                        }

                        val maxStep = when (tab) {
                            RankTab.MONTH -> rankBoard.value.highestStep

                            RankTab.FRIEND -> friendRankBoard.value.highestStep
                        }

                        emit(
                            User(
                                rank = rank,
                                maxStep = maxStep,
                            )
                        )
                    }
                }.collectLatest { user ->
                    _user.update { user }
                    _uiState.emit(UiState.Success)
                }
            else
                _uiState.emit(UiState.Error(CANNOT_LOGIN_EXCEPTION))
        }.launchIn(viewModelScope)
    }

    fun changeRankTab(tab: RankTab) = _rankTab.update { tab }

    private fun fetchRanking() = getRankBoardUseCase(1)
        .zip(getRankBoardUseCase(1)) { monthRankBoardModel, friendRankBoardModel ->

            val monthRankBoard = monthRankBoardModel.asRankBoard(1)

            val list = friendRankBoardModel.list.map {
                UserStepRank(
                    user = jinproject.stepwalk.domain.model.User(
                        name = if(it.user.name != "이지훈") it.user.name + "a" else it.user.name,
                        character = it.user.character,
                        level = it.user.level,
                        designation = it.user.designation
                    ),
                    stepRank = it.stepRank
                )
            }

            val friendRankBoard = RankBoard(
                rankList = list.map { userRankModel -> userRankModel.asRank() },
                page = 1
            )

            listOf(monthRankBoard, friendRankBoard)
        }.onStart {
            _uiState.emit(UiState.Loading)
            delay(500) //TODO 없애야함
        }.onEach { list ->
            _rankBoard.update { list.first() }
            _friendRankBoard.update { list.last() }
        }.catch { e ->
            _uiState.emit(UiState.Error(e))
            Log.d("test", "catch")
        }

    fun fetchMoreMonthRankBoard() {
        val page = when (rankTab.value) {
            RankTab.MONTH -> rankBoard.value.page
            RankTab.FRIEND -> friendRankBoard.value.page
        }
        fetchRankBoard(page + 1)
    }

    private fun fetchRankBoard(page: Int) {
        //TODO 랭킹보드 리스트를 가져오는 api 연결
        when (rankTab.value) {
            RankTab.MONTH -> {
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
            }

            RankTab.FRIEND -> {
                getRankBoardUseCase(page).onEach { rankBoardModel ->
                    _friendRankBoard.update { state ->
                        state.copy(
                            rankList = state.addNextPage(rankBoardModel.asRankBoard(page).rankList),
                            page = page
                        )
                    }
                }.catch { e ->
                    //TODO 페이지의 끝에 도달했을 때를 처리 또는 그외 에러처리
                }.launchIn(viewModelScope)
            }
        }
    }

    internal data class Ranking(
        val rankBoard: RankBoard,
        val friendRankBoard: RankBoard,
        val userName: String,
    )

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

    @Stable
    enum class RankTab(val display: String) {
        MONTH("월간 랭킹"),
        FRIEND("친구 랭킹"),
    }

    data class User(
        val rank: Rank,
        val maxStep: Int,
    ) {
        companion object {
            fun getInitValues() = User(
                rank = Rank.getInitValues(),
                maxStep = 0,
            )
        }
    }

    companion object {
        val CANNOT_LOGIN_EXCEPTION = IllegalStateException("로그인 불가")
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