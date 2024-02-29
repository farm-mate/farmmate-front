package com.example.farmmate1

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.*
import java.util.*

class OneDayDecorator : DayViewDecorator {
    private var date: CalendarDay = CalendarDay.today()

    constructor() {}

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(StyleSpan(android.graphics.Typeface.BOLD))
        //view.addSpan(RelativeSizeSpan(1.4f))
        view.addSpan(ForegroundColorSpan(Color.MAGENTA))
    }

    fun setDate(date: Date) {
        this.date = CalendarDay.from(date)
    }
}
