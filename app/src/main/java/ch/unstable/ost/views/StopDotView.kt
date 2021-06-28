package ch.unstable.ost.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ch.unstable.ost.R

class StopDotView @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0,
                                            defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val circlePaint: Paint
    private val circlePixelWidth: Int
    private val linePaint: Paint
    var lineMode: LineMode = LineMode.BOTH
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x = width / 2f
        var startY = 0f
        var stopY = height.toFloat()
        when (lineMode) {
            LineMode.TOP -> startY = height / 2f
            LineMode.BOTH -> {
            }
            LineMode.BOTTOM -> stopY = height / 2f
        }
        canvas.drawLine(x, startY, x, stopY, linePaint)
        canvas.drawCircle(x, height / 2f, circlePixelWidth / 2f, circlePaint)
    }

    enum class LineMode {
        TOP, BOTTOM, BOTH
    }

    init {
        val style = context.theme.obtainStyledAttributes(attrs, R.styleable.StopDotView, defStyleAttr, 0)
        val lineColor = style.getColor(R.styleable.StopDotView_lineColor, -0x1000000)
        val lineWidthPx = style.getDimensionPixelSize(R.styleable.StopDotView_lineWidth, 1)
        val type = style.getInt(R.styleable.StopDotView_lineMode, 0)
        circlePixelWidth = style.getDimensionPixelSize(R.styleable.StopDotView_circleWidth, 2)
        val circleColor = style.getColor(R.styleable.StopDotView_circleColor, -0x1)
        lineMode = when (type) {
            1 -> LineMode.TOP
            2 -> LineMode.BOTTOM
            else -> LineMode.BOTH
        }
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.color = lineColor
        linePaint.strokeWidth = lineWidthPx.toFloat()
        linePaint.style = Paint.Style.STROKE // set to STOKE
        linePaint.strokeJoin = Paint.Join.ROUND // set the join to round you want
        linePaint.strokeCap = Paint.Cap.ROUND // set the paint cap to round too
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePaint.color = circleColor
        circlePaint.style = Paint.Style.FILL
        style.recycle()
    }
}