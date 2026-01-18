package com.example.egobook.ui.diary

import android.view.View
import com.example.egobook.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val binding = CalendarDayLayoutBinding.bind(view)
}
