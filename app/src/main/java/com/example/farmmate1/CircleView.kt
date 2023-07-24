import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val circlePaint: Paint
    private var hasEvent = false

    init {
        circlePaint = Paint()
        circlePaint.color = Color.RED
        circlePaint.style = Paint.Style.FILL
    }

    fun setHasEvent(hasEvent: Boolean) {
        this.hasEvent = hasEvent
        invalidate() // View를 다시 그리도록 갱신
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hasEvent) {
            val cx = width / 2f
            val cy = height / 2f
            val radius = Math.min(cx, cy) - 10
            canvas.drawCircle(cx, cy, radius, circlePaint)
        }
    }
}
