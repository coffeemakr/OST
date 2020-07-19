package ch.unstable.ost.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import ch.unstable.ost.R
import ch.unstable.ost.views.lists.connection.TravelDurations

class ConnectionLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var walkingLinePaint: Paint
    private val waitingLinePaint: Paint
    private val travellingLinePaint: Paint
    private var lengths: List<TravelDurations> = emptyList()
    private var totalLength: Long = 1
    fun setLengths(lengths: List<TravelDurations>) {
        this.lengths = lengths
        totalLength = 0
        for (duration in lengths) {
            totalLength += duration.duration.toLong()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val middleY = height / 2
        var x = 0f
        val width = width.toLong()
        //Log.d(TAG, "Lengths: " + Arrays.toString(lengths) + " totalLength: " + totalLength);
        for ((duration, type) in lengths) {
            val paint = when(type) {
                TravelDurations.Type.TRAVEL -> travellingLinePaint
                TravelDurations.Type.WAIT -> waitingLinePaint
                TravelDurations.Type.WALK -> walkingLinePaint
            }
            val lineWidth = (width.toFloat() * duration.toFloat()) / totalLength.toFloat()
            canvas.drawLine(x, middleY.toFloat(), x + lineWidth, middleY.toFloat(), paint)
            waitingLinePaint.strokeWidth
            x += lineWidth
        }
    }

    companion object {
        private const val TAG = "ConnectionLineView"
    }

    init {
        val dm = resources.displayMetrics
        val dpSizeWaiting = 2
        val waitingStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSizeWaiting.toFloat(), dm)
        waitingLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = waitingStrokeWidth
            color = ResourcesCompat.getColor(context.resources, R.color.colorConnectionLineWaiting, context.theme)
            strokeCap = Paint.Cap.ROUND
        }
        travellingLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            val dpSizeTravelling = 8.toFloat()
            strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSizeTravelling, dm)
            color = ResourcesCompat.getColor(context.resources, R.color.colorConnectionLineTravelling, context.theme)
        }
        walkingLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            val dpSizeTravelling = 8.toFloat()
            strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSizeTravelling, dm)
            color = ResourcesCompat.getColor(context.resources, R.color.colorConnectionLineWalking, context.theme)
        }
    }
}