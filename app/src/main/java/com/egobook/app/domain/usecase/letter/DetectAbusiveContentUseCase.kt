package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class DetectAbusiveContentUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(text: String) = repository.detectAbusiveContent(text = text)
}