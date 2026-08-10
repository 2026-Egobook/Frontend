package com.egobook.app.ui.square.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
import com.egobook.app.domain.model.square.letter.LetterMode
import com.egobook.app.domain.usecase.GetFriendListUseCase
import com.egobook.app.domain.usecase.GetUserInfoUseCase
import com.egobook.app.store.data.ShopRepository
import com.egobook.app.domain.model.square.letter.LetterPaperItem
import com.egobook.app.domain.usecase.letter.DeferReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.DeleteLetterThreadUseCase
import com.egobook.app.domain.usecase.letter.DetectAbusiveContentUseCase
import com.egobook.app.domain.usecase.letter.GetArrivedPendingLetterUseCase
import com.egobook.app.domain.usecase.letter.GetDeferredLettersUseCase
import com.egobook.app.domain.usecase.letter.GetReceivedReplyByIdUseCase
import com.egobook.app.domain.usecase.letter.GetSentLetterWithReplyUseCase
import com.egobook.app.domain.usecase.letter.GetSentLettersUseCase
import com.egobook.app.domain.usecase.letter.GiveUpReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.ReplyLetterUseCase
import com.egobook.app.domain.usecase.letter.ReportArrivedLetterUseCase
import com.egobook.app.domain.usecase.letter.ReportRepliedLetterUseCase
import com.egobook.app.domain.usecase.letter.SendLetterUseCase
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.square.model.friend.FriendListModel
import com.egobook.app.ui.square.model.friend.toPresentation
import com.egobook.app.ui.square.model.letter.AbusiveContentModel
import com.egobook.app.ui.square.model.letter.ArrivedPendingLetterModel
import com.egobook.app.ui.square.model.letter.DeferredLetterModel
import com.egobook.app.ui.square.model.letter.ReceivedReplyModel
import com.egobook.app.ui.square.model.letter.ReplyLetterModel
import com.egobook.app.ui.square.model.letter.ReportContentModel
import com.egobook.app.ui.square.model.letter.SendLetterModel
import com.egobook.app.ui.square.model.letter.SentLetterModel
import com.egobook.app.ui.square.model.letter.SentLetterWithReplyModel
import com.egobook.app.ui.square.model.letter.toDomain
import com.egobook.app.ui.square.model.letter.toPresentation
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LetterViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val getFriendListUseCase: GetFriendListUseCase,
    private val sendLetterUseCase: SendLetterUseCase,
    private val detectAbusiveContentUseCase: DetectAbusiveContentUseCase,
    private val getArrivedPendingLetterUseCase: GetArrivedPendingLetterUseCase,
    private val replyLetterUseCase: ReplyLetterUseCase,
    private val deferReplyLetterUseCase: DeferReplyLetterUseCase,
    private val giveUpReplyLetterUseCase: GiveUpReplyLetterUseCase,
    private val getSentLettersUseCase: GetSentLettersUseCase,
    private val getSentLetterWithReplyUseCase: GetSentLetterWithReplyUseCase,
    private val reportRepliedLetterUseCase: ReportRepliedLetterUseCase,
    private val reportArrivedLetterUseCase: ReportArrivedLetterUseCase,
    private val deleteLetterThreadUseCase: DeleteLetterThreadUseCase,
    private val getDeferredLettersUseCase: GetDeferredLettersUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getReceivedReplyByIdUseCase: GetReceivedReplyByIdUseCase,
    private val analyticsLogger: AnalyticsLogger
): ViewModel() {

    private val _letterPaperItems = MutableStateFlow<UiState<List<LetterPaperItem>>>(UiState.Idle)
    val letterPaperItems = _letterPaperItems.asStateFlow()

    private val _purchaseLetterPaperResult = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val purchaseLetterPaperResult = _purchaseLetterPaperResult.asStateFlow()

    fun loadLetterPaperItems() {
        viewModelScope.launch {
            _letterPaperItems.value = UiState.Loading
            runCatching { shopRepository.loadLetterPaperItems() }
                .onSuccess { items -> _letterPaperItems.value = UiState.Success(items) }
                .onFailure { error -> _letterPaperItems.value = UiState.Failure(error.message) }
        }
    }

    init {
        loadLetterPaperItems()
    }

    private val _friendList = MutableStateFlow<UiState<FriendListModel>>(UiState.Idle)
    val friendList = _friendList.asStateFlow()

    fun getFriendList() {
        viewModelScope.launch {
            _friendList.value = UiState.Loading
            getFriendListUseCase().onSuccess { domainList ->
                _friendList.value = UiState.Success(domainList.toPresentation())
            }.onFailure { error ->
                _friendList.value = UiState.Failure(error.message)
            }
        }
    }

    private val _sendLetterResult = MutableSharedFlow<UiState<Unit>>()
    val sendLetterResult = _sendLetterResult.asSharedFlow()

    fun sendLetter(letter: SendLetterModel) {
        viewModelScope.launch {
            _sendLetterResult.emit(UiState.Loading)
            sendLetterUseCase(letter = letter.toDomain()).onSuccess {
                analyticsLogger.logEvent(
                    AnalyticsEvent.LETTER_SEND,
                    mapOf(AnalyticsParam.TARGET_TYPE to letter.mode.value.lowercase())
                )
                _sendLetterResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _sendLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _detectAbusiveContentResult = MutableSharedFlow<UiState<AbusiveContentModel>>()
    val detectAbusiveContentResult = _detectAbusiveContentResult.asSharedFlow()
    private var detectAbusiveContentJob: Job? = null

    fun detectAbusiveContent(text: String) {
        detectAbusiveContentJob?.cancel()
        detectAbusiveContentJob = viewModelScope.launch {
            _detectAbusiveContentResult.emit(UiState.Loading)
            detectAbusiveContentUseCase(text = text).onSuccess { domain ->
                _detectAbusiveContentResult.emit(UiState.Success(domain.toPresentation()))
            }.onFailure { error ->
                _detectAbusiveContentResult.emit(UiState.Failure(error.message))
            }
        }
    }

    fun cancelDetectAbusiveContent() {
        detectAbusiveContentJob?.cancel()
        detectAbusiveContentJob = null
    }

    private val _arrivedPendingLetterResult = MutableStateFlow<UiState<ArrivedPendingLetterModel>>(UiState.Idle) // stateflow vs sharedflow
    val arrivedPendingLetterResult = _arrivedPendingLetterResult.asStateFlow()

    // 화면 재진입마다 같은 편지를 다시 폴링해도 letter_receive가 중복 집계되지 않도록 마지막으로 집계한 편지 ID를 기억한다.
    private var lastNotifiedArrivedLetterId: Long? = null

    fun getArrivedPendingLetter() {
        viewModelScope.launch {
            _arrivedPendingLetterResult.value = UiState.Loading
            getArrivedPendingLetterUseCase().onSuccess { domain ->
                val presentation = domain.toPresentation()
                presentation.letter?.let { letter ->
                    if (letter.letterId != lastNotifiedArrivedLetterId) {
                        lastNotifiedArrivedLetterId = letter.letterId
                        val fromType = when (letter.mode) {
                            LetterMode.RANDOM -> "stranger"
                            LetterMode.FRIEND -> "friend"
                        }
                        analyticsLogger.logEvent(
                            AnalyticsEvent.LETTER_RECEIVE,
                            mapOf(AnalyticsParam.FROM_TYPE to fromType)
                        )
                    }
                }
                _arrivedPendingLetterResult.value = UiState.Success(presentation)
            }.onFailure { error ->
                _arrivedPendingLetterResult.value = UiState.Failure(error.message)
            }
        }
    }

    fun resetArrivedPendingLetterStatus() {
        _arrivedPendingLetterResult.value = UiState.Idle
    }

    private val _replyLetterResult = MutableSharedFlow<UiState<ReplyLetterModel>>()
    val replyLetterResult = _replyLetterResult.asSharedFlow()

    fun replyLetter(letterId: Long, text: String) {
        viewModelScope.launch {
            _replyLetterResult.emit(UiState.Loading)
            replyLetterUseCase(letterId = letterId, text = text).onSuccess { domain ->
                analyticsLogger.logEvent(AnalyticsEvent.LETTER_REPLY_SEND)
                _replyLetterResult.emit(UiState.Success(domain.toPresentation()))
            }.onFailure { error ->
                _replyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deferReplyLetterResult = MutableSharedFlow<UiState<Unit>>()
    val deferReplyLetterResult = _deferReplyLetterResult.asSharedFlow()

    fun deferReplyLetter(letterId: Long) {
        viewModelScope.launch {
            _deferReplyLetterResult.emit(UiState.Loading)
            deferReplyLetterUseCase(letterId = letterId).onSuccess {
                _deferReplyLetterResult.emit(UiState.Success(Unit))
            }.onFailure { error ->
                _deferReplyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _giveUpReplyLetterResult = MutableSharedFlow<UiState<Unit>>()
    val giveUpReplyLetterResult = _giveUpReplyLetterResult.asSharedFlow()

    fun giveUpReplyLetter(letterId: Long) {
        viewModelScope.launch {
            _giveUpReplyLetterResult.emit(UiState.Loading)
            giveUpReplyLetterUseCase(letterId = letterId).onSuccess {
                analyticsLogger.logEvent(AnalyticsEvent.LETTER_GIVE_UP)
                _giveUpReplyLetterResult.emit(UiState.Success(Unit))
            }.onFailure { error ->
                _giveUpReplyLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _sentLetters = MutableStateFlow<PagingData<SentLetterModel>?>(null)
    val sentLetters = _sentLetters.asStateFlow()

    fun getSentLetters(size: Int) {
        viewModelScope.launch {
            getSentLettersUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _sentLetters.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _sentLetterWithReply = MutableStateFlow<UiState<SentLetterWithReplyModel>>(UiState.Idle)
    val sentLetterWithReply = _sentLetterWithReply.asStateFlow()

    fun getSentLetterWithReply(letterId: Long) {
        viewModelScope.launch {
            _sentLetterWithReply.value = UiState.Loading
            getSentLetterWithReplyUseCase(letterId = letterId).onSuccess { domain ->
                _sentLetterWithReply.value = UiState.Success(domain.toPresentation())
            }.onFailure { error ->
                _sentLetterWithReply.value = UiState.Failure(error.message)
            }
        }
    }

    private val _reportArrivedLetterResult = MutableSharedFlow<UiState<Unit>>()
    val reportArrivedLetterResult = _reportArrivedLetterResult.asSharedFlow()

    fun reportArrivedLetter(letterId: Long, reportLetter: ReportContentModel) {
        viewModelScope.launch {
            _reportArrivedLetterResult.emit(UiState.Loading)
            reportArrivedLetterUseCase(letterId = letterId, reportContent = reportLetter.toDomain()).onSuccess {
                analyticsLogger.logEvent(
                    AnalyticsEvent.LETTER_REPORT,
                    mapOf(AnalyticsParam.REASON to reportLetter.reason.value.lowercase())
                )
                _reportArrivedLetterResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _reportArrivedLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _reportRepliedLetterResult = MutableSharedFlow<UiState<Unit>>()
    val reportRepliedLetterResult = _reportRepliedLetterResult.asSharedFlow()

    fun reportRepliedLetter(replyId: Long, reportLetter: ReportContentModel) {
        viewModelScope.launch {
            _reportRepliedLetterResult.emit(UiState.Loading)
            reportRepliedLetterUseCase(replyId = replyId, reportContent = reportLetter.toDomain()).onSuccess {
                analyticsLogger.logEvent(
                    AnalyticsEvent.LETTER_REPORT,
                    mapOf(AnalyticsParam.REASON to reportLetter.reason.value.lowercase())
                )
                _reportRepliedLetterResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _reportRepliedLetterResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deleteLetterThreadResult = MutableSharedFlow<UiState<Unit>>()
    val deleteLetterThreadResult = _deleteLetterThreadResult.asSharedFlow()

    fun deleteLetterThread(threadId: Long) {
        viewModelScope.launch {
            _deleteLetterThreadResult.emit(UiState.Loading)
            deleteLetterThreadUseCase(threadId = threadId).onSuccess {
                analyticsLogger.logEvent(AnalyticsEvent.LETTER_DELETE)
                _deleteLetterThreadResult.emit(UiState.Success(it))
            }.onFailure { error ->
                _deleteLetterThreadResult.emit(UiState.Failure(error.message))
            }
        }
    }

    private val _deferredLetters = MutableStateFlow<PagingData<DeferredLetterModel>?>(null)
    val deferredLetters = _deferredLetters.asStateFlow()

    fun getDeferredLetters(size: Int) {
        viewModelScope.launch {
            getDeferredLettersUseCase(size = size).cachedIn(viewModelScope).collectLatest { pagingData ->
                _deferredLetters.value = pagingData.map { it.toPresentation() }
            }
        }
    }

    private val _receivedReplies = MutableStateFlow<UiState<ReceivedReplyModel>>(UiState.Idle)
    val receivedReplies = _receivedReplies.asStateFlow()

    fun getReceivedReplyById(replyId: Long) {
        viewModelScope.launch {
            _receivedReplies.value = UiState.Loading
            getReceivedReplyByIdUseCase(replyId = replyId).onSuccess { domainItem ->
                _receivedReplies.value = UiState.Success(domainItem.toPresentation())
            }.onFailure { error ->
                _receivedReplies.value = UiState.Failure(error.message)
            }
        }
    }

    private val _userInfo = MutableSharedFlow<UiState<User>>()
    val userInfo = _userInfo.asSharedFlow()

    fun getUserInfo() {
        viewModelScope.launch {
            _userInfo.emit(UiState.Loading)
            getUserInfoUseCase().onSuccess { domain ->
                _userInfo.emit( UiState.Success(domain))
            }.onFailure { error ->
                _userInfo.emit(UiState.Failure(error.message))
            }
        }
    }

    fun purchaseLetterPaper(item: LetterPaperItem) {
        viewModelScope.launch {
            _purchaseLetterPaperResult.value = UiState.Loading
            runCatching { shopRepository.purchaseLetterPaperItem(item) }
                .onSuccess {
                    _letterPaperItems.value = UiState.Success(
                        (_letterPaperItems.value as? UiState.Success)?.data?.map {
                            if (it.id == item.id) it.copy(isPurchased = true) else it
                        } ?: emptyList()
                    )
                    _purchaseLetterPaperResult.value = UiState.Success(Unit)
                }
                .onFailure { error -> _purchaseLetterPaperResult.value = UiState.Failure(error.message) }
        }
    }

    fun resetPurchaseLetterPaperResult() {
        _purchaseLetterPaperResult.value = UiState.Idle
    }
}
