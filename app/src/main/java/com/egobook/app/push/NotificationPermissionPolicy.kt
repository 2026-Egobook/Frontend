package com.egobook.app.push

import android.os.Build

enum class NotificationPermissionDecision {
    REQUEST,
    SKIP,
}

/**
 * 알림 권한 요청 여부를 판단하는 정책.
 *
 * 안드로이드 13부터 두 번 거절하면 시스템이 권한 다이얼로그를 더 이상 띄우지 않으므로,
 * 화면 진입마다 반복 요청하지 않고 최초 1회만 요청한다.
 * 이미 요청한 뒤 거절한 사용자는 시스템 설정으로 유도한다.
 */
object NotificationPermissionPolicy {
    fun decide(
        sdkInt: Int,
        isGranted: Boolean,
        hasRequestedBefore: Boolean,
    ): NotificationPermissionDecision {
        if (sdkInt < Build.VERSION_CODES.TIRAMISU) return NotificationPermissionDecision.SKIP
        if (isGranted) return NotificationPermissionDecision.SKIP
        if (hasRequestedBefore) return NotificationPermissionDecision.SKIP

        return NotificationPermissionDecision.REQUEST
    }
}
