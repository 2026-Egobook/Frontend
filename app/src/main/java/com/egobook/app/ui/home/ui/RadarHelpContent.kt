package com.egobook.app.ui.home.ui

object RadarHelpContent {
    fun buildText(): String = buildString {
        appendLine("항목에 해당하는 일을 수행할 때마다 1칸씩 상승합니다.")
        appendLine("5칸을 모두 채우면 1레벨이 상승합니다.")
        appendLine()
        appendLine("공감성 (하루 한 번)")
        appendLine("답장 편지를 작성하면 올라갑니다")
        appendLine()
        appendLine("자존감")
        appendLine("일간 칭찬서를 확인하면 올라갑니다")
        appendLine()
        appendLine("감정조절 (하루 한 번)")
        appendLine("감정 일기 중 '고민' 항목을 포함한 일기를 작성하면 한 칸 올라갑니다.")
        appendLine()
        appendLine("긍정사고 (하루 한 번)")
        appendLine("감정 일기 중 '감사' 혹은 '칭찬' 항목을 포함한 일기를 작성하면 한 칸 올라갑니다.")
        appendLine()
        appendLine("성실함")
        append("광장 페이지에 있는 질문에 답변을 하면 한 칸 올라갑니다.")
    }
}
