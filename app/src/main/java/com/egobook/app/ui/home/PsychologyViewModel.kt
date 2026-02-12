package com.egobook.app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egobook.app.ui.home.repository.DailyPsychologyDto
import com.egobook.app.ui.home.repository.PsychologyKnowledge
import com.egobook.app.ui.home.repository.PsychologyReward
import com.egobook.app.ui.home.repository.UserPsychologyRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.user.Ink
import com.egobook.app.ui.home.user.Level
import com.egobook.app.ui.home.user.User
import com.egobook.app.ui.shop.CustomItem
import com.egobook.app.ui.shop.StoreRepository
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
}
