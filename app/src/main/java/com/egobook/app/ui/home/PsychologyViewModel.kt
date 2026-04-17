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

    init {
        loadDailyPsychology()
        loadSavedPsychology()
    }

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

    fun loadDailyPsychology() {
        viewModelScope.launch {
            showLoading()
            try {
                _dailyPhycologyDto.value = psychologyRepository.loadDailyPsychology()
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
                loadDailyPsychology()
                loadSavedPsychology()
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
                loadDailyPsychology()
                loadSavedPsychology()
            } finally {
                hideLoading()
            }
        }
    }

    fun loadSavedPsychology() {
        viewModelScope.launch {
            showLoading()
            try {
                _savedPsychologies.value = psychologyRepository.loadSavedPsychology()
            } finally {
                hideLoading()
            }
        }
    }
}
