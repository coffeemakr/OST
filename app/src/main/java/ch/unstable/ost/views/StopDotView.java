package ch.unstable.ost.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ch.unstable.ost.R;


public class StopDotView extends View {

    private final Paint mCirclePaint;
    private Type mType;
    private final int mCirclePixelWidth;

    public void setLineMode(Type type) {
        mType = type;
        invalidate();
    }

    public enum Type {
        TOP,
        BOTTOM,
        BOTH
    }

    private final Paint mLinePaint;
    public StopDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StopDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray style = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StopDotView, defStyleAttr, 0);
        int lineColor = style.getColor(R.styleable.StopDotView_lineColor, 0xff000000);
        int lineWidthPx = style.getDimensionPixelSize(R.styleable.StopDotView_lineWidth, 1);
        int type = style.getInt(R.styleable.StopDotView_lineMode, 0);
        mCirclePixelWidth = style.getDimensionPixelSize(R.styleable.StopDotView_circleWidth, 2);
        int circleColor = style.getColor(R.styleable.StopDotView_circleColor, 0xffffffff);
        switch (type) {
            case 1:
                mType = Type.TOP;
                break;
            case 2:
                mType = Type.BOTTOM;
                break;
            default:
                mType = Type.BOTH;
        }
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(lineColor);
        mLinePaint.setStrokeWidth(lineWidthPx);
        mLinePaint.setStyle(Paint.Style.STROKE);       // set to STOKE
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        style.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = getWidth() / 2f;
        float startY = 0;
        float stopY = getHeight();
        switch (mType) {
            case TOP:
                startY = getHeight() / 2;
                break;
            case BOTH:
                break;
            case BOTTOM:
                stopY = getHeight() / 2;
                break;
        }
        canvas.drawLine(x, startY, x, stopY, mLinePaint);
        canvas.drawCircle(x, getHeight() / 2, mCirclePixelWidth / 2, mCirclePaint);
    }

    protected Paint getLinePaint() {
        return mLinePaint;
    }

    protected Paint getCirclePaint() {
        return mCirclePaint;
    }

    protected int getCirclePixelWidth() {
        return mCirclePixelWidth;
    }
}
