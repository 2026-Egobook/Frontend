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

    fun loadDailyPsychology() {
        viewModelScope.launch {
            _dailyPhycologyDto.value = psychologyRepository.loadDailyPsychology()
        }
    }

    fun savePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            psychologyRepository.saveDailyPsychology(knowledgeId)
            loadDailyPsychology()
            loadSavedPsychology()
        }
    }

    fun deletePsychology(knowledgeId: Int) {
        viewModelScope.launch {
            psychologyRepository.deletePsychology(knowledgeId)
            loadDailyPsychology()
            loadSavedPsychology()
        }
    }

    fun loadSavedPsychology() {
        viewModelScope.launch {
            _savedPsychologies.value = psychologyRepository.loadSavedPsychology()
        }
    }
}
