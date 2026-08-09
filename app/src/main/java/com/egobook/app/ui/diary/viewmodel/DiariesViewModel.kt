package com.egobook.app.ui.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.analytics.AnalyticsParam
import com.egobook.app.domain.model.diary.entity.DiaryFilter
import com.egobook.app.domain.model.diary.entity.DiarySummary
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.ui.diary.mapper.DiaryEntityMapper
import com.egobook.app.ui.diary.model.DiaryExportUiForm
import com.egobook.app.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import javax.inject.Inject

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val _state = MutableStateFlow(DiariesState())  // 뷰모델 내부 갱신용
    val state = _state.asStateFlow()    // 외부(ui) 읽기 전용

    private val _isValidDate = MutableStateFlow<TermType>(TermType.None)
    val isValidDate = _isValidDate.asStateFlow()

    private val _fileLoadState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val fileLoadState = _fileLoadState.asStateFlow()


    private val _downloadUrl = MutableSharedFlow<String>()
    val downloadUrl = _downloadUrl.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()


    init {
        loadDiaries(LocalDate.now(), null)
    }

    fun onEvent(event: DiariesEvent) {
        when(event) {
            is DiariesEvent.SwipeTab -> {
                val domainTypes = event.displayTypes?.let {
                    DiaryEntityMapper.uiDisplayTypesToDomain(it)
                }

                _state.value = state.value.copy(selectedTabType = domainTypes)
                loadDiaries(state.value.selectedDate, domainTypes)
            }
            is DiariesEvent.ChangeDate -> {
                // 도메인 엔티티 형식으로 날짜 변환
                val date = DiaryEntityMapper.uiYearMonthDateToDomain(
                    event.year,
                    event.month,
                    event.day,
                )
                _state.value = state.value
                    .withDate(date)
                    .copy(selectedTabType = null)
                loadDiaries(date, null) // 날짜 변경 시 "전체" 탭으로 리셋
            }
            is DiariesEvent.RefreshDiaries -> {
                // 현재 선택된 날짜와 탭으로 다시 로드
                loadDiaries(state.value.selectedDate, state.value.selectedTabType)
            }
        }
    }

    //상태를 보지 말고 뷰모델 내부 state 기반으로만 동작
    private fun loadDiaries(selectedDate: LocalDate, types: Set<DiaryType>?) {
        val filter = DiaryFilter(selectedDate, types)

        // 로딩 상태 설정
        _state.value = state.value.copy(diaries = UiState.Loading)

        val diariesFlow = diaryUseCases
            .getDiaries(filter)
            .cachedIn(viewModelScope)

        _state.value = state.value.copy(diaries = UiState.Success(diariesFlow))

        // dailyCount도 함께 로드
        //loadDailyCount(selectedDate)
    }

    fun onExport(event: ExportEvent) {
        val form = when (event) {
            is ExportEvent.PDFExport -> DiaryEntityMapper.toDomainDiaryExportForm(event.diaryExportUiForm)
            is ExportEvent.TextExport -> DiaryEntityMapper.toDomainDiaryExportForm(event.diaryExportUiForm)
        }
        viewModelScope.launch {
            diaryUseCases.exportDiary(form)
                .onSuccess {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.DIARY_EXPORT,
                        mapOf(
                            AnalyticsParam.FORMAT to form.format,
                            AnalyticsParam.DATE_RANGE_DAYS to
                                (java.time.temporal.ChronoUnit.DAYS.between(form.startDate, form.endDate) + 1)
                        )
                    )
                    _downloadUrl.emit(it.fileUrl)
                }
                .onFailure {
                        e -> _errorMessage.emit(e.message ?: "내보낼 수 있는 감정 일기가 없어요")
                }
        }
    }

    /**
     * 날짜 유효성 검사 (시작 날짜가 종료 날짜보다 늦은지 확인)
     */
    fun validateDates(startDateStr: String, endDateStr: String) {
        val datePattern = Regex("""\d{4}\.\d{2}\.\d{2}""")
        if (!datePattern.matches(startDateStr) || !datePattern.matches(endDateStr)) {
            _isValidDate.value = TermType.None
            return
        }

        val formatter = DateTimeFormatter.ofPattern("uuuu.MM.dd").withResolverStyle(ResolverStyle.STRICT)

        val start = try { LocalDate.parse(startDateStr, formatter) } catch (e: Exception) { null }
        val end = try { LocalDate.parse(endDateStr, formatter) } catch (e: Exception) { null }

        if (start == null && end == null) {
            _isValidDate.value = TermType.InvalidBothDate
            return
        }
        if (start == null) {
            _isValidDate.value = TermType.InvalidStartDate
            return
        }
        if (end == null) {
            _isValidDate.value = TermType.InvalidEndDate
            return
        }

        val today = LocalDate.now()

        // 미래 날짜 체크
        if (start.isAfter(today)) {
            _isValidDate.value = TermType.StartFuture
            Timber.d("StartFuture")
            return
        }
        if (end.isAfter(today)) {
            _isValidDate.value = TermType.EndFuture
            Timber.d("EndFuture")
            return
        }

        // 시작 > 종료
        if (start.isAfter(end)) {
            _isValidDate.value = TermType.Reverse
            Timber.d("Reverse")
            return
        }

        // 1년 초과 체크
        if (start.plusYears(1).isBefore(end)) {
            _isValidDate.value = TermType.MoreThanOneYear
            Timber.d("MoreThanOneYear")
            return
        }

        _isValidDate.value = TermType.Valid
    }

    /**
     * 캐시 기반 dailyCount 조회 (캐시 없으면 API 호출)
     * btnAdd 클릭 시 48 체크용으로 사용
     */
    suspend fun getDailyCountWithCache(): Int {
        val currentDate = state.value.selectedDate
        return diaryUseCases.getDailyCount(currentDate)
            .getOrDefault(state.value.dailyCount)
    }

    // 날짜가 바뀌면 UI 표시값까지 자동 변경하는 확장함수
    private fun DiariesState.withDate(date: LocalDate): DiariesState {
        return copy(
            selectedDate = date,
            yearText = date.year.toString(),
            monthText = date.monthValue.toString(),
            dayText = date.dayOfMonth.toString()
        )
    }
}

sealed class DiariesEvent {
    data class SwipeTab(val displayTypes: Set<String>?) : DiariesEvent()
    data class ChangeDate(val year: Int, val month: Int, val day: Int) : DiariesEvent()
    data object RefreshDiaries : DiariesEvent()
}

data class DiariesState(
    val diaries: UiState<Flow<PagingData<DiarySummary>>> = UiState.Idle,
    val selectedTabType: Set<DiaryType>? = null,

    // 내부 로직용
    val selectedDate: LocalDate = LocalDate.now(),
    val dailyCount: Int = 0,  // 해당 날짜의 일기 개수

    // UI 표시용
    val yearText: String = selectedDate.year.toString(),
    val monthText: String = selectedDate.monthValue.toString(),
    val dayText: String = selectedDate.dayOfMonth.toString()
)

sealed class TermType {
    object None : TermType()
    object InvalidStartDate : TermType()
    object InvalidEndDate : TermType()
    object InvalidBothDate : TermType()
    object StartFuture : TermType()
    object EndFuture : TermType()
    object Reverse : TermType()
    object MoreThanOneYear: TermType()
    object Valid: TermType()
}

sealed class ExportEvent {
    data class PDFExport(val diaryExportUiForm: DiaryExportUiForm) : ExportEvent()
    data class TextExport(val diaryExportUiForm: DiaryExportUiForm) : ExportEvent()
}
