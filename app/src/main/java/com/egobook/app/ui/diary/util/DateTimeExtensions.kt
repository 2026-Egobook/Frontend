package com.egobook.app.ui.diary.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * UI에서 시간만 표시할 때 사용하는 포맷터
 *
 * 예) 17:00
 */
private val TIME_FORMATTER =
    DateTimeFormatter.ofPattern("HH:mm")


/**
 * UI에서 날짜와 시간을 함께 표시할 때 사용하는 포맷터
 *
 * 예) 2025.12.25 17:32
 */
private val DATE_TIME_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")


/**
 * UI에서 날짜를 한글 형태로 표시할 때 사용하는 포맷터 (LocalDate 전용)
 *
 * 예) 2025년 12월 25일
 */
private val DATE_KOREAN_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")


/**
 * [LocalDateTime]을 UI에서 사용하기 위한 시간 문자열로 변환한다.
 *
 * @return HH:mm 형식의 시간 문자열
 *
 * 예) 17:00
 */
fun LocalDateTime.toTimeString(): String =
    this.format(TIME_FORMATTER)


/**
 * [LocalDateTime]을 UI에서 사용하기 위한 날짜+시간 문자열로 변환한다.
 *
 * @return yyyy.MM.dd HH:mm 형식의 날짜+시간 문자열
 *
 * 예) 2025.12.25 17:32
 */
fun LocalDateTime.toDateTimeString(): String =
    this.format(DATE_TIME_FORMATTER)


/**
 * [LocalDate]를 UI에서 사용하기 위한 한글 날짜 문자열로 변환한다.
 *
 * @return yyyy년 MM월 dd일 형식의 날짜 문자열
 *
 * 예) 2025년 12월 25일
 */
fun LocalDate.toKoreanDateString(): String =
    this.format(DATE_KOREAN_FORMATTER)
