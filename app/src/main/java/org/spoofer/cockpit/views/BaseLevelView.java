package org.spoofer.cockpit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;

import org.spoofer.cockpit.R;

import java.io.Serializable;

// LevelView is a base class for custom views using the 'setLevel' interface
public class BaseLevelView extends View implements LevelView {
    protected static final int MAX_LEVEL_VALUE = 10000;

    private float level;
    protected Autorange autoRange;

    public BaseLevelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BaseLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LevelView, defStyleAttr, defStyleRes);
        try {
            setLevel(ta.getFloat(R.styleable.LevelView_level, 0f));
            setRange(ta.getInt(R.styleable.LevelView_range, 0));
        } finally {
            ta.recycle();
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

    public float getLevel() {
        return level;
    }

    public boolean setLevel(float level) {
        boolean changed = this.level != level;
        if (changed) {
            autoRange.setRange(level);
            float from = this.level;
            this.level = level;
            onLevelChanged(from, level);
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

    protected void onLevelChanged(float fromLevel, float toLevel) {
        invalidate();
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
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new BaseLevelView.SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
