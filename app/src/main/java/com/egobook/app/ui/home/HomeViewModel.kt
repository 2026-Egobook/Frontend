package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.notice.NoticeOpenResult
import com.egobook.app.domain.model.notice.NoticePolicy
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.home.repository.AdInfoDto
import com.egobook.app.ui.home.repository.NoticeRepository
import com.egobook.app.ui.home.repository.UserAdRepository
import com.egobook.app.ui.home.repository.UserPsychologyRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.repository.UserTendencyRepository
import com.egobook.app.ui.home.user.Ink
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.home.user.Tendency
import com.egobook.app.store.data.ShopRepository
import com.egobook.app.store.ui.CustomItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import javax.inject.Inject
import timber.log.Timber
import com.egobook.app.domain.usecase.GenerateTurtleShareUseCase
import com.egobook.app.util.UiState

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userAdRepository: UserAdRepository,
    private val psychologyRepository: UserPsychologyRepository,
    private val noticeRepository: NoticeRepository,
    private val shopRepository: ShopRepository,
    private val userTendencyRepository: UserTendencyRepository,
    private val diaryUseCases: DiaryUseCases,
    private val analyticsLogger: AnalyticsLogger,
    private val generateTurtleShareUseCase: GenerateTurtleShareUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(User(id=-1, Level(1),Ink(0), nickname = ""))
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    private val _tendencies = MutableStateFlow<List<Tendency>>(emptyList())
    val tendencies: StateFlow<List<Tendency>> = _tendencies.asStateFlow()

    private val _adState = MutableStateFlow(AdInfoDto(0, 0, false, 0, ""))
    val adState: StateFlow<AdInfoDto> = _adState.asStateFlow()

    private val _isLoadingAdInfo = MutableStateFlow(false)
    val isLoadingAdInfo: StateFlow<Boolean> = _isLoadingAdInfo.asStateFlow()

    private val _equippedItems = MutableStateFlow<List<CustomItem>>(emptyList())
    val equippedItems: StateFlow<List<CustomItem>> = _equippedItems
    private var equippedItemsReady = false
    private val _shareImage = MutableStateFlow<UiState<String>>(UiState.Idle)
    val shareImage = _shareImage.asStateFlow()

    fun createShareImage() {
        if (_shareImage.value is UiState.Loading) return
        if (!equippedItemsReady) {
            _shareImage.value = UiState.Failure("착용 정보를 불러온 후 다시 시도해 주세요.")
            return
        }
        val items = _equippedItems.value.toList()
        _shareImage.value = UiState.Loading
        viewModelScope.launch {
            try { _shareImage.value = UiState.Success(generateTurtleShareUseCase(items)) }
            catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                Timber.e(error, "Failed to create turtle share image")
                _shareImage.value = UiState.Failure("공유 이미지를 만들지 못했습니다. 연결 상태를 확인하고 다시 시도해 주세요.")
            }
        }
    }

    fun resetShareImage() { _shareImage.value = UiState.Idle }

    private val _dailyPhycologyReadState = MutableStateFlow(false)
    val dailyPhycologyReadState = _dailyPhycologyReadState.asStateFlow()

    private val _hasUnreadNotice = MutableStateFlow(false)
    val hasUnreadNotice: StateFlow<Boolean> = _hasUnreadNotice.asStateFlow()

    private val _noticeOpenResult = MutableSharedFlow<NoticeOpenResult>()
    val noticeOpenResult = _noticeOpenResult.asSharedFlow()

    private var noticeRequest: Job? = null
    private var noticeReadVersion = 0L

    // 과거의 나 돌아보기: 띄울 일기가 있으면 값이 채워지고, 팝업을 띄운 뒤 onRecallShown()으로 비운다.
    private val _recallDiary = MutableStateFlow<Diary?>(null)
    val recallDiary: StateFlow<Diary?> = _recallDiary.asStateFlow()

    // 과거의 나 돌아보기에서 '오늘 일기 쓰기'를 눌렀을 때 작성 가능 여부 (하루 48개 제한)
    private val _canWriteTodayDiary = MutableSharedFlow<Boolean>()
    val canWriteTodayDiary = _canWriteTodayDiary.asSharedFlow()

    private var hasCheckedRecall = false

    private var loadingCount = 0
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun showLoading() {
        loadingCount++
        _isLoading.value = true
    }

    private fun hideLoading() {
        loadingCount--
        if (loadingCount <= 0) {
            loadingCount = 0
            _isLoading.value = false
        }
    }

    init {
        fetchInitialData()
        fetchRecallDiary()
    }

    /**
     * 앱 진입 시 '과거의 나 돌아보기'로 띄울 일기를 조회한다.
     * 노출 주기(21일)와 일기 선정은 서버가 판단하며, 대상이 없으면 아무것도 하지 않는다.
     */
    private fun fetchRecallDiary() {
        if (hasCheckedRecall) return
        hasCheckedRecall = true
        viewModelScope.launch {
            diaryUseCases.getRecallDiary()
                .onSuccess { diary -> _recallDiary.value = diary }
                .onFailure { error -> Timber.e(error, "Failed to fetch recall diary") }
        }
    }

    /**
     * 과거의 나 돌아보기 팝업을 실제로 띄운 직후 호출해 노출을 확정한다. (다음 21일 주기 시작)
     */
    fun onRecallShown(diaryId: Long) {
        _recallDiary.value = null
        viewModelScope.launch {
            diaryUseCases.markRecallSeen(diaryId)
                .onFailure { error -> Timber.e(error, "Failed to mark recall diary seen: $diaryId") }
        }
    }

    /**
     * 오늘 일기를 더 쓸 수 있는지(하루 48개 미만) 확인해 알린다.
     */
    fun checkCanWriteTodayDiary() {
        viewModelScope.launch {
            val dailyCount = diaryUseCases.getDailyCount(LocalDate.now()).getOrDefault(0)
            _canWriteTodayDiary.emit(dailyCount < MAX_DAILY_DIARY_COUNT)
        }
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            showLoading()
            try {
                // Fetch all initial data in parallel if possible, or sequentially
                val userJob = launch { fetchUserInternal() }
                val equipJob = launch { fetchEquipItemsInternal() }
                val psychJob = launch { fetchDailyPhycologyReadStateInternal() }
                val tendencyJob = launch { fetchTendenciesInternal() }
                
                userJob.join()
                equipJob.join()
                psychJob.join()
                tendencyJob.join()
            } finally {
                hideLoading()
            }
        }
    }

    private suspend fun fetchUserInternal() {
        val readVersionAtStart = noticeReadVersion
        try {
            val user = userRepository.load()
            _uiState.value = user
            if (readVersionAtStart == noticeReadVersion) {
                _hasUnreadNotice.value = user.hasUnreadNotice
            }
        } catch(error: Exception) {
            Timber.e(error, "Failed to fetch user")
        }
    }

    private suspend fun fetchTendenciesInternal() {
        try {
            val previousTendencies = _tendencies.value
            val tendencyList = userTendencyRepository.loadTendencies()
            _tendencies.value = tendencyList
            tendencyList.forEach { updated ->
                val previous = previousTendencies.find { it.type == updated.type }
                if (previous != null && updated.level > previous.level) {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.LEVEL_UP,
                        mapOf(
                            AnalyticsParam.STAT_TYPE to updated.type.name,
                            AnalyticsParam.NEW_LEVEL to updated.level
                        )
                    )
                }
            }
        } catch (error: Exception) {
            Timber.e(error, "Failed to fetch tendencies")
        }
    }

    private suspend fun fetchEquipItemsInternal() {
        equippedItemsReady = false
        try {
            _equippedItems.value = shopRepository.loadEquippedItems()
            equippedItemsReady = true
        } catch(error: Exception) {
            Timber.e(error, "Failed to fetch equip items")
        }
    }

    private suspend fun fetchDailyPhycologyReadStateInternal() {
        try {
            _dailyPhycologyReadState.value = psychologyRepository.isReadDailyPsychology()
        } catch(error: Exception) {
            Timber.e(error, "Failed to fetch psychology state")
        }
    }

    fun fetchEquipItems() {
        viewModelScope.launch {
            showLoading()
            fetchEquipItemsInternal()
            hideLoading()
        }
    }

    fun fetchDailyPhycologyReadState() {
        viewModelScope.launch {
            showLoading()
            fetchDailyPhycologyReadStateInternal()
            hideLoading()
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            showLoading()
            fetchUserInternal()
            hideLoading()
        }
    }

    fun fetchTendencies() {
        viewModelScope.launch {
            showLoading()
            fetchTendenciesInternal()
            hideLoading()
        }
    }

    fun loadCurrentAdInfo() {
        viewModelScope.launch {
            _isLoadingAdInfo.value = true
            try {
                val adInfo = userAdRepository.loadAdInfo()
                _adState.value = adInfo
            } catch (error: Exception) {
                Timber.e(error, "Failed to load ad info")
            } finally {
                _isLoadingAdInfo.value = false
            }
        }
    }

    /**
     * 최신 공지를 조회해 웹뷰로 열 수 있는지 결과를 알린다.
     *
     * 서버는 조회에 성공한 시점에 읽음 처리하므로, 열어줄 공지가 없더라도 조회가 성공했으면 레드닷을 해제한다.
     * 조회 자체가 실패했을 때만 레드닷을 유지한다.
     */
    fun openNotice() {
        if (noticeRequest?.isActive == true) return
        noticeRequest = viewModelScope.launch {
            runCatching { noticeRepository.loadLatestNotice() }
                .onSuccess { notice ->
                    noticeReadVersion++
                    _hasUnreadNotice.value = false
                    _noticeOpenResult.emit(NoticePolicy.resolve(notice))
                }
                .onFailure { error ->
                    if (error is CancellationException) throw error
                    Timber.e(error, "Failed to load latest notice")
                    _noticeOpenResult.emit(NoticeOpenResult.Failure)
                }
        }
    }

    fun logPsychKnowledgeOpen() {
        // 이 진입점(홈의 오늘의 병 아이콘)은 당일 미열람 상태일 때만 노출되므로 항상 최초 오픈이다.
        analyticsLogger.logEvent(
            AnalyticsEvent.PSYCH_KNOWLEDGE_OPEN,
            mapOf(AnalyticsParam.IS_FIRST_TODAY to true)
        )
    }

    fun watchAd() {
        viewModelScope.launch {
            showLoading()
            try {
                val message = userAdRepository.watchAd()
                fetchUserInternal()
                val freshAdCount = runCatching { userAdRepository.loadAdInfo().currentViewCount }
                    .getOrDefault(_adState.value.currentViewCount + 1)
                analyticsLogger.logEvent(
                    AnalyticsEvent.AD_REWARD_WATCH,
                    mapOf(AnalyticsParam.AD_COUNT_TODAY to freshAdCount)
                )
                Timber.d("Ad watched: $message")
            } finally {
                hideLoading()
            }
        }
    }

    companion object {
        private const val MAX_DAILY_DIARY_COUNT = 48
    }
}
