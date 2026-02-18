package com.egobook.app.ui.diary.mapper

import com.egobook.app.R
import com.egobook.app.domain.model.diary.entity.Diary
import com.egobook.app.domain.model.diary.entity.DiaryRewards
import com.egobook.app.domain.model.diary.entity.DiaryType
import com.egobook.app.domain.model.diary.entity.RewardType
import java.time.LocalDate
import java.time.LocalDateTime
import com.egobook.app.ui.diary.model.ToastMessage

/**
 * Domain 모델과 UI 레이어 간의 데이터 변환을 담당하는 매퍼
 * 순수하게 데이터 변환만 담당하며, UI 리소스(이미지, 색상 등)는 UI 레이어에서 처리
 */
object DiaryEntityMapper {

    // ========== Domain Entity -> UI ==========

    /**
     * Domain DiaryType Set -> UI displayTypes Set
     */
    fun domainToUiDisplayTypes(types: Set<DiaryType>): Set<String> {
        return types.map { it.displayType }.toSet()
    }
    
    /**
     * Domain DiaryType -> UI displayType("감정", "고민", "칭찬", "감사")
     */
    fun domainToUiDisplayType(diaryType: DiaryType): String {
        return diaryType.displayType
    }

    /**
     * Domain RewardType -> UI displayType("잉크", "감정조절", "긍정사고")
     */
    fun domainRewardTypeToUiDisplayType(rewardType: RewardType): String {
        return rewardType.displayType
    }

    fun domainRewardTypesToUiDisplayTypes(types: List<RewardType>): List<String> {
        return types.map { it.displayType }
    }

    fun createToastMessages(rewards: DiaryRewards): List<ToastMessage> {
        // 작성한 일기 타입 중 첫 번째를 대표로 사용
        val primaryDiaryType = rewards.type.firstOrNull() ?: DiaryType.EMOTION

        // 일기 타입에 따른 이미지 결정
        val rewardImageRes = getRewardImageResForDiaryType(primaryDiaryType)

        return rewards.rewards.map { reward ->
            when (reward.rewardType) {
                RewardType.INK -> ToastMessage(
                    rewardType = "INK",
                    message = "잉크를 ${reward.amount} 획득했어요",
                    amount = reward.amount,
                    imageRes = R.drawable.ink_icon  // 잉크는 기본 잉크 아이콘
                )
                else -> ToastMessage(
                    rewardType = "REWARD",
                    message = createRewardMessage(primaryDiaryType, reward.rewardType),
                    amount = reward.amount,
                    imageRes = rewardImageRes  // 일기 타입에 따른 이미지
                )
            }
        }
    }

    /**
     * 일기 타입에 따른 리워드 토스트 이미지 결정
     * - 칭찬, 감사 → ic_radar_sun
     * - 고민 → ic_radar_star
     * - 감정 → ic_radar_sun (기본값)
     */
    private fun getRewardImageResForDiaryType(diaryType: DiaryType): Int {
        return when (diaryType) {
            DiaryType.PRAISE, DiaryType.GRATITUDE -> R.drawable.ic_radar_sun
            DiaryType.CONCERN -> R.drawable.ic_radar_star
            DiaryType.EMOTION -> R.drawable.ic_radar_sun
        }
    }

    /**
     * STAT 보상 메시지 생성
     * "{일기타입} 일기를 작성하여\n{보상타입}[이/가] 상승했어요"
     * - 받침 있음(감정조절) → "이"
     * - 받침 없음(긍정사고) → "가"
     */
    private fun createRewardMessage(
        diaryType: DiaryType,
        rewardType: RewardType
    ): String {
        val diaryDisplay = diaryType.displayType      // "감정", "고민", "칭찬", "감사"
        val rewardDisplay = rewardType.displayType    // "감정조절", "긍정사고"
        
        // 받침 여부에 따라 조사 결정
        val particle = if (hasFinalConsonant(rewardDisplay)) "이" else "가"

        return "${diaryDisplay} 일기를 작성하여\n${rewardDisplay}${particle} 상승했어요"
    }
    
    /**
     * 한글 받침(종성) 여부 확인
     */
    private fun hasFinalConsonant(text: String): Boolean {
        if (text.isEmpty()) return false
        val lastChar = text.last()
        // 한글 완성형 범위: 0xAC00 ~ 0xD7A3
        // 받침 있음: (code - 0xAC00) % 28 != 0
        return lastChar.code in 0xAC00..0xD7A3 && (lastChar.code - 0xAC00) % 28 != 0
    }

    // ========== UI -> Domain Entity ==========
    
    /**
     * UI displayTypes Set -> Domain DiaryType Set
     */
    fun uiDisplayTypesToDomain(displayTypes: Set<String>): Set<DiaryType> {
        return displayTypes.mapNotNull { displayType ->
            try {
                DiaryType.fromDisplayType(displayType)
            } catch (e: IllegalArgumentException) {
                null // 알 수 없는 타입은 무시
            }
        }.toSet()
    }

    /**
     * UI displayType("감정", "고민", "칭찬", "감사") -> Domain DiaryType
     */
    fun uiDisplayTypeToDomain(displayType: String): DiaryType {
        return DiaryType.fromDisplayType(displayType)
    }

    /**
     * UI 년원일 -> Domain Entity LocalDate
     */
    fun uiYearMonthDateToDomain(year: Int, month: Int, date: Int): LocalDate {
        return LocalDate.of(year, month, date)
    }

    /**
     * UI 상태를 Domain Diary 엔티티로 변환 (새 일기 생성용, 일기 수정에도 사용가능)
     * @param selectedTypes UI displayType Set (예: ["감정", "고민"])
     * @param content 일기 내용
     * @param emotionLevel 감정 레벨 (1~5)
     * @return 새로 생성할 Diary 엔티티 (diaryId와 createdAt는 임시값)
     */
    //dateTime 하나로 date, writtenAt, createdAt을 모두 설정함 -> 문제점 발생.
    fun createNewDiary(
        selectedTypes: Set<String>,
        content: String,
        emotionLevel: Int?,
        date: LocalDate, //선택된 날짜
        writtenAt: LocalDateTime // 실제 작성 시간
    ): Diary {
        // UI displayType을 Domain DiaryType으로 변환
        val diaryTypes = uiDisplayTypesToDomain(selectedTypes)
        
        // 감정 타입이 선택되지 않았으면 emotionLevel은 null
        val finalEmotionLevel = if (selectedTypes.contains("감정")) {
            emotionLevel
        } else {
            null
        }
        
        return Diary(
            diaryId = 0L, // 새 일기는 임시 ID (서버가 생성), 임시 삽입.
            date = date,
            writtenAt = writtenAt, // 서버가 실제 값으로 대체. 임시 삽입
            types = diaryTypes,
            emotionLevel = finalEmotionLevel,
            content = content,
            createdAt = writtenAt // 서버가 실제 값으로 대체. 임시 삽입
        )
    }

    fun createUpdatedDiary(
        diaryId: Long,
        selectedTypes: Set<String>,
        content: String,
        emotionLevel: Int?,
        writtenAt: LocalDateTime
    ): Diary {
        // UI displayType을 Domain DiaryType으로 변환
        val diaryTypes = uiDisplayTypesToDomain(selectedTypes)

        // 감정 타입이 선택되지 않았으면 emotionLevel은 null
        val finalEmotionLevel = if (selectedTypes.contains("감정")) {
            emotionLevel
        } else {
            null
        }

        return Diary(
            diaryId = diaryId,
            types = diaryTypes,
            emotionLevel = finalEmotionLevel,
            content = content,

            //어차피 DiaryMapper에서 걸러지는 값들. 그냥 임시 삽입
            createdAt = writtenAt,
            date = writtenAt.toLocalDate(),
            writtenAt = writtenAt
        )
    }
}
