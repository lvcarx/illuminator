package Filters;

import Model.ImgEditingModel;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;

/**
 * manipulates light sections of the image
 */
public class LightsFilter extends Filter {
    private final ImgEditingModel imgModel;
    private int lowestValueLights;
    private int highestValueLights;
    private boolean valuesSetLights;

    public LightsFilter (final ImgEditingModel saturationModel) {
        this.imgModel = saturationModel;
        this.valuesSetLights = false;
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
            if (!valuesSetLights) {
                calcValues(image);
            }
            int lowerBorder = (int) ((this.highestValueLights - this.lowestValueLights) * 0.4);
            // all pixels between lowestVal and upperBorder will be manipulated
            image.stream().parallel().forEach( pix -> {
                if (pix.getLuminance() <= this.highestValueLights && pix.getLuminance() > lowerBorder) {
                    int red = (int) (pix.r() * preComputedVal);
                    int green = (int) (pix.g() * preComputedVal);
                    int blue = (int) (pix.b() * preComputedVal);
                    pix.setRGB_preserveAlpha(super.preventGetOutOfBoundaries(red), super.preventGetOutOfBoundaries(green), super.preventGetOutOfBoundaries(blue));
                }
            });
        }
        return image;
    }

    public static short manipulatePixel(final short highestVal, final short lowerBorder, final Pixel px, final short pixelVal, final float value) {
        if (px.getLuminance() <= highestVal && px.getLuminance() > lowerBorder) {
            return (short) ( pixelVal * value );
        } else {
            return (short) ( (pixelVal * 0.3) + (pixelVal * value) * 0.7 );
        }
    }

    private void calcValues(final Img image) {
        this.lowestValueLights = 255;
        this.highestValueLights = 0;
        image.forEach(true, pixel -> {
            // calc lowest
            int grey = pixel.getLuminance();
            if (grey < lowestValueLights) {
                lowestValueLights = grey;
            }
            // calc highest
            if (grey > highestValueLights) {
                highestValueLights = grey;
            }

        });
        this.valuesSetLights = true;
    }
}
