package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.ReportLetter
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class ReportRepliedLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(replyId: Long, reportLetter: ReportLetter): Result<Unit> =
        repository.reportRepliedLetter(replyId = replyId, reportLetter = reportLetter)
}

