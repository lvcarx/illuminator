package Filters;

import Model.ImgEditingModel;
import hageldave.imagingkit.core.Img;

/**
 * manipulates brightness of image
 */
public class BrightnessFilter extends Filter {
    private final ImgEditingModel imgModel;
    public BrightnessFilter (final ImgEditingModel brightnessModel) {
        this.imgModel = brightnessModel;
    }

    /**
     * deprecated - used when universal filter is not used
     * @param value
     * @return manipulated image
     */
    @Override
    public Img manipulateImg(final Integer value) {
        Img image = null;
        if (this.imgModel.getCurrentImg() != null) {
            image = this.imgModel.getCurrentImg().copy();
            float preComputedValue = (float) value / 100;
            image.stream().parallel().forEach( px -> {
                short red = (short) (px.r() * preComputedValue);
                short green = (short) (px.g() * preComputedValue);
                short blue = (short) (px.b() * preComputedValue);
                px.setRGB_preserveAlpha(super.preventGetOutOfBoundaries(red), super.preventGetOutOfBoundaries(green), super.preventGetOutOfBoundaries(blue));
            });
        }
        return image;
    }

    public static short manipulatePixel(final short pixel, final float value) {
        return (short) (pixel * value);
    }
}
