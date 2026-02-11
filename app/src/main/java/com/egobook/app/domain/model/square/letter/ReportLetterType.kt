package com.egobook.app.domain.model.square.letter

enum class ReportLetterType(val value: String) {
    ABUSE("ABUSE"), // 비속어/욕설/모욕
    SPAM("SPAM"), // 광고/스팸
    INAPPROPRIATE("INAPPROPRIATE"), // 부적절한 콘텐츠
    OTHER("OTHER") // 기타
}