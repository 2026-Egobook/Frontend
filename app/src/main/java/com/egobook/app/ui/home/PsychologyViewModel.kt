package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.analytics.AnalyticsEvent
import com.egobook.app.analytics.AnalyticsLogger
import com.egobook.app.ui.home.repository.DailyPsychologyDto
import com.egobook.app.ui.home.repository.PsychologyKnowledge
import com.egobook.app.ui.home.repository.PsychologyReward
import com.egobook.app.ui.home.repository.SavedPsychologyDto
import com.egobook.app.ui.home.repository.UserPsychologyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PsychologyViewModel @Inject constructor(
    private val psychologyRepository: UserPsychologyRepository,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private var loadingCount = 0
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _savedPsychologies = MutableStateFlow(emptyList<SavedPsychologyDto>())
    val savedPsychologies: StateFlow<List<SavedPsychologyDto>> = _savedPsychologies.asStateFlow()

    private val _dailyPhycologyDto = MutableStateFlow(
        DailyPsychologyDto(
            date = "0000-00-00",
            knowledge = PsychologyKnowledge(
                knowledgeId = 0,
                title = "",
                content = "",
                source = ""
            ),
            reward = PsychologyReward(
                granted = false,
                inkGranted = 0,
                inkBalance = 0,
                toastMessage = ""
            ),
            isBookmarked = false
        )
    )
    val dailyPhycologyDto: StateFlow<DailyPsychologyDto> = _dailyPhycologyDto.asStateFlow()

    init {
        loadInitialData()
    }

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

    private fun loadInitialData() {
        viewModelScope.launch {
            showLoading()
            try {
                val dailyJob = launch { fetchDailyPsychologyInternal() }
                val savedJob = launch { fetchSavedPsychologyInternal() }
                dailyJob.join()
                savedJob.join()
            } finally {
                hideLoading()
            }
        }
    }

    private suspend fun fetchDailyPsychologyInternal() {
        try {
            _dailyPhycologyDto.value = psychologyRepository.loadDailyPsychology()
        } catch (e: Exception) {
            // Error handling
        }
    }

    private suspend fun fetchSavedPsychologyInternal() {
        try {
            _savedPsychologies.value = psychologyRepository.loadSavedPsychology()
        } catch (e: Exception) {
            // Error handling
        }
    }

    fun loadDailyPsychology() {
        viewModelScope.launch {
            showLoading()
            try {
                fetchDailyPsychologyInternal()
            } finally {
                hideLoading()
            }
        }
    }

    fun savePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            showLoading()
            try {
                psychologyRepository.saveDailyPsychology(knowledgeId)
                analyticsLogger.logEvent(AnalyticsEvent.PSYCH_KNOWLEDGE_SAVE)
                fetchDailyPsychologyInternal()
                fetchSavedPsychologyInternal()
            } finally {
                hideLoading()
            }
        }
    }

    fun deletePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            showLoading()
            try {
                psychologyRepository.deletePsychology(knowledgeId)
                analyticsLogger.logEvent(AnalyticsEvent.PSYCH_KNOWLEDGE_UNSAVE)
                fetchDailyPsychologyInternal()
                fetchSavedPsychologyInternal()
            } finally {
                hideLoading()
            }
        }
    }

    fun loadSavedPsychology() {
        viewModelScope.launch {
            showLoading()
            try {
                fetchSavedPsychologyInternal()
            } finally {
                hideLoading()
            }
        }
    }
}
