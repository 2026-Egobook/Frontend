package com.egobook.app.domain.usecase.letter

import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.repository.LetterRepository
import javax.inject.Inject

class ReportRepliedLetterUseCase @Inject constructor(private val repository: LetterRepository) {
    suspend operator fun invoke(replyId: Long, reportContent: ReportContent): Result<Unit> =
        repository.reportRepliedLetter(replyId = replyId, reportContent = reportContent)
}

