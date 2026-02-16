package com.egobook.app.domain.usecase.letter

import androidx.paging.PagingData
import com.egobook.app.domain.model.square.letter.DeferredLetter
import com.egobook.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDeferredLettersUseCase @Inject constructor(private val repository: LetterRepository) {
    operator fun invoke(size: Int): Flow<PagingData<DeferredLetter>> = repository.fetchDeferredLetters(size = size)
}