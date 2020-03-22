package org.spoofer.cockpit.views;

import java.io.Serializable;

public class Autorange implements Serializable {
    private float upperLimit = 0;
    private float lowerLimit = 0;
    private Float fixedRange = null;

    public float getUpperLimit() {
        return upperLimit;
    }

    public float getLowerLimit() {
        return lowerLimit;
    }


    // getRange gets the size of the ranges seen in the setRange.
    // specifically it is the ABS lower limit plus the upper limit.
    public float getRange() {
        return fixedRange != null ? fixedRange.floatValue() : upperLimit + Math.abs(lowerLimit);
    }

    // setFixedRange locks the given range.  Setting to zero will revert to auto range.
    public void setFixedRange(float value) {
        this.fixedRange = value != 0 ? value : null;
    }

    // setRange will add the given value to the current range.
    // if given is less than lower limit, it becomes lower limit
    // if given greater than upper limit, it becomes upper limit
    public void setRange(float value) {
        if (value < lowerLimit)
            lowerLimit = value;
        else if (value > upperLimit)
            upperLimit = value;
    }


}


