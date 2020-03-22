package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

public class Dialview extends BaseLevelView {

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

        float valueScale = (getLevel() + Math.abs(autoRange.getLowerLimit())) / getRange();
        int val = (int) (MAX_LEVEL_VALUE * valueScale);

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

        if (needleOffset != 0)
            val -= (MAX_LEVEL_VALUE / (360 / needleOffset));

        needleDrawable.setLevel(val);
        needleDrawable.setBounds(clip);
        needleDrawable.draw(canvas);
    }

}
