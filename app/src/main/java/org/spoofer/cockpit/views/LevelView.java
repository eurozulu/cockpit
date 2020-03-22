package org.spoofer.cockpit.views;

// LevelView is implemented by views which receive a 'level' value. which they reflect in their GUI.
public interface LevelView {
    boolean setLevel(float level);

    float getLevel();

    void setRange(float range);

    float getRange();
}
