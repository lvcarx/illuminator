package Filters;

import Model.ImgEditingModel;
import hageldave.imagingkit.core.Img;

/**
 * manipulates contrast of image
 */
public class ContrastFilter extends Filter {
    private final ImgEditingModel imgModel;
    private static final Integer universalGrey = 100;
    public ContrastFilter (final ImgEditingModel brightnessModel) {
        this.imgModel = brightnessModel;
    }

    /**
     * deprecated - used when universal filter is not used
     * @param value
     * @return manipulated image
     */
    public Img manipulateImg(final Integer value) {
        Img image = null;
        if (this.imgModel.getCurrentImg() != null) {
            image = this.imgModel.getCurrentImg().copy();
            float preComputedVal = (float) value / 100;
            image.stream().parallel().forEach( px -> {
                int red = (int) (ContrastFilter.universalGrey + (px.r() - ContrastFilter.universalGrey) * preComputedVal);
                int green = (int) (ContrastFilter.universalGrey + (px.g() - ContrastFilter.universalGrey) * preComputedVal);
                int blue = (int) (ContrastFilter.universalGrey + (px.b() - ContrastFilter.universalGrey) * preComputedVal);
                px.setRGB_preserveAlpha(super.preventGetOutOfBoundaries(red), super.preventGetOutOfBoundaries(green), super.preventGetOutOfBoundaries(blue));
            });
        }
        return image;
    }

    public static short  manipulatePixel(final short pixel, final float value) {
        return (short) (ContrastFilter.universalGrey + (pixel - ContrastFilter.universalGrey) * value);
    }
}
