package com.example.farmmate1

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import android.util.Log

class CustomMultipleDotSpan(private val radius: Float = DEFAULT_RADIUS, private val colors: IntArray) : LineBackgroundSpan {

    companion object {
        private const val DEFAULT_RADIUS = 5f
    }

//    override fun drawBackground(
//        canvas: Canvas, paint: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
//        charSequence: CharSequence, start: Int, end: Int, lineNum: Int
//    ) {
//        val total = if (colors.size > 3) 3 else colors.size
//        var leftMost = (total - 1) * -12
//
//        for (i in 0 until total) {
//            val oldColor = paint.color
//            if (colors[i] != 0) {
//                paint.color = colors[i]
//            }
//            canvas.drawCircle(((left + right) / 2 - leftMost).toFloat(), bottom + radius, radius, paint)
//            paint.color = oldColor
//            leftMost += 24
//        }
//    }
override fun drawBackground(
    canvas: Canvas, paint: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
    charSequence: CharSequence, start: Int, end: Int, lineNum: Int
) {
    val total = colors.size
    val centerX = (left + right) / 2 // 가운데 x 좌표

    // 각 점 사이의 간격 계산
    val interval: Float = if (total > 1) {
        if (total % 2 == 0) { // 짝수인 경우
            (right - left - radius * 2) / (total*3.3 - 1).toFloat()
        } else { // 홀수인 경우
            (right - left - radius * 2) / (total*2).toFloat()
        }
    } else {
        0f
    }

    var currentX = centerX - (interval * (total - 1) / 2) // 첫 번째 점의 x 좌표 계산

    for (i in 0 until total) {
        val oldColor = paint.color
        if (colors[i] != 0) {
            paint.color = colors[i]
        }

        // 각 점의 x 좌표 계산
        val x = currentX.toFloat()
        Log.d("CustomMultipleDotSpan", "점 ${i + 1}의 x 좌표: $x")

        canvas.drawCircle(x, bottom + radius, radius, paint) // 점 그리기
        paint.color = oldColor

        currentX += interval.toInt() // 다음 점으로 이동
    }
}




}
