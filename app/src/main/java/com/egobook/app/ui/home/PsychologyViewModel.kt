package com.egobook.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val psychologyRepository: UserPsychologyRepository
) : ViewModel() {

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

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dailyJob = launch { fetchDailyPsychologyInternal() }
                val savedJob = launch { fetchSavedPsychologyInternal() }
                dailyJob.join()
                savedJob.join()
            } finally {
                _isLoading.value = false
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
            _isLoading.value = true
            fetchDailyPsychologyInternal()
            _isLoading.value = false
        }
    }

    fun savePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                psychologyRepository.saveDailyPsychology(knowledgeId)
                fetchDailyPsychologyInternal()
                fetchSavedPsychologyInternal()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                psychologyRepository.deletePsychology(knowledgeId)
                fetchDailyPsychologyInternal()
                fetchSavedPsychologyInternal()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSavedPsychology() {
        viewModelScope.launch {
            _isLoading.value = true
            fetchSavedPsychologyInternal()
            _isLoading.value = false
        }
    }
}
