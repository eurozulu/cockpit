package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

public class Dialview extends View implements LevelView {
    private static final int MAX_VALUE = 10000;

    private float level;
    private Autorange autoRange;

    @DrawableRes
    private int needleImgId;
    private RotateDrawable needleDrawable;

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
            setLevel(ta.getFloat(R.styleable.Dialview_level, 0f));
            setRange(ta.getInt(R.styleable.Dialview_range, 0));

            setStartDegree(ta.getFloat(R.styleable.Dialview_startDegree, 0));
            setEndDegree(ta.getFloat(R.styleable.Dialview_endDegree, 360));

            setNeedleImgId(ta.getResourceId(R.styleable.Dialview_needleImage, R.drawable.needle));


        } finally {
            ta.recycle();
        }
    }

    public float getLevel() {
        return level;
    }

    public boolean setLevel(float level) {
        boolean changed = this.level != level;
        if (changed) {
            autoRange.setRange(level);
            this.level = level;
            invalidate();
        }
        return changed;
    }

    @Override
    public float getRange() {
        return autoRange.getRange();
    }

    @Override
    public void setRange(float range) {
        autoRange = new Autorange();
        if (range != 0)
            autoRange.setFixedRange(range);
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
        needleDrawable.setPivotX(0.5f);
        needleDrawable.setPivotY(0.772f);
        needleDrawable.setFromDegrees(startDegree);
        needleDrawable.setToDegrees(endDegree);
        needleDrawable.setLevel((int) level);
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needleDrawable == null)
            return;

        float valueScale = (getLevel() + Math.abs(autoRange.getLowerLimit())) / getRange();
        int val = (int) (MAX_VALUE * valueScale);
        needleDrawable.setLevel(val);

        Rect clip = needleDrawable.getBounds();
        if (clip.width() == 0 || clip.height() == 0) {
            clip.left = getLeft() + getPaddingLeft();
            clip.right = getRight() - getPaddingRight();
            clip.top = getTop() + getPaddingTop();
            clip.bottom = getBottom() - getPaddingBottom();
            if (clip.width() < clip.height()) {
                int offset = (clip.height() - clip.width()) / 2;
                clip.top += offset;
                clip.bottom -= offset;
            } else {
                int offset = (clip.width() - clip.height()) / 2;
                clip.left += offset;
                clip.right -= offset;
            }
            needleDrawable.setBounds(clip);
        }
        needleDrawable.draw(canvas);

    }

}
