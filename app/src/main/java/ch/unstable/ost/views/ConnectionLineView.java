package ch.unstable.ost.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.Arrays;

import ch.unstable.ost.R;


public class ConnectionLineView extends View {

    private static final String TAG = "ConnectionLineView";
    private final Paint mWaitingLinePaint;
    private final Paint mTravellingLinePaint;
    private int[] lengths = new int[]{1};
    private int totalLength = 1;

    public ConnectionLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ConnectionLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics dm = getResources().getDisplayMetrics();


        int dpSizeWaiting = 2;
        float waitingStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSizeWaiting, dm);
        mWaitingLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWaitingLinePaint.setStyle(Paint.Style.STROKE);
        mWaitingLinePaint.setStrokeWidth(waitingStrokeWidth);
        int line_color = ResourcesCompat.getColor(context.getResources(), R.color.colorConnectionLineWaiting, context.getTheme());
        mWaitingLinePaint.setColor(line_color);
        mWaitingLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mTravellingLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTravellingLinePaint.setStyle(Paint.Style.STROKE);
        mTravellingLinePaint.setStrokeCap(Paint.Cap.ROUND);

        int dpSizeTravelling = 8;
        float travellingStroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSizeTravelling, dm);
        int line2Color = ResourcesCompat.getColor(context.getResources(), R.color.colorConnectionLineTravelling, context.getTheme());
        mTravellingLinePaint.setStrokeWidth(travellingStroke);
        mTravellingLinePaint.setColor(line2Color);
    }


    public void setLengths(int[] lengths) {
        this.lengths = Arrays.copyOf(lengths, lengths.length);
        this.totalLength = 0;
        for (int length : lengths) {
            this.totalLength += length;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int middleY = getHeight() / 2;
        float x = 0;
        boolean isTravelling = true;
        long width = getWidth();
        //Log.d(TAG, "Lengths: " + Arrays.toString(lengths) + " totalLength: " + totalLength);
        for (int length : lengths) {
            Paint paint;
            if (isTravelling) {
                paint = mTravellingLinePaint;
            } else {
                paint = mWaitingLinePaint;
            }
            isTravelling = !isTravelling;
            float lineWidth = ((float) (width * length)) / totalLength;
            canvas.drawLine(x, middleY, x + lineWidth, middleY, paint);
            mWaitingLinePaint.getStrokeWidth();
            x += lineWidth;
        }
    }
}
