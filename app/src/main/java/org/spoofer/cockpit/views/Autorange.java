package org.spoofer.cockpit.views;

import java.io.Serializable;

public class Autorange implements Serializable {
    private int upperRange = 0;
    private int lowerRange = 0;

    public int getUpperRange() {
        return upperRange;
    }

    public int getLowerRange() {
        return lowerRange;
    }

    public int getMidRange() {
        return getRangeSize() / 2;
    }

    public int getRangeSize() {
        return upperRange + Math.abs(lowerRange);
    }

    public void setRange(int value) {
        if (value < lowerRange)
            lowerRange = value;
        else if (value > upperRange)
            upperRange = value;
    }


}


