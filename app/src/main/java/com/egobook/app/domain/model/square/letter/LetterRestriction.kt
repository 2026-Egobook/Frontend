package com.egobook.app.domain.model.square.letter

data class LetterRestriction(
    val restricted: Boolean = false,
    val reason: String? = null,
    val restrictedUntil: String? = null
)
