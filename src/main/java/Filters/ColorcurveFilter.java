package Filters;

import Model.ImgEditingModel;
import UI.Tools.Colorcurve;
import hageldave.imagingkit.core.Img;

import java.util.HashMap;

public class ColorcurveFilter {
    private final ImgEditingModel model;
    private final Colorcurve curveUI;

    public ColorcurveFilter (final ImgEditingModel model, final Colorcurve curveUI) {
        this.model = model;
        this.curveUI = curveUI;
    }

    /**
     * deprecated - used when universal filter is not used
     * @param value
     * @return manipulated image
     */
    public Img manipulateImg (HashMap<Integer, Integer> value) {
        Img image = null;
        if (this.model.getCurrentImg() != null) {
            image = this.model.getCurrentImg().copy();
            image.stream().parallel().forEach( pix -> {
                int red = 255 - value.get(pix.r());
                int green = 255 - value.get(pix.g());
                int blue = 255 - value.get(pix.b());
                if (this.curveUI.getMode() == Colorcurve.Mode.RGB) {
                    pix.setRGB_preserveAlpha(red, green, blue);
                } else if (this.curveUI.getMode() == Colorcurve.Mode.RED) {
                    pix.setRGB_preserveAlpha(red, pix.g(), pix.b());
                } else if (this.curveUI.getMode() == Colorcurve.Mode.GREEN) {
                    pix.setRGB_preserveAlpha(pix.r(), green, pix.b());
                } else if (this.curveUI.getMode() == Colorcurve.Mode.BLUE) {
                    pix.setRGB_preserveAlpha(pix.r(), pix.g(), blue);
                }
            });
        }
        return image;
    }

    public static int manipulatePixel(final HashMap<Integer, Integer> map, final int pixelVal) {
        return 255 - map.get(pixelVal);
    }
}
