package com.egobook.app.domain.model.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NicknameValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = ["가나", "에고북123", "ab", "ABC12345", "한글영문Ab12"])
    fun `유효한 닉네임은 Valid를 반환한다`(nickname: String) {
        assertThat(NicknameValidator.validate(nickname))
            .isEqualTo(NicknameValidationResult.Valid)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "  "])
    fun `빈 닉네임은 Empty를 반환한다`(nickname: String) {
        assertThat(NicknameValidator.validate(nickname))
            .isEqualTo(NicknameValidationResult.Empty)
    }

    @ParameterizedTest
    @ValueSource(strings = ["가", "a", "123456789", "닉네임!!", "nick name", "nick_name"])
    fun `형식에 맞지 않는 닉네임은 Invalid를 반환한다`(nickname: String) {
        assertThat(NicknameValidator.validate(nickname))
            .isEqualTo(NicknameValidationResult.Invalid)
    }
}
