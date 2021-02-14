package UI.Sidepanel;

import Model.BrushSettings;
import Model.ImageModelManager;
import UI.ImageContainer;
import UI.Resources.Colors;
import UI.Tools.ManipulatorArea;
import UI.Tools.Presets.Presets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GlobalMenu extends SidePanel {
    public GlobalMenu (ImageModelManager imageModelManager, BrushSettings brushSettings, ImageContainer imageContainer) {
        super(imageModelManager, brushSettings, imageContainer);
        this.brushSettings.getBrushActive().addValueListener(e -> {
            if (brushSettings.getBrushActive().getValue()) {
                resetSliderValues();
            }
        });
        this.createFilters();
        this.setBackground(Colors.sidebarBackground);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(super.createHeading("Presets"));
        this.add(this.createPresetArea());
        this.add(super.createSeparator());
        this.add(super.createHeading("Gradationskurve"));
        this.add(super.createGradationcurve());
        this.add(super.createSeparator());
        this.add(createHeading("Manipulationen"));
        this.add(super.createBrightnessManipulators());
        this.add(super.createSeparator());
        this.add(super.createColorManipulators());
        this.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        this.setBorder(new EmptyBorder(5, 5, 10, 10));
    }

    @Override
    protected Container createPresetArea () {
        Container container = new Container();
        container.setLayout(new FlowLayout());
        this.presets = new Presets(this.currentSlider, this.imageModelManager, this.manipulatorArea.getBrightnessArea().getSliderVal(),
                this.manipulatorArea.getContrastArea().getSliderVal(), this.manipulatorArea.getLightsArea().getSliderVal(),
                this.manipulatorArea.getShadowsArea().getSliderVal(), this.manipulatorArea.getSaturationArea().getSliderVal(), this, this.imageContainer);
        container.add(this.presets);
        return container;
    }

    @Override
    protected void createFilters () {
        this.manipulatorArea = new ManipulatorArea(imageModelManager.getModel(), this.imageModelManager.getTempModel());
    }

    @Override
    public void resetSliderValues () {
        this.presets.resetPresets();
        this.manipulatorArea.getCurve().resetCurve();
        this.manipulatorArea.getBrightnessArea().resetManipulator();
        this.manipulatorArea.getContrastArea().resetManipulator();
        this.manipulatorArea.getLightsArea().resetManipulator();
        this.manipulatorArea.getShadowsArea().resetManipulator();
        this.manipulatorArea.getSaturationArea().resetManipulator();
    }
}
