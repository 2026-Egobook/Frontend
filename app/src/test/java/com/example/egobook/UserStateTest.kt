package com.example.egobook

import com.example.egobook.ui.home.Level
import com.example.egobook.ui.home.Ink
import com.example.egobook.ui.home.LevelType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class UserStateTest {
    @ParameterizedTest
    @MethodSource("levelInformation")
    fun `레벨 값에 맞는 레벨 타입을 결정한다`(level: Level, expectedLevelType: LevelType) {
        assertThat(level.type)
            .isEqualTo(expectedLevelType)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 1501])
    fun `올바르지 않은 레벨 값은 예외를 반환한다`(invalidLevelNumber: Int) {
        assertThatThrownBy { Level(invalidLevelNumber) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `잉크 값이 음수라면 예외를 반환한다`() {
       assertThatThrownBy { Ink(-1) }
           .isInstanceOf(java.lang.IllegalStateException::class.java)
    }

    companion object {
        @JvmStatic
        fun levelInformation(): List<Arguments> {
            return listOf(
                Arguments.of(Level(1), LevelType.ONE),
                Arguments.of(Level(99), LevelType.ONE),
                Arguments.of(Level(100), LevelType.TWO),
                Arguments.of(Level(299), LevelType.TWO),
                Arguments.of(Level(300), LevelType.THREE),
                Arguments.of(Level(499), LevelType.THREE),
                Arguments.of(Level(500), LevelType.FOUR),
                Arguments.of(Level(699), LevelType.FOUR),
                Arguments.of(Level(700), LevelType.FIVE),
                Arguments.of(Level(999), LevelType.FIVE),
                Arguments.of(Level(1000), LevelType.SIX),
                Arguments.of(Level(1299), LevelType.SIX),
                Arguments.of(Level(1300), LevelType.SEVEN),
                Arguments.of(Level(1499), LevelType.SEVEN),
                Arguments.of(Level(1500), LevelType.EIGHT),
            )
        }
    }
}
