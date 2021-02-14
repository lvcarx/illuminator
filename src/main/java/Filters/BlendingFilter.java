package Filters;

import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;

/**
 * For applying the brush manipulation
 */
public class BlendingFilter {
    private Img originalImage;

    public BlendingFilter (final Img originalImage) {
        this.originalImage = originalImage;
    }

    /**
     * blends the images
     *
     * @param editedImage
     * @return blended image
     */
    public Img manipulateImg(final Img editedImage) {
        Img newImage = new Img(originalImage.getWidth(), originalImage.getHeight());
        originalImage.stream().parallel().forEach( px -> {
            short xCord = (short) px.getX();
            short yCord = (short) px.getY();
            Pixel editedPx = editedImage.getPixel(xCord, yCord);
            Pixel newPx = newImage.getPixel(xCord, yCord);
            short red = (short) (editedPx.a_asDouble() * editedPx.r() + (1 - editedPx.a_asDouble()) * px.r());
            short green = (short) (editedPx.a_asDouble() * editedPx.g() + (1 - editedPx.a_asDouble()) * px.g());
            short blue = (short) (editedPx.a_asDouble() * editedPx.b() + (1 - editedPx.a_asDouble()) * px.b());
            newPx.setRGB(red, green, blue);
        });

        return newImage;
    }
}
