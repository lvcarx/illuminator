package UI.Tools.Presets;

import Model.ImageModelManager;
import Model.ValueModel;
import UI.CustomImagePanel.CustomImagePanel;
import UI.ImageContainer;
import UI.Sidepanel.SidePanel;

import javax.swing.*;

public class Presets extends JComboBox<Presets.Preset> {
    protected ImageModelManager imageModelManager;
    protected ValueModel<JComponent> currentSlider;

    // value models
    protected ValueModel<Integer> brightnessModel;
    protected ValueModel<Integer> contrastModel;
    protected ValueModel<Integer> lightsModel;
    protected ValueModel<Integer> shadowsModel;
    protected ValueModel<Integer> saturationModel;

    enum Preset {
        None,
        Landschaft,
        Portrait,
        Sonnenuntergang,
        BW
    }

    public Presets (final ValueModel<JComponent> currentSlider, final ImageModelManager imageModelManager,
                    ValueModel<Integer> brightnessModel, ValueModel<Integer> contrastModel, ValueModel<Integer> lightsModel,
                    ValueModel<Integer> shadowsModel, ValueModel<Integer> saturationModel,
                    final SidePanel parent, final ImageContainer imageContainer) {
        this.brightnessModel = brightnessModel;
        this.contrastModel = contrastModel;
        this.lightsModel = lightsModel;
        this.shadowsModel = shadowsModel;
        this.saturationModel = saturationModel;
        this.imageModelManager = imageModelManager;
        this.currentSlider = currentSlider;

        for (Preset preset : Preset.values()) {
            this.addItem(preset);
        }
        this.addActionListener(e -> {
            manipulate((Preset) this.getSelectedItem());
            if (parent.getBrushActive().getValue()) {
                CustomImagePanel customImagePanel = (CustomImagePanel) imageContainer.getPanel();
                this.imageModelManager.getModel().setCurrentImg(customImagePanel.blendImage());
            }
        });
    }

    public void manipulate(final Preset preset) {
        if (preset == Preset.Landschaft) {
            setImageFilterValue(110, 110, 105, 95, 115);
        } else if (preset == Preset.Portrait) {
            setImageFilterValue(115, 85, 95, 115, 85);
        } else if (preset == Preset.Sonnenuntergang) {
            setImageFilterValue(90, 100, 80, 115, 120);;
        } else if (preset == Preset.BW) {
            setImageFilterValue( 85, 130, 115, 85, 0);
        } else {
            setImageFilterValue( 100, 100, 100, 100, 100);
        }
        this.currentSlider.setValue(this);
    }

    private void setImageFilterValue(final Integer brightness, final Integer contrast, final Integer lights,
                                     final Integer shadows, final Integer saturation) {
        this.brightnessModel.setAndGet(brightness);
        this.contrastModel.setAndGet(contrast);
        this.lightsModel.setAndGet(lights);
        this.shadowsModel.setAndGet(shadows);
        this.saturationModel.setAndGet(saturation);
    }

    public void resetPresets() {
        this.setSelectedItem(Preset.None);
    }
}
