package com.example.farmmate1

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class CustomMultipleDotSpan(private val radius: Float = DEFAULT_RADIUS, private val colors: IntArray) : LineBackgroundSpan {

    companion object {
        private const val DEFAULT_RADIUS = 5f
    }

    override fun drawBackground(
        canvas: Canvas, paint: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        charSequence: CharSequence, start: Int, end: Int, lineNum: Int
    ) {
        val total = if (colors.size > 3) 3 else colors.size
        var leftMost = (total - 1) * -12

        for (i in 0 until total) {
            val oldColor = paint.color
            if (colors[i] != 0) {
                paint.color = colors[i]
            }
            canvas.drawCircle(((left + right) / 2 - leftMost).toFloat(), bottom + radius, radius, paint)
            paint.color = oldColor
            leftMost += 24
        }
    }
}
