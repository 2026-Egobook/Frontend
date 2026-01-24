package com.egobook.app.ui.diary

import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiariesViewModel @Inject constructor(
    private val diaryUseCases: DiaryUseCases
) {

}

//이벤트
sealed class DiariesEvent {

}

//상태 명세 정의
data class DiariesState(

)