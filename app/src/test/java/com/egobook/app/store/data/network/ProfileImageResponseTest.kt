package com.egobook.app.store.data.network

import com.google.gson.JsonParser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.Retrofit

class ProfileImageResponseTest {
    @ParameterizedTest
    @ValueSource(booleans = [false, true])
    fun `confirm keeps nullable profile result for reuse`(wrapped: Boolean) = runTest {
        val service = mockk<ShopApiService>()
        val retrofit = mockk<Retrofit> { every { create(ShopApiService::class.java) } returns service }
        val payload = """{"turtleImageUrl":"https://example.invalid/turtle.png","backgroundImageUrl":null}"""
        coEvery { service.confirmProfile() } returns JsonParser().parse(if (wrapped) """{"status":200,"data":$payload}""" else payload).asJsonObject
        val source = RemoteShopDataSource(retrofit)
        source.confirmProfile()
        assertThat(source.latestConfirmedProfile?.turtleImageUrl).isEqualTo("https://example.invalid/turtle.png")
        assertThat(source.latestConfirmedProfile?.backgroundImageUrl).isNull()
    }
}
