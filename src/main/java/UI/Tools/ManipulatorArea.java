package UI.Tools;

import Filters.UniversalFilter;
import Model.ImgEditingModel;
import UI.ImageContainer;
import UI.Tools.ManipulatorSliders.*;

public class ManipulatorArea {
    private final Brightness brightnessArea;
    private final Contrast contrastArea;
    private final Lights lightsArea;
    private final Shadows shadowsArea;
    private final Saturation saturationArea;
    private final Colorcurve curve;
    private final UniversalFilter universalFilter;


    public ManipulatorArea (final ImgEditingModel model, final ImgEditingModel tempModel) {
        this.brightnessArea = new Brightness(model);
        this.contrastArea = new Contrast(model);
        this.lightsArea = new Lights(model);
        this.shadowsArea = new Shadows(model);
        this.saturationArea = new Saturation(model);
        this.curve = new Colorcurve(Colorcurve.Mode.RGB);

        this.universalFilter = new UniversalFilter(tempModel, this.curve, this.brightnessArea.getSliderVal(), this.contrastArea.getSliderVal(),
                this.lightsArea.getSliderVal(), this.shadowsArea.getSliderVal(), this.saturationArea.getSliderVal());

        this.brightnessArea.setFilter(this.universalFilter);
        this.contrastArea.setFilter(this.universalFilter);
        this.lightsArea.setFilter(this.universalFilter);
        this.shadowsArea.setFilter(this.universalFilter);
        this.saturationArea.setFilter(this.universalFilter);
    }

    public Brightness getBrightnessArea () {
        return brightnessArea;
    }

    public Contrast getContrastArea () {
        return contrastArea;
    }

    public Lights getLightsArea () {
        return lightsArea;
    }

    public Shadows getShadowsArea () {
        return shadowsArea;
    }

    public Saturation getSaturationArea () {
        return saturationArea;
    }

    public Colorcurve getCurve () {
        return curve;
    }

    public UniversalFilter getUniversalFilter () {
        return universalFilter;
    }
}

