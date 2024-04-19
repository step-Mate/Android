package com.stepmate.ranking.rank

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.core.SnackBarMessage
import com.stepmate.core.catchDataFlow
import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.model.user.User
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.rank.GetRankBoardUseCase
import com.stepmate.domain.usecase.rank.GetUserRankUseCase
import com.stepmate.domain.usecase.user.GetFriendRequestUseCase
import com.stepmate.ranking.rank.state.asRank
import com.stepmate.ranking.rank.state.asRankBoard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class RankingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRankBoardUseCase: GetRankBoardUseCase,
    checkHasTokenUseCase: CheckHasTokenUseCase,
    private val getUserRankUseCase: GetUserRankUseCase,
    private val getFriendRequestUseCase: GetFriendRequestUseCase,
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

    private val _snackBarState: MutableSharedFlow<SnackBarMessage> = MutableSharedFlow(replay = 0)
    val snackBarState: SharedFlow<SnackBarMessage> get() = _snackBarState.asSharedFlow()

    private val deletedFriend get() = savedStateHandle.get<String>("deletedFriendName") ?: ""

    private val _isRequestedFriend = MutableStateFlow(false)
    val isRequestedFriend get() = _isRequestedFriend.asStateFlow()

    init {
        checkHasTokenUseCase().flatMapLatest { token ->
            if (token) {
                getIsRequestedFriend()
                getUserRankUseCase().zip(fetchRanking()) { userStepRank, _ ->
                    val user = User(
                        rank = userStepRank.asRank(),
                        maxStep = rankBoard.value.highestStep,
                    )

                    _user.update { user }
                    _uiState.emit(UiState.Success)
                }
            }
            else
                flow {
                    _uiState.emit(UiState.Error(CANNOT_LOGIN_EXCEPTION))
                }
        }.catchDataFlow(
            action = { e ->
                when (e.code) {
                    401, 402 -> CANNOT_LOGIN_EXCEPTION
                    404 -> {
                        e
                    }

                    else -> e
                }
            }, onException = { e ->
                Log.d("test", "catch : ${e.message}")
                _uiState.emit(UiState.Error(e))
            }
        ).launchIn(viewModelScope)
    }

    fun changeRankTab(tab: RankTab) = _rankTab.update { tab }

    fun deleteFriendIfDeleted() {
        if (deletedFriend.isNotBlank()) {
            _friendRankBoard.update { state ->
                state.copy(
                    rankList = state.rankList.toMutableList()
                        .apply { removeIf { rank -> rank.name == deletedFriend } }
                )
            }

            savedStateHandle.remove<String>("deletedFriend")
        }
    }

    private fun fetchRanking() = getRankBoardUseCase.getMonthRankBoard(1)
        .zip(getRankBoardUseCase.getFriendRankBoard(1)) { monthRankBoardModel, friendRankBoardModel ->

            val monthRankBoard = monthRankBoardModel.asRankBoard(1)

            val list = friendRankBoardModel.list.map {
                UserStepRank(
                    user = User(
                        name = it.user.name,
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
        }.onEach { list ->
            _rankBoard.update { list.first() }
            _friendRankBoard.update { list.last() }
        }

    fun fetchMoreMonthRankBoard() {
        val page = when (rankTab.value) {
            RankTab.MONTH -> rankBoard.value.page
            RankTab.FRIEND -> friendRankBoard.value.page
        }
        fetchRankBoard(page + 1)
    }

    private fun fetchRankBoard(page: Int) {
        when (rankTab.value) {
            RankTab.MONTH -> {
                getRankBoardUseCase.getMonthRankBoard(page)
                    .transform { rankBoardModel ->
                        if (rankBoardModel.list.isNotEmpty())
                            emit(rankBoardModel.asRankBoard(page))
                        else {
                            _snackBarState.emit(
                                SnackBarMessage(
                                    headerMessage = "월간 랭킹의 끝에 도달했어요.",
                                    contentMessage = "더 이상 불러올 유저가 없어요."
                                )
                            )
                        }
                    }.onEach { rankBoard ->
                        _rankBoard.update { state ->
                            state.copy(
                                rankList = state.addNextPage(rankBoard.rankList),
                                page = page
                            )
                        }
                    }
            }

            RankTab.FRIEND -> {
                getRankBoardUseCase.getFriendRankBoard(page)
                    .transform { rankBoardModel ->
                        if (rankBoardModel.list.isNotEmpty())
                            emit(rankBoardModel.asRankBoard(page))
                        else {
                            _snackBarState.emit(
                                SnackBarMessage(
                                    headerMessage = "친구 랭킹의 끝에 도달했어요.",
                                    contentMessage = "더 이상 불러올 친구가 없어요."
                                )
                            )
                        }
                    }.onEach { rankBoardModel ->
                        _friendRankBoard.update { state ->
                            state.copy(
                                rankList = state.addNextPage(rankBoardModel.rankList),
                                page = page
                            )
                        }
                    }
            }
        }.catchDataFlow(
            action = { e ->
                e
            },
            onException = {
                //TODO 에러처리
            }
        ).launchIn(viewModelScope)
    }

    private suspend fun getIsRequestedFriend() {
        getFriendRequestUseCase().onEach {requestedFriendList ->
            _isRequestedFriend.update { requestedFriendList.isNotEmpty() }
        }.catchDataFlow(
            action = { e -> e },
            onException = {

            }
        ).collect()
    }

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