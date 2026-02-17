package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class ReportArrivedLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(letterId: Long, reportContent: ReportContent): Result<Unit> = repository.reportArrivedLetter(letterId = letterId, reportContent = reportContent)
}