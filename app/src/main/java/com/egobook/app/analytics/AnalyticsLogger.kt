package com.egobook.app.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모든 Firebase Analytics 이벤트 로깅은 이 클래스를 통해서만 나가야 한다.
 * 로깅 실패가 앱 로직에 영향을 주지 않도록 내부에서 예외를 전부 흡수한다.
 */
@Singleton
class AnalyticsLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logEvent(event: String, params: Map<String, Any?> = emptyMap()) {
        try {
            firebaseAnalytics.logEvent(event, params.toBundle())
        } catch (e: Exception) {
            Timber.e(e, "Analytics logEvent failed: $event")
        }
    }

    private fun Map<String, Any?>.toBundle(): Bundle = Bundle().apply {
        for ((key, value) in this@toBundle) {
            when (value) {
                null -> Unit
                is String -> putString(key, value)
                is Int -> putLong(key, value.toLong())
                is Long -> putLong(key, value)
                is Float -> putDouble(key, value.toDouble())
                is Double -> putDouble(key, value)
                else -> putString(key, value.toString())
            }
        }
    }
}
