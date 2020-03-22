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
import android.widget.PopupMenu;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

import java.io.Serializable;

public class DigiMeter extends View implements LevelView {

    @ColorInt
    private static final int DEFAULT_COLOUR = Color.BLUE;
    private static final int DEFAULT_SEGMENT_COUNT = 10;

    private transient float level;
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
            setLevel(taSegment.getFloat(R.styleable.DigiMeter_level, 0));
            setRange(taSegment.getInt(R.styleable.DigiMeter_range, 0));

            setZeroOffset(taSegment.getInt(R.styleable.DigiMeter_zeroOffset, 0));
            setSegmentCount(taSegment.getInt(R.styleable.DigiMeter_segmentCount, DEFAULT_SEGMENT_COUNT));

            setColor(taSegment.getColor(R.styleable.DigiMeter_colour, DEFAULT_COLOUR));
            setColorNegative(taSegment.getColor(R.styleable.DigiMeter_colourNegative, DEFAULT_COLOUR));

            isHorizontal = taSegment.getBoolean(R.styleable.DigiMeter_orientation, false);
            isInverted = taSegment.getBoolean(R.styleable.DigiMeter_invertAxes, false);

        } finally {
            taSegment.recycle();
        }

        setOnLongClickListener(v -> {
            boolean isLevelView = v instanceof LevelView;
            if (!isLevelView)
                return false;
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_recalibrate:
                        ((LevelView) v).setRange(0);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
            return true;
        });
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
    public void setRange(float range) {
        autoRange = new Autorange();
        if (range != 0) {
            autoRange.setFixedRange(range);
        }
    }

    @Override
    public float getRange() {
        return autoRange.getRange();
    }


    @Override
    public float getLevel() {
        return level;
    }

    @Override
    public boolean setLevel(float level) {
        boolean changed = false;

        if (this.level != level) {
            changed = true;
            autoRange.setRange(level);
            this.level = level;
            invalidate();
        }
        return changed;
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


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        // wrap up superstate in our own state
        SavedState ss = new SavedState(superState);
        ss.level = getLevel();
        ss.autoRange = autoRange;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        level = ss.level;
        autoRange = (Autorange) ss.autoRange;
    }

    private static class SavedState extends BaseSavedState {
        float level;
        Serializable autoRange;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            level = in.readFloat();
            autoRange = in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(level);
            out.writeSerializable(autoRange);
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
