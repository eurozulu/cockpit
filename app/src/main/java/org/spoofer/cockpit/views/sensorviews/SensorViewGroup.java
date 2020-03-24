package org.spoofer.cockpit.views.sensorviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;
import org.spoofer.cockpit.views.LevelView;

public class SensorViewGroup extends LinearLayout implements SensorView {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private String sensorName;

    @IdRes
    private int[] viewIDs;

    public SensorViewGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SensorViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SensorView, defStyleAttr, 0);
        try {
            sensorName = ta.getString(R.styleable.SensorView_sensorName);
            if (TextUtils.isEmpty(sensorName))
                throw new IllegalStateException("must have a sensor name");

            viewIDs = getValueViews(ta);

        } finally {
            ta.recycle();
        }
    }

    @Override
    public String getSensorName() {
        return sensorName;
    }

    @Override
    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
        invalidate();
        requestLayout();
    }

    @Override
    public void update(Event event) {
        if (event == null)
            return;
        final float[] vals = event.getValues();
        if (vals == null || vals.length == 0)
            return;

        uiHandler.post(() -> {
            for (int i = 0; i < vals.length; i++) {
                if (viewIDs.length <= i || viewIDs[i] == 0)
                    continue;
                View v = findViewById(viewIDs[i]);
                if (v == null)
                    continue;
                setViewValue(v, vals[i]);
            }
        });
    }

    private void setViewValue(View v, float value) {
        if (v instanceof TextView)
            ((TextView) v).setText(String.valueOf(value));

        else if (v instanceof ProgressBar)
            ((ProgressBar) v).setProgress((int) value);

        else if (v instanceof LevelView)
            ((LevelView) v).setLevel(value);
    }

    @IdRes
    private int[] getValueViews(TypedArray ta) {
        @IdRes
        int[] ids = new int[5];
        ids[0] = ta.getResourceId(R.styleable.SensorView_valueRef_0, 0);
        ids[1] = ta.getResourceId(R.styleable.SensorView_valueRef_1, 0);
        ids[2] = ta.getResourceId(R.styleable.SensorView_valueRef_2, 0);
        ids[3] = ta.getResourceId(R.styleable.SensorView_valueRef_3, 0);
        ids[4] = ta.getResourceId(R.styleable.SensorView_valueRef_4, 0);
        return ids;
    }
}
