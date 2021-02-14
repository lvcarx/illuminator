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

public class BrushMenu extends SidePanel {
    public BrushMenu (ImageModelManager imageModelManager, BrushSettings brushSettings, ImageContainer imageContainer) {
        super(imageModelManager, brushSettings, imageContainer);
        this.createFilters();
        this.setBackground(Colors.sidebarBackground);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(createHeading("Pinseleinstellungen"));
        this.add(createBrushSection());
        this.add(createSeparator());
        this.add(createHeading("Presets"));
        this.add(this.createPresetArea());
        this.add(createSeparator());
        this.add(createHeading("Gradationskurve"));
        this.add(createGradationcurve());
        this.add(createSeparator());
        this.add(showMaskSection());
        this.add(resetSelection());
        this.add(createSeparator());
        this.add(saveSection());
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
        this.manipulatorArea = new ManipulatorArea(imageModelManager.getBrushEditedImage(), this.imageModelManager.getBrushEditedTempImage());
    }

    @Override
    public void resetSliderValues () {
        this.presets.resetPresets();
        this.manipulatorArea.getCurve().resetCurve();
    }
}
