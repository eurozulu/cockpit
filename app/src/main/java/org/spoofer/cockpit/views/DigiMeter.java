package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

public class DigiMeter extends BaseLevelView {

    @ColorInt
    private static final int DEFAULT_COLOUR = Color.BLUE;
    private static final int DEFAULT_SEGMENT_COUNT = 10;

    private int zeroOffset;
    private boolean isHorizontal;
    private boolean isInverted;
    private boolean showLimits;

    private int segmentCount;

    @ColorInt
    private int color;
    @ColorInt
    private int colorNegative;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public DigiMeter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigiMeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DigiMeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setSaveEnabled(true);

        TypedArray taSegment = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DigiMeter,
                defStyleAttr, defStyleRes);
        try {
            setZeroOffset(taSegment.getInt(R.styleable.DigiMeter_zeroOffset, 0));
            setSegmentCount(taSegment.getInt(R.styleable.DigiMeter_segmentCount, DEFAULT_SEGMENT_COUNT));

            setColor(taSegment.getColor(R.styleable.DigiMeter_colour, DEFAULT_COLOUR));
            setColorNegative(taSegment.getColor(R.styleable.DigiMeter_colourNegative, DEFAULT_COLOUR));

            isHorizontal = taSegment.getBoolean(R.styleable.DigiMeter_orientation, false);
            isInverted = taSegment.getBoolean(R.styleable.DigiMeter_invertAxes, false);
            showLimits = taSegment.getBoolean(R.styleable.DigiMeter_showLimits, false);

        } finally {
            taSegment.recycle();
        }
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public void setSegmentCount(int segmentCount) {
        this.segmentCount = segmentCount;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorNegative() {
        return colorNegative;
    }

    public void setColorNegative(int colorNegative) {
        this.colorNegative = colorNegative;
    }

    public int getZeroOffset() {
        return zeroOffset;
    }

    public void setZeroOffset(int zeroOffset) {
        this.zeroOffset = zeroOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isHorizontal)
            drawHorizontal(canvas);
        else
            drawVertical(canvas);
    }


    private void drawHorizontal(Canvas canvas) {
        int width = getWidth() - (getPaddingStart() + getPaddingEnd());
        int start = getPaddingTop();
        int end = getBottom() - getPaddingBottom();

        float scaleX = getLevel() / getRange();
        float val = width * scaleX;

        int segmentWidth = width / segmentCount;
        int segsToPaint = (int) (Math.abs(val) / segmentWidth);

        paint.setStrokeWidth(segmentWidth * 0.8f);
        int inc = segmentWidth;
        paint.setColor(colorNegative);

        if (val >= 0) {
            paint.setColor(color);
            inc = -inc;
        }
        if (isInverted)
            inc = -inc;

        int x = (width / 2) + zeroOffset;
        for (int i = 0; i < segsToPaint; i++) {
            canvas.drawLine(x, start, x, end, paint);
            x += inc;
        }

        if (showLimits) {
            paint.setColor(color);
            segsToPaint = (int) (Math.abs(autoRange.getUpperLimit()) / segmentWidth);
            x = segsToPaint * segmentWidth;
            canvas.drawLine(x, start, x, end, paint);

            paint.setColor(colorNegative);
            segsToPaint = (int) (Math.abs(autoRange.getLowerLimit()) / segmentWidth);
            x = segsToPaint * segmentWidth;
            canvas.drawLine(x, start, x, end, paint);
        }
    }

    private void drawVertical(Canvas canvas) {
        int height = getHeight() - (getPaddingTop() + getPaddingBottom());
        int start = getPaddingLeft();
        int end = getRight() - getPaddingRight();

        float scaleY = getLevel() / getRange();
        float val = height * scaleY;

        int segmentHeight = height / segmentCount;
        int segsToPaint = (int) (Math.abs(val) / segmentHeight);

        paint.setStrokeWidth(segmentHeight * 0.8f);
        int inc = segmentHeight;
        paint.setColor(colorNegative);

        if (val >= 0) {
            paint.setColor(color);
            inc = -inc;
        }

        int y = (height / 2) - zeroOffset;
        for (int i = 0; i < segsToPaint; i++) {
            canvas.drawLine(start, y, end, y, paint);
            y += inc;
        }
    }


}
