package Filters;

import Model.ImgEditingModel;
import hageldave.imagingkit.core.Img;

/**
 * manipulates saturation of image
 */
public class SaturationFilter extends Filter {
    private final ImgEditingModel imgModel;
    public SaturationFilter (final ImgEditingModel saturationModel) {
        this.imgModel = saturationModel;
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
                int greyValue = (px.r() + px.g() + px.b()) / 3;
                int red = (int) (greyValue + (px.r() - greyValue) * preComputedVal);
                int green = (int) (greyValue + (px.g() - greyValue) * preComputedVal);
                int blue = (int) (greyValue + (px.b() - greyValue) * preComputedVal);
                px.setRGB_preserveAlpha(super.preventGetOutOfBoundaries(red), super.preventGetOutOfBoundaries(green), super.preventGetOutOfBoundaries(blue));
            });
        }
        return image;
    }

    public static int manipulatePixel(final short pixel, final short greyValue, final float value) {
        return (short) ( greyValue + ( pixel - greyValue ) * value);
    }
}
