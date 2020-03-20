package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;
import org.spoofer.cockpit.events.EventListener;

public class SegmentMeter extends View implements EventListener {

    @ColorInt
    private static final int DEFAULT_COLOUR = Color.RED;

    private boolean isHorizontal;
    @ColorInt private int color;

    private final Object lock = new Object();
    private transient Event lastEvent;

    public SegmentMeter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentMeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SegmentMeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SegmentMeter,
                defStyleAttr, defStyleRes);

        try {
            isHorizontal = ta.getBoolean(R.styleable.SegmentMeter_orientation, false);

            color = ta.getColor(R.styleable.SegmentMeter_colour, DEFAULT_COLOUR);

        } finally {
            ta.recycle();
        }
    }

    @Override
    public void update(Event event) {
        synchronized (lock) {
            this.lastEvent = event;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Event ev;
        synchronized (lock) {
            ev = this.lastEvent;
        }
        if (ev == null) {
            // No events received yet
            this.setEnabled(false);
            return;
        } else if (!this.isEnabled()) {
                this.setEnabled(true);
        }

    }
}
