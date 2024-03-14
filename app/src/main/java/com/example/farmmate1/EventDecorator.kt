package com.example.farmmate1

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.DayViewDecorator

class EventDecorator(private val context: Context, private val checkedItems: Set<String>, private val date: CalendarDay) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return date == day
    }

    override fun decorate(view: DayViewFacade) {
        val multiDots = mutableListOf<Int>()

        if ("water" in checkedItems) {
            multiDots.add(Color.parseColor("#2196F3"))
        }
        if ("fertilizer" in checkedItems) {
            multiDots.add(Color.parseColor("#4CAF50"))
        }
        if ("pesticide" in checkedItems) {
            multiDots.add(Color.parseColor("#FFC107"))
        }

        view.addSpan(CustomMultipleDotSpan(colors = multiDots.toIntArray()))
    }
}


//class EventDecorator(private val context: Context, private val checkedItems: Set<String>, private val dates: Set<CalendarDay>) : DayViewDecorator {
//    private val waterColor = Color.parseColor("#2196F3") // 물 점의 색상
//    private val fertilizerColor = Color.parseColor("#4CAF50") // 비료 점의 색상
//    private val pesticideColor = Color.parseColor("#FFC107") // 농약 점의 색상
//
//    override fun shouldDecorate(day: CalendarDay): Boolean {
//        return dates.contains(day)
//    }
//
//    override fun decorate(view: DayViewFacade) {
//        for (date in dates) {
//            val multiDots = mutableListOf<Int>()
//
//            if ("water" in checkedItems && CalendarDay.from(date.year, date.month, date.day) == date) {
//                multiDots.add(waterColor)
//            }
//            if ("fertilizer" in checkedItems && CalendarDay.from(date.year, date.month, date.day) == date) {
//                multiDots.add(fertilizerColor)
//            }
//            if ("pesticide" in checkedItems && CalendarDay.from(date.year, date.month, date.day) == date) {
//                multiDots.add(pesticideColor)
//            }
//
//            Log.d("EventDecorator", "Selected Dates: $dates")
//            Log.d("EventDecorator", "Checked Items: $checkedItems")
//            Log.d("EventDecorator", "MultiDots: $multiDots")
//
//            view.addSpan(CustomMultipleDotSpan(colors = multiDots.toIntArray()))
//        }
//    }
//}