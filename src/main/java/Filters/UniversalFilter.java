package Filters;

import Model.ImgEditingModel;
import Model.ValueModel;
import UI.Tools.Colorcurve;
import hageldave.imagingkit.core.Img;


/**
 * this filter applies all the manipulations to the image with the given values of the manipulator models (brightness, contras,t etc.)
 */
public class UniversalFilter extends Filter {
    private short lowestValueLights;
    private short highestValueLights;
    private boolean valuesSetLights = false;
    private short lowestValueShadows;
    private short highestValueShadows;
    private boolean valuesSetShadows = false;


    private final ImgEditingModel imgEditingModel;
    private final ValueModel<Integer> brightnessVal;
    private final ValueModel<Integer> contrastVal;
    private final ValueModel<Integer> lightsVal;
    private final ValueModel<Integer> shadowsVal;
    private final ValueModel<Integer> saturationVal;
    private final Colorcurve curveUI;

    public UniversalFilter (final ImgEditingModel imgEditingModel, final Colorcurve curve, final ValueModel<Integer> brightnessVal,
                            final ValueModel<Integer> contrastVal, final ValueModel<Integer> lightsVal, final ValueModel<Integer> shadowsVal,
                            final ValueModel<Integer> saturationVal) {
        this.imgEditingModel = imgEditingModel;
        this.curveUI = curve;
        this.brightnessVal = brightnessVal;
        this.contrastVal = contrastVal;
        this.lightsVal = lightsVal;
        this.shadowsVal = shadowsVal;
        this.saturationVal = saturationVal;
    }

    public Img manipulateImg () {
        Img copiedImg = null;
        if (imgEditingModel.getCurrentImg() != null) {
            copiedImg = imgEditingModel.getCurrentImg().copy();
            float preComputedBrightness = (float) brightnessVal.getValue() / 100;
            float preComputedContrast = (float) contrastVal.getValue() / 100;
            float preComputedLights = (float) lightsVal.getValue() / 100;
            float preComputedSaturation = (float) saturationVal.getValue() / 100;
            float preComputedShadows = (float) shadowsVal.getValue() / 100;

            copiedImg.stream().parallel().forEach(px -> {
                // color curve manipulation
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapRGB(), px.r())),
                        super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapRGB(), px.g())),
                        super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapRGB(), px.b())));

                px.setR(
                        super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapRed(), px.r())));

                px.setG(super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapGreen(), px.g())));

                px.setB(super.preventGetOutOfBoundaries(ColorcurveFilter.manipulatePixel(this.curveUI.getCurveMapBlue(), px.b())));

                // BRIGHTNESS manipulation
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(BrightnessFilter.manipulatePixel((short) px.r(), preComputedBrightness)),
                        super.preventGetOutOfBoundaries(BrightnessFilter.manipulatePixel((short) px.g(), preComputedBrightness)),
                        super.preventGetOutOfBoundaries(BrightnessFilter.manipulatePixel((short) px.b(), preComputedBrightness)));

                // CONTRAST manipulation
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(ContrastFilter.manipulatePixel((short) px.r(), preComputedContrast)),
                        super.preventGetOutOfBoundaries(ContrastFilter.manipulatePixel((short) px.g(), preComputedContrast)),
                        super.preventGetOutOfBoundaries(ContrastFilter.manipulatePixel((short) px.b(), preComputedContrast)));

                // LIGHTS manipulation
                if (!valuesSetLights) {
                    calcValuesLights(imgEditingModel.getCurrentImg());
                }
                short lowerBorder = (short) ( ( this.highestValueLights - this.lowestValueLights ) * 0.4 );
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(LightsFilter.manipulatePixel(this.highestValueLights, lowerBorder, px, (short) px.r(), preComputedLights)),
                        super.preventGetOutOfBoundaries(LightsFilter.manipulatePixel(this.highestValueLights, lowerBorder, px, (short) px.g(), preComputedLights)),
                        super.preventGetOutOfBoundaries(LightsFilter.manipulatePixel(this.highestValueLights, lowerBorder, px, (short) px.b(), preComputedLights)));

                // SATURATION manipulation
                short greyValue = (short) (( px.r() + px.g() + px.b() ) / 3);
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(SaturationFilter.manipulatePixel((short) px.r(), greyValue, preComputedSaturation)),
                        super.preventGetOutOfBoundaries(SaturationFilter.manipulatePixel((short) px.g(), greyValue, preComputedSaturation)),
                        super.preventGetOutOfBoundaries(SaturationFilter.manipulatePixel((short) px.b(), greyValue, preComputedSaturation)));

                // SHADOWS manipulation
                if (!valuesSetShadows) {
                    calcValuesShadows(imgEditingModel.getCurrentImg());
                }
                short upperBorder = (short) ( ( this.highestValueShadows - this.lowestValueShadows ) * 0.4 );
                px.setRGB_preserveAlpha(
                        super.preventGetOutOfBoundaries(ShadowsFilter.manipulatePixel(this.highestValueShadows, upperBorder, px, (short) px.r(), preComputedShadows)),
                        super.preventGetOutOfBoundaries(ShadowsFilter.manipulatePixel(this.highestValueShadows, upperBorder, px, (short) px.g(), preComputedShadows)),
                        super.preventGetOutOfBoundaries(ShadowsFilter.manipulatePixel(this.highestValueShadows, upperBorder, px, (short) px.b(), preComputedShadows)));
            });
        }
        return copiedImg;
    }

    /**
     * calculates the light sections of the image
     *
     * @param image
     */
    public void calcValuesLights (final Img image) {
        this.lowestValueLights = 255;
        this.highestValueLights = 0;
        image.forEach(true, pixel -> {
            // calc lowest
            short grey = (short) pixel.getLuminance();
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

    /**
     * calculates the light sections of the image
     *
     * @param image
     */
    public void calcValuesShadows (final Img image) {
        this.lowestValueShadows = 255;
        this.highestValueShadows = 0;
        image.forEach(true, pixel -> {
            // calc lowest
            short grey = (short) pixel.getLuminance();
            if (grey < lowestValueShadows) {
                lowestValueShadows = grey;
            }
            // calc highest
            if (grey > highestValueShadows) {
                highestValueShadows = grey;
            }

        });
        this.valuesSetShadows = true;
    }

    @Override
    public Img manipulateImg (Integer value) {
        return null;
    }
}
