package com.example.farmmate1

import android.content.Context
import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.DayViewDecorator

class EventDecorator(private val context: Context, private val checkedItems: Set<String>, private val dates: HashSet<CalendarDay>) : DayViewDecorator {
    private val waterColor = Color.parseColor("#2196F3") // 물 점의 색상
    private val fertilizerColor = Color.parseColor("#4CAF50") // 비료 점의 색상
    private val pesticideColor = Color.parseColor("#FFC107") // 농약 점의 색상

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        val multiDots = mutableListOf<Int>()

        // 물, 비료, 농약 체크 여부에 따라 점 추가
        if ("water" in checkedItems) multiDots.add(waterColor)
        if ("fertilizer" in checkedItems) multiDots.add(fertilizerColor)
        if ("pesticide" in checkedItems) multiDots.add(pesticideColor)

        view.addSpan(CustomMultipleDotSpan(colors = multiDots.toIntArray()))
    }
}