package com.egobook.app.domain.model.square.letter

enum class LetterStatus(val value: String) {
    SENT("SENT"),
    ARRIVED("ARRIVED"),
    DEFERRED("DEFERRED"),
    REPLIED("REPLIED"),
    GAVE_UP("GAVE_UP"),
    AI_REPLIED("AI_REPLIED")
}
