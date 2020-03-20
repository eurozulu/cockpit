package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

import java.io.Serializable;

public class DigiMeter extends View implements ValueView {

    @ColorInt
    private static final int DEFAULT_COLOUR = Color.BLUE;
    private static final int DEFAULT_SEGMENT_COUNT = 10;

    private int range;
    private Autorange autoRange;
    private int zeroOffset;

    private boolean isHorizontal;
    private boolean isInverted;

    private int segmentCount;

    @ColorInt
    private int color;
    @ColorInt
    private int colorNegative;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Object lock = new Object();
    private transient int value;

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
            int minHeight = taSegment.getDimensionPixelSize(R.styleable.DigiMeter_minHeight, 0);
            setMinimumHeight(minHeight);
            int minWidth = taSegment.getDimensionPixelSize(R.styleable.DigiMeter_minWidth, 0);
            setMinimumWidth(minWidth);

            segmentCount = taSegment.getInt(R.styleable.DigiMeter_segmentCount, DEFAULT_SEGMENT_COUNT);
            range = taSegment.getInt(R.styleable.DigiMeter_range, 0);
            if (range == 0) {
                autoRange = new Autorange();
            }

            value = taSegment.getInt(R.styleable.DigiMeter_value, 0);
            zeroOffset = taSegment.getInt(R.styleable.DigiMeter_zeroOffset, 0);

            isHorizontal = taSegment.getBoolean(R.styleable.DigiMeter_orientation, false);
            isInverted = taSegment.getBoolean(R.styleable.DigiMeter_invertAxes, false);

            color = taSegment.getColor(R.styleable.DigiMeter_colour, DEFAULT_COLOUR);
            colorNegative = taSegment.getColor(R.styleable.DigiMeter_colourNegative, DEFAULT_COLOUR);

        } finally {
            taSegment.recycle();
        }
    }


    public int getValue() {
        synchronized (lock) {
            return value;
        }
    }

    public void setValue(float value) {
        setValue((int) value);
    }

    public void setValue(int value) {
        synchronized (lock) {
            if (autoRange != null) {
                autoRange.setRange(value);
                range = autoRange.getRangeSize();
            }
            if (this.value != value) {
                this.value = value;
                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float value = getValue();

        if (isHorizontal)
            drawHorizontal(value, canvas);
        else
            drawVertical(value, canvas);
    }


    private void drawHorizontal(float value, Canvas canvas) {
        int width = getWidth() - (getPaddingStart() + getPaddingEnd());
        int start = getPaddingTop();
        int end = getBottom() - getPaddingBottom();

        float scaleX = value / range;
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
    }

    private void drawVertical(float value, Canvas canvas) {
        int height = getHeight() - (getPaddingTop() + getPaddingBottom());
        int start = getPaddingLeft();
        int end = getRight() - getPaddingRight();

        float scaleY = value / range;
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


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        // wrap up superstate in our own state
        SavedState ss = new SavedState(superState);
        ss.value = value;
        ss.range = range;
        ss.autoRange = autoRange;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        value = ss.value;
        range = ss.range;
        autoRange = (Autorange) ss.autoRange;
    }

    private static class SavedState extends BaseSavedState {
        int value;
        int range;
        Serializable autoRange;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
            autoRange = in.readSerializable();
            range = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
            out.writeSerializable(autoRange);
            out.writeInt(range);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<DigiMeter.SavedState>() {
            public DigiMeter.SavedState createFromParcel(Parcel in) {
                return new DigiMeter.SavedState(in);
            }

            public DigiMeter.SavedState[] newArray(int size) {
                return new DigiMeter.SavedState[size];
            }
        };
    }
}
