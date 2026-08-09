package com.egobook.app.ui.square

import com.egobook.app.data.model.square.friend.FriendResponse
import com.egobook.app.data.model.square.friend.FriendRequestResponse
import com.egobook.app.data.model.square.friend.SearchUserResponse
import com.egobook.app.data.model.square.friend.toDomain
import com.egobook.app.data.model.square.question.UserTodayQuestionAnswerItemResponse
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.ui.square.model.friend.toPresentation
import com.egobook.app.ui.square.model.question.toPresentation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProfileImageMappingTest {
    @Test
    fun `friend profile image urls are preserved through presentation mapping`() {
        val model = FriendResponse(
            id = 1L,
            name = "friend",
            level = 10L,
            turtleImageUrl = TURTLE_IMAGE_URL,
            backgroundImageUrl = BACKGROUND_IMAGE_URL
        ).toDomain().toPresentation()

        assertThat(model.turtleImageUrl).isEqualTo(TURTLE_IMAGE_URL)
        assertThat(model.backgroundImageUrl).isEqualTo(BACKGROUND_IMAGE_URL)
    }

    @Test
    fun `searched user profile image urls are preserved through presentation mapping`() {
        val model = SearchUserResponse(
            userId = 1L,
            nickname = "friend",
            level = 10L,
            turtleImageUrl = TURTLE_IMAGE_URL,
            backgroundImageUrl = BACKGROUND_IMAGE_URL
        ).toDomain().toPresentation()

        assertThat(model.turtleImageUrl).isEqualTo(TURTLE_IMAGE_URL)
        assertThat(model.backgroundImageUrl).isEqualTo(BACKGROUND_IMAGE_URL)
    }

    @Test
    fun `question answer profile image urls are preserved through presentation mapping`() {
        val model = UserTodayQuestionAnswerItemResponse(
            answerId = 1L,
            userId = 2L,
            nickname = "friend",
            content = "answer",
            createdAt = "2026-08-06T00:00:00Z",
            level = 321L,
            turtleImageUrl = TURTLE_IMAGE_URL,
            backgroundImageUrl = BACKGROUND_IMAGE_URL
        ).toDomain().toPresentation()

        assertThat(model.turtleImageUrl).isEqualTo(TURTLE_IMAGE_URL)
        assertThat(model.backgroundImageUrl).isEqualTo(BACKGROUND_IMAGE_URL)
        assertThat(model.level).isEqualTo(321L)
    }

    @Test
    fun `pending friend profile image urls are preserved through presentation mapping`() {
        val model = FriendRequestResponse(
            requestId = 1L,
            userId = 2L,
            nickname = "friend",
            level = 10L,
            turtleImageUrl = TURTLE_IMAGE_URL,
            backgroundImageUrl = BACKGROUND_IMAGE_URL,
            requestedAt = "2026-08-06T00:00:00Z"
        ).toDomain().toPresentation()

        assertThat(model.turtleImageUrl).isEqualTo(TURTLE_IMAGE_URL)
        assertThat(model.backgroundImageUrl).isEqualTo(BACKGROUND_IMAGE_URL)
    }

    @Test
    fun `missing profile image urls remain null through presentation mapping`() {
        val model = FriendResponse(
            id = 1L,
            name = "friend",
            level = 10L
        ).toDomain().toPresentation()

        assertThat(model.turtleImageUrl).isNull()
        assertThat(model.backgroundImageUrl).isNull()
    }

    private companion object {
        const val TURTLE_IMAGE_URL = "https://example.com/turtle.png"
        const val BACKGROUND_IMAGE_URL = "https://example.com/background.png"
    }
}
