package com.egobook.app.push

import android.os.Build
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NotificationPermissionPolicyTest {
    @Test
    fun `안드로이드 13 미만에서는 런타임 권한이 없으므로 요청하지 않는다`() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION_CODES.S_V2,
                isGranted = false,
                hasRequestedBefore = false,
            )

        assertThat(decision).isEqualTo(NotificationPermissionDecision.SKIP)
    }

    @Test
    fun `이미 권한이 허용되어 있으면 요청하지 않는다`() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION_CODES.TIRAMISU,
                isGranted = true,
                hasRequestedBefore = false,
            )

        assertThat(decision).isEqualTo(NotificationPermissionDecision.SKIP)
    }

    @Test
    fun `안드로이드 13 이상이고 권한이 없으며 요청한 적이 없으면 요청한다`() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION_CODES.TIRAMISU,
                isGranted = false,
                hasRequestedBefore = false,
            )

        assertThat(decision).isEqualTo(NotificationPermissionDecision.REQUEST)
    }

    @Test
    fun `이전에 요청한 적이 있으면 거절 상태여도 다시 요청하지 않는다`() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION_CODES.TIRAMISU,
                isGranted = false,
                hasRequestedBefore = true,
            )

        assertThat(decision).isEqualTo(NotificationPermissionDecision.SKIP)
    }

    @Test
    fun `요청한 적이 있어도 권한이 허용된 상태면 요청하지 않는다`() {
        val decision =
            NotificationPermissionPolicy.decide(
                sdkInt = Build.VERSION_CODES.TIRAMISU,
                isGranted = true,
                hasRequestedBefore = true,
            )

        assertThat(decision).isEqualTo(NotificationPermissionDecision.SKIP)
    }
}
