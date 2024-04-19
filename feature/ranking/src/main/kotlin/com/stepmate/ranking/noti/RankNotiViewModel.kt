package com.stepmate.ranking.noti

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.core.SnackBarMessage
import com.stepmate.core.catchDataFlow
import com.stepmate.domain.usecase.user.GetFriendRequestUseCase
import com.stepmate.domain.usecase.user.ProcessFriendRequestUseCase
import com.stepmate.ranking.noti.state.RequestedFriendList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankNotiViewModel @Inject constructor(
    private val processFriendRequestUseCase: ProcessFriendRequestUseCase,
    private val getFriendRequestUseCase: GetFriendRequestUseCase,
) : ViewModel() {

    private val _requestedFriendList: MutableStateFlow<RequestedFriendList> = MutableStateFlow(
        RequestedFriendList(emptyList())
    )
    val requestedFriendList: StateFlow<RequestedFriendList> get() = _requestedFriendList.asStateFlow()

    private val _snackBarState: MutableSharedFlow<SnackBarMessage> = MutableSharedFlow(replay = 0)
    val snackBarState: SharedFlow<SnackBarMessage> get() = _snackBarState.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, t ->
        viewModelScope.launch {
            _snackBarState.emit(
                SnackBarMessage(
                    headerMessage = "오류가 발생했어요.",
                    contentMessage = "${t.message}"
                )
            )
        }
    }

    var isNeedToRefresh = false
        private set

    init {
        getFriendRequestList()
    }

    fun processRequestFriend(bool: Boolean, userName: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            processFriendRequestUseCase(bool, userName)

            getFriendRequestList()

            if(bool) {
                isNeedToRefresh = true

                _snackBarState.emit(
                    SnackBarMessage(
                        headerMessage = "$userName 님의 요청을 수락했어요.",
                        contentMessage = "$userName 님과 친구가 되었어요."
                    )
                )
            }
            else
                _snackBarState.emit(
                    SnackBarMessage(
                        headerMessage = "$userName 님의 요청을 거절했어요.",
                    )
                )
        }
    }

    private fun getFriendRequestList() {
        getFriendRequestUseCase().onEach { requestedFriends ->
            _requestedFriendList.update { RequestedFriendList(requestedFriends) }
        }.catchDataFlow(
            action = { e ->
                Log.d("test", "exception ${e.message} has occurred")
                e
            },
            onException = { e ->
                _snackBarState.emit(
                    SnackBarMessage(
                        headerMessage = "오류가 발생했어요.",
                        contentMessage = "${e.message}"
                    )
                )
            }
        ).launchIn(viewModelScope)
    }
}
