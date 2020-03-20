package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleTextView extends LinearLayout implements SensorView {

    private String sensorName;

    private List<TextView> views;


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

        } finally {
            ta.recycle();
        }
    }

    @Override
    public List<String> getSensorNames() {
        return Arrays.asList(new String[]{sensorName});
    }

    @Override
    public void update(Event event) {
        if (event == null)
            return;
        float[] vals = event.getValues();
        if (vals == null || vals.length == 0)
            return;

        if (views == null || views.isEmpty()) {
            views = getTextViews(vals.length);
        }

        int size = Math.min(views.size(), vals.length);
        for (int i = 0; i < size; i++) {
            views.get(i).setText(String.valueOf(vals[i]));
        }
    }

    private List<TextView> getTextViews(int count) {
        List<TextView> tvs = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            if (tvs.size() >= count) {
                break;
            }
            View v = getChildAt(i);
            if (!(v instanceof TextView))
                continue;
            tvs.add((TextView) v);
        }
        return tvs;
    }
}
