package org.spoofer.cockpit.sensorviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;

public class ProgressView extends ProgressBar implements SensorView {
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private String sensorName;
    private int valueIndex;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SensorViewValue, defStyleAttr, 0);
        try {
            sensorName = ta.getString(R.styleable.SensorView_sensorName);
            valueIndex = ta.getInt(R.styleable.SensorViewValue_valueIndex, 0);
            if (valueIndex < 0)
                valueIndex = Math.abs(valueIndex);

        } finally {
            ta.recycle();
        }
    }

    public int getValueIndex() {
        return valueIndex;
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

        final int val = (int) event.getValues()[valueIndex];

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getProgress() == val)
                    return;
                setProgress(val);
                invalidate();
            }
        });

    }
}
