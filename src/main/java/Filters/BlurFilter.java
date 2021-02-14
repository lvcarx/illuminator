package Filters;

import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;


/**
 * blurs the alpha values in the image for soft brush
 */
public class BlurFilter {
    // precomputed values for faster box blur
    private final int minimum;
    private final int maximum;
    private final int kernelSizeDoubled;

    public BlurFilter (final int kernelSize) {
        if (!( ( kernelSize % 2 ) == 0 )) {
            this.minimum = (int) ( Math.floor((float) kernelSize / 2) * ( -1 ) );
            this.maximum = (int) Math.floor((float) kernelSize / 2);
            this.kernelSizeDoubled = kernelSize * 2;
        } else {
            throw new IllegalArgumentException("kernel size has to be uneven");
        }
    }

    /**
     * uses box blur with two convoluted 1d filters for faster performance
     * @param copiedEditedImg
     * @return
     */
    public Img manipulateAlpha (final Img copiedEditedImg) {
        Img img = copiedEditedImg.copy();
        Img blurredImg = copiedEditedImg.copy();
        blurredImg.stream().parallel().forEach(px -> {
            float alphaSumX = 0;
            float alphaSumY = 0;
            for (int i = this.minimum; i < this.maximum; i++) {
                alphaSumX += Pixel.a(img.getValue(px.getX() + i, px.getY(), Img.boundary_mode_repeat_edge));
                alphaSumY += Pixel.a(img.getValue(px.getX(), px.getY() + i, Img.boundary_mode_repeat_edge));
            }
            px.setA((int) ( ( alphaSumX + alphaSumY ) / this.kernelSizeDoubled ));
        });
        return blurredImg;
    }
}
