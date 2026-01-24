package com.egobook.app.domain.model

data class Statistics(
    val emotions: Map<EmotionType, MonthData>
)

data class MonthData(val months: List<DailyData>) {
    init {
        require(months.size == 12) {
            "months는 1월부터 12월까지의 데이터를 담고 있기에, 리스트의 크기는 반드시 12이어야 합니다."
        }
    }
    // 해당 감정의 1년 전체 합계
    val totalCnt: Int get() = months.sumOf { it.totalCnt }
}

data class DailyData(val days: List<TimeData>) {
    init {
        require(days.size == 7) {
            "days는 월요일(한달치)부터 일요일(한달치)까지의 데이터를 담고 있기에, 리스트의 크기는 반드시 7이어야 합니다."
        }
    }
    // 해당 달의 전체 합계
    val totalCnt: Int get() = days.sumOf { it.totalCnt }
}

data class TimeData(val hours: List<Int>) {
    init {
        require(hours.size == 24) {
            "hours는 0시부터 23시의 데이터를 담고 있기에, 리스트의 크기는 반드시 24이어야 합니다."
        }
    }
    // 해당 요일의 전체 합계
    val totalCnt: Int get() = hours.sumOf { it }
}

