package com.egobook.app.analytics

/**
 * Firebase 예약 파라미터(method 등)는 [com.google.firebase.analytics.FirebaseAnalytics.Param]을
 * 그대로 사용하고, 여기에는 커스텀 파라미터명만 정의한다.
 */
object AnalyticsParam {
    const val IS_FIRST_TODAY = "is_first_today"
    const val STAT_TYPE = "stat_type"
    const val NEW_LEVEL = "new_level"
    const val AD_COUNT_TODAY = "ad_count_today"
    const val MOOD_LEVEL = "mood_level"
    const val HAS_WORRY = "has_worry"
    const val HAS_GRATITUDE = "has_gratitude"
    const val HAS_PRAISE = "has_praise"
    const val FORMAT = "format"
    const val DATE_RANGE_DAYS = "date_range_days"
    const val ENABLED = "enabled"
    const val TONE = "tone"
    const val FROM_TYPE = "from_type"
    const val REASON = "reason"
    const val TARGET_TYPE = "target_type"
    const val VISIBILITY = "visibility"
    const val ITEM_ID = "item_id"
    const val ITEM_TYPE = "item_type"
    const val PRICE = "price"
    const val NOTIFICATION_TYPE = "notification_type"
}
