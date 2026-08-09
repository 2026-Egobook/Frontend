package com.egobook.app.analytics

/**
 * Firebase 예약 이벤트(login, sign_up 등)는 [com.google.firebase.analytics.FirebaseAnalytics.Event]를
 * 그대로 사용하고, 여기에는 커스텀 이벤트명만 정의한다.
 */
object AnalyticsEvent {

    // A. 로그인/가입
    const val ONBOARDING_COMPLETE = "onboarding_complete"

    // B. 계정/상점/알림
    const val ACCOUNT_LINK_START = "account_link_start"
    const val ACCOUNT_LINK_COMPLETE = "account_link_complete"
    const val ACCOUNT_WITHDRAW_REQUEST = "account_withdraw_request"
    const val ACCOUNT_WITHDRAW_CONFIRM = "account_withdraw_confirm"
    const val SHOP_OPEN = "shop_open"
    const val SHOP_EXIT = "shop_exit"
    const val ITEM_PURCHASE = "item_purchase"
    const val ITEM_EQUIP = "item_equip"
    const val NOTIFICATION_OPEN = "notification_open"
    const val NOTIFICATION_TOGGLE = "notification_toggle"

    // C. 메인페이지
    const val NICKNAME_CHANGE = "nickname_change"
    const val PSYCH_KNOWLEDGE_OPEN = "psych_knowledge_open"
    const val PSYCH_KNOWLEDGE_SAVE = "psych_knowledge_save"
    const val PSYCH_KNOWLEDGE_UNSAVE = "psych_knowledge_unsave"
    const val LEVEL_UP = "level_up"
    const val AD_REWARD_WATCH = "ad_reward_watch"

    // D. 감정일기
    const val DIARY_WRITE = "diary_write"
    const val DIARY_EDIT = "diary_edit"
    const val DIARY_DELETE = "diary_delete"
    const val DIARY_EXPORT = "diary_export"
    const val DIARY_CALENDAR_VIEW = "diary_calendar_view"

    // E. 에고룸
    const val PRAISE_LETTER_VIEW = "praise_letter_view"
    const val PRAISE_LETTER_TOGGLE = "praise_letter_toggle"
    const val WEEKLY_REPORT_UNLOCK = "weekly_report_unlock"
    const val WEEKLY_REPORT_TONE_SELECT = "weekly_report_tone_select"
    const val WEEKLY_REPORT_TOGGLE = "weekly_report_toggle"
    const val STATS_VIEW = "stats_view"

    // F. 광장/친구
    const val LETTER_RECEIVE = "letter_receive"
    const val LETTER_REPLY_SEND = "letter_reply_send"
    const val LETTER_GIVE_UP = "letter_give_up"
    const val LETTER_REPORT = "letter_report"
    const val LETTER_SEND = "letter_send"
    const val LETTER_DELETE = "letter_delete"
    const val QUESTION_ANSWER_WRITE = "question_answer_write"
    const val QUESTION_VIEW_ALL = "question_view_all"
    const val QUESTION_ANSWER_EDIT = "question_answer_edit"
    const val QUESTION_ANSWER_DELETE = "question_answer_delete"
    const val FRIEND_REQUEST_SEND = "friend_request_send"
    const val FRIEND_REQUEST_ACCEPT = "friend_request_accept"
    const val FRIEND_REQUEST_REJECT = "friend_request_reject"
    const val FRIEND_DELETE = "friend_delete"

    // 스펙 표에는 없지만 기존 신고 기능과 대응되어 추가한 이벤트
    const val QUESTION_ANSWER_REPORT = "question_answer_report"
}
