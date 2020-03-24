package org.spoofer.cockpit.sensorviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;

public class DigiMeter extends View implements SensorView {

    @ColorInt
    private static final int DEFAULT_COLOUR = Color.BLUE;
    private static final int DEFAULT_SEGMENT_COUNT = 10;

    private String sensorName;
    private int valueIndex;

    private float level;
    private final Autorange autorange = new Autorange();

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

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DigiMeter,
                defStyleAttr, defStyleRes);
        try {
            sensorName = ta.getString(R.styleable.Dialview_sensorName);
            valueIndex = ta.getInt(R.styleable.DigiMeter_valueIndex, 0);
            if (valueIndex < 0)
                valueIndex = Math.abs(valueIndex);

            setZeroOffset(ta.getInt(R.styleable.DigiMeter_zeroOffset, 0));
            setSegmentCount(ta.getInt(R.styleable.DigiMeter_segmentCount, DEFAULT_SEGMENT_COUNT));

            setColor(ta.getColor(R.styleable.DigiMeter_colour, DEFAULT_COLOUR));
            setColorNegative(ta.getColor(R.styleable.DigiMeter_colourNegative, DEFAULT_COLOUR));

            isHorizontal = ta.getBoolean(R.styleable.DigiMeter_orientation, false);
            isInverted = ta.getBoolean(R.styleable.DigiMeter_invertAxes, false);
            showLimits = ta.getBoolean(R.styleable.DigiMeter_showLimits, false);

        } finally {
            ta.recycle();
        }
    }

    @Override
    public String getSensorName() {
        return sensorName;
    }

    @Override
    public void update(Event event) {
        if (event == null ||
                event.getValues() == null ||
                event.getValues().length < getValueIndex())
            return;
        setLevel(event.getValues()[getValueIndex()]);
    }

    public int getValueIndex() {
        return valueIndex;
    }


    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        if (level == this.level)
            return;
        autorange.updateRange(level);
        this.level = level;
        invalidate();
    }

    public float getRange() {
        return autorange.getRange();
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
            segsToPaint = (int) (Math.abs(autorange.getUpperLimit()) / segmentWidth);
            x = segsToPaint * segmentWidth;
            canvas.drawLine(x, start, x, end, paint);

            paint.setColor(colorNegative);
            segsToPaint = (int) (Math.abs(autorange.getLowerLimit()) / segmentWidth);
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
