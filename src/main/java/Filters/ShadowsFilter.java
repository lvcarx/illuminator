package Filters;

import Model.ImgEditingModel;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;
import hageldave.imagingkit.core.operations.ColorSpaceTransformation;

/**
 * manipulates the dark sections of the image
 */
public class ShadowsFilter extends Filter {
    private final ImgEditingModel imgModel;
    private Integer lowestValue;
    private Integer highestValue;
    private boolean valuesSet;

    public ShadowsFilter (final ImgEditingModel saturationModel) {
        this.imgModel = saturationModel;
        this.valuesSet = false;
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
            if (!valuesSet) {
                calcValues(image);
            }
            int upperBorder = (int) ((this.highestValue - this.lowestValue) * 0.4);

            // all pixels between lowestVal and upperBorder will be manipulated
            image.stream().parallel().forEach( pix -> {
                if (pix.getLuminance() >= this.lowestValue && pix.getLuminance() < upperBorder) {
                    int red = (int) (pix.r() * preComputedVal);
                    int green = (int) (pix.g() * preComputedVal);
                    int blue = (int) (pix.b() * preComputedVal);
                    pix.setRGB_preserveAlpha(super.preventGetOutOfBoundaries(red), super.preventGetOutOfBoundaries(green), super.preventGetOutOfBoundaries(blue));
                }
            });
        }
        return image;
    }

    public static int manipulatePixel(final short lowestVal, final short upperBorder, final Pixel px, final short pixelVal, final float value) {
        if (px.getLuminance() >= lowestVal && px.getLuminance() < upperBorder) {
            return (short) ( pixelVal * value );
        } else {
            return (short) ( (pixelVal * 0.3) + (pixelVal * value) * 0.7 );
        }
    }

    private void calcValues(final Img image) {
        this.lowestValue = 255;
        this.highestValue = 0;
        image.forEach(ColorSpaceTransformation.RGB_2_HSV);
        image.forEach(true, (pixel) -> {
            // calc lowest
            if (pixel.b() < lowestValue) {
                lowestValue = pixel.b();
            }
            // calc highest
            if (pixel.b() > highestValue) {
                highestValue = pixel.b();
            }
        });
        this.valuesSet = true;
        image.forEach(ColorSpaceTransformation.HSV_2_RGB);
    }
}
