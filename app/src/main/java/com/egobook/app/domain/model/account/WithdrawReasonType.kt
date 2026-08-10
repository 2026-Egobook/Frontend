package com.egobook.app.domain.model.account

/**
 * 회원 탈퇴 사유 유형
 *
 * [value]는 서버(`POST /users/withdraw/reason`)에 전달하는 ENUM 값이다.
 */
enum class WithdrawReasonType(val value: String) {
    NOT_USED_OFTEN("NOT_USED_OFTEN"),
    LACK_OF_CONTENT("LACK_OF_CONTENT"),
    INCONVENIENT_UI("INCONVENIENT_UI"),
    DIFFICULT_TO_COLLECT_INK("DIFFICULT_TO_COLLECT_INK"),
    OTHER("OTHER");

    companion object {
        /** 기타 선택 시 입력 가능한 상세 사유 최대 길이 */
        const val MAX_TEXT_LENGTH = 500
    }
}
