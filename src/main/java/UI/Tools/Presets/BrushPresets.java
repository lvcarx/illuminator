package UI.Tools.Presets;

import Model.ImageModelManager;
import Model.ValueModel;
import UI.ImageContainer;
import UI.Sidepanel.SidePanel;

import javax.swing.*;

public class BrushPresets extends Presets {
    public BrushPresets (final ValueModel<JComponent> currentSlider, final ImageModelManager imageModelManager,
                         ValueModel<Integer> brightnessModel, ValueModel<Integer> contrastModel, ValueModel<Integer> lightsModel,
                         ValueModel<Integer> shadowsModel, ValueModel<Integer> saturationModel,
                         final SidePanel parent, final ImageContainer imageContainer) {
        super(currentSlider, imageModelManager, brightnessModel, contrastModel, lightsModel, shadowsModel, saturationModel,
                parent, imageContainer);
    }

    public void manipulate(final Preset preset) {

            if (preset == Preset.Landschaft) {
                setImageFilterValue(110, 110, 105, 95, 115);
            } else if (preset == Preset.Portrait) {
                setImageFilterValue(115, 85, 95, 115, 85);
            } else if (preset == Preset.Sonnenuntergang) {
                setImageFilterValue(90, 100, 80, 115, 120);
            } else if (preset == Preset.BW) {
                setImageFilterValue( 85, 130, 115, 85, 0);
            } else {
                setImageFilterValue( 100, 100, 100, 100, 100);
            }
        }


    private void setImageFilterValue(final Integer brightness, final Integer contrast, final Integer lights,
                                    final Integer shadows, final Integer saturation) {
        this.brightnessModel.setAndGet(brightness);
        this.contrastModel.setAndGet(contrast);
        this.lightsModel.setAndGet(lights);
        this.shadowsModel.setAndGet(shadows);
        this.saturationModel.setAndGet(saturation);
    }
}
