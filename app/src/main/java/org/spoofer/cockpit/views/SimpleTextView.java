package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;

public class SimpleTextView extends AppCompatTextView implements SensorView {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private String sensorName;
    private int valueIndex;

    public SimpleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SensorView, defStyleAttr, 0);
        try {
            sensorName = ta.getString(R.styleable.SensorView_sensorName);
            if (TextUtils.isEmpty(sensorName))
                throw new IllegalStateException("must have a sensor name");

            valueIndex = ta.getInt(R.styleable.SensorView_valueIndex, 0);
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
    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    @Override
    public void update(Event event) {
        if (event == null)
            return;
        float[] vals = event.getValues();
        if (vals == null || vals.length <= valueIndex)
            return;

        final String val = String.valueOf(vals[valueIndex]);

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (getText().equals(val))
                    return;
                setText(val);
                invalidate();
            }
        });
    }
}
