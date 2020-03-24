package org.spoofer.cockpit.sensorviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;
import org.spoofer.cockpit.events.Event;

public class Dialview extends View implements SensorView {

    private String sensorName;
    private int valueIndex;

    @IntRange(from = 0, to = 10000)
    private int level;
    private float multiplier;

    @DrawableRes
    private int needleImgId;
    private RotateDrawable needleDrawable;
    private float needlePivotX;
    private float needlePivotY;
    private float needleOffset;

    private float startDegree;
    private float endDegree;

    public Dialview(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dialview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Dialview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dialview, defStyleAttr, defStyleRes);
        try {
            sensorName = ta.getString(R.styleable.Dialview_sensorName);
            valueIndex = ta.getInt(R.styleable.Dialview_valueIndex, 0);
            if (valueIndex < 0)
                valueIndex = Math.abs(valueIndex);

            multiplier = ta.getFloat(R.styleable.Dialview_multiplier, 1);

            setStartDegree(ta.getFloat(R.styleable.Dialview_startDegree, 0));
            setEndDegree(ta.getFloat(R.styleable.Dialview_endDegree, 360));

            setNeedlePivotX(ta.getFloat(R.styleable.Dialview_needlePivotX, 0.5f));
            setNeedlePivotY(ta.getFloat(R.styleable.Dialview_needlePivotY, 0.5f));
            setNeedleOffset(ta.getFloat(R.styleable.Dialview_needleOffset, 0.0f));

            // do needle id last so prvious properties are all set
            setNeedleImgId(ta.getResourceId(R.styleable.Dialview_needleImage, R.drawable.needle));

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

        int val = (int) (event.getValues()[valueIndex] * multiplier);
        if (val < 0)
            val = 0;
        else if (val > 10000)
            val = 10000;

        setLevel(val);
    }

    @IntRange(from = 0, to = 10000)
    public int getLevel() {
        return level;
    }

    public void setLevel(@IntRange(from = 0, to = 10000) int level) {
        if (this.level == level)
            return;
        this.level = level;
        invalidate();
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public int getNeedleImgId() {
        return needleImgId;
    }

    public void setNeedleImgId(@DrawableRes int needleImgId) {
        Drawable needleDraw = getResources().getDrawable(needleImgId, null);
        if (needleDraw == null)
            throw new IllegalArgumentException("Needle drawable not found");

        this.needleImgId = needleImgId;
        needleDrawable = new RotateDrawable();
        needleDrawable.setDrawable(needleDraw);
        needleDrawable.setPivotXRelative(true);
        needleDrawable.setPivotYRelative(true);
        needleDrawable.setPivotX(needlePivotX);
        needleDrawable.setPivotY(needlePivotY);
        needleDrawable.setFromDegrees(startDegree);
        needleDrawable.setToDegrees(endDegree);
    }

    public float getStartDegree() {
        return startDegree;
    }

    public void setStartDegree(float startDegree) {
        this.startDegree = startDegree;
    }

    public float getEndDegree() {
        return endDegree;
    }

    public void setEndDegree(float endDegree) {
        this.endDegree = endDegree;
    }

    public float getNeedlePivotX() {
        return needlePivotX;
    }

    public void setNeedlePivotX(float needlePivotX) {
        this.needlePivotX = needlePivotX;
    }

    public float getNeedlePivotY() {
        return needlePivotY;
    }

    public void setNeedlePivotY(float needlePivotY) {
        this.needlePivotY = needlePivotY;
    }

    public float getNeedleOffset() {
        return needleOffset;
    }

    public void setNeedleOffset(float needleOffset) {
        this.needleOffset = needleOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needleDrawable == null)
            return;
        Rect clip = new Rect();
        clip.left = getPaddingLeft();
        clip.right = getWidth() - getPaddingRight();
        clip.top = getPaddingTop();
        clip.bottom = getHeight() - getPaddingBottom();

        if (clip.width() < clip.height()) {
            int offset = (clip.height() - clip.width()) / 2;
            clip.top += offset;
            clip.bottom -= offset;
        } else {
            int offset = (clip.width() - clip.height()) / 2;
            clip.left += offset;
            clip.right -= offset;
        }
        needleDrawable.setLevel(getLevel());
        needleDrawable.setBounds(clip);
        needleDrawable.draw(canvas);
    }

}
