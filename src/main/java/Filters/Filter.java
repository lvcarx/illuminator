package Filters;

import hageldave.imagingkit.core.Img;

import java.util.HashMap;

public abstract class Filter {
    public abstract Img manipulateImg(final Integer value);

    /**
     * prevents that the pixel values get out of the 0-255 range
     *
     * @param value
     * @return
     */
    protected Integer preventGetOutOfBoundaries(final int value) {
        short newVal = (short) value;
        if (value > 255) {
            newVal = 255;
        } else if (value < 0) {
            newVal = 0;
        }
        return (int) newVal;
    }
}
