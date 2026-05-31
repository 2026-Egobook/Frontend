package com.egobook.app.domain.model.account

object NicknameValidator {
    private val NICKNAME_REGEX = Regex("^[가-힣a-zA-Z0-9]{2,8}$")

    fun validate(nickname: String): NicknameValidationResult {
        return when {
            nickname.isBlank() -> NicknameValidationResult.Empty
            !NICKNAME_REGEX.matches(nickname) -> NicknameValidationResult.Invalid
            else -> NicknameValidationResult.Valid
        }
    }
}

sealed class NicknameValidationResult {
    object Valid : NicknameValidationResult()
    object Empty : NicknameValidationResult()
    object Invalid : NicknameValidationResult()
}
