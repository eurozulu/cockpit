package org.spoofer.cockpit.sensorviews;

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

    public void setRange(float range) {
        if (range == 0) {
        } else {
            fixedRange = new Float(range);
        }
    }


    /**
     * Update the curent range with the given value.
     * if given is less than lower limit, it becomes lower limit
     * if given greater than upper limit, it becomes upper limit
     *
     * @param value
     */
    public void updateRange(float value) {
        if (value < lowerLimit)
            lowerLimit = value;
        else if (value > upperLimit)
            upperLimit = value;
    }


}


