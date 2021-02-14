package UI.Sidepanel;

import Model.BrushSettings;
import Model.ImageModelManager;
import Model.ValueModel;
import UI.CustomImagePanel.CustomImagePanel;
import UI.ImageContainer;
import UI.Resources.Colors;
import UI.Tools.Colorcurve;
import UI.Tools.ManipulatorArea;
import UI.Tools.Presets.Presets;
import hageldave.imagingkit.core.Img;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Initializes a Sidepanel with its inner components
 */
public abstract class SidePanel extends JPanel {
    protected ImageModelManager imageModelManager;
    protected BrushSettings brushSettings;
    protected ImageContainer imageContainer;
    protected final ValueModel<JComponent> currentSlider = new ValueModel<JComponent>();
    protected ManipulatorArea manipulatorArea;
    protected Presets presets;


    public SidePanel (final ImageModelManager imageModelManager, final BrushSettings brushSettings, final ImageContainer imageContainer) {
        this.imageContainer = imageContainer;
        this.imageModelManager = imageModelManager;
        this.brushSettings = brushSettings;
    }

    protected abstract Container createPresetArea ();

    protected abstract void createFilters();

    protected Box createHeading (final String value) {
        Box box = Box.createHorizontalBox();
        JLabel title = new JLabel(value);
        title.setForeground(Colors.headingTextColor);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(new Font("Calibri", Font.TRUETYPE_FONT, 15));
        box.add(title);
        box.add(Box.createHorizontalGlue());
        box.setBorder(new EmptyBorder(5, 0, 15, 0));
        return box;
    }

    protected JSeparator createSeparator () {
        JSeparator separator = new JSeparator();
        separator.setForeground(Colors.separatorColor);
        return separator;
    }

    protected  Container createColorManipulators() {
        Container wrapper = new Container();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
        wrapper.add(this.manipulatorArea.getSaturationArea());
        return wrapper;
    }

    protected Container createBrightnessManipulators() {
        Container wrapper = new Container();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
        wrapper.add(this.manipulatorArea.getBrightnessArea());
        wrapper.add(this.manipulatorArea.getContrastArea());
        wrapper.add(this.manipulatorArea.getLightsArea());
        wrapper.add(this.manipulatorArea.getShadowsArea());
        return wrapper;
    }

    protected Container createGradationcurve () {
        Container wrapper = new Container();
        Container buttonWrapper = new Container();
        Container confirmGradationWrapper = new Container();

        Container gradationWrapper = new Container();

        buttonWrapper.setLayout(new FlowLayout());
        confirmGradationWrapper.setLayout(new FlowLayout());
        gradationWrapper.setLayout(new FlowLayout());
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JComboBox chooseMode = new JComboBox();
        JButton deletePoint = new JButton("Punkt löschen");
        deletePoint.setEnabled(false);

        for (Colorcurve.Mode mode : Colorcurve.Mode.values()) {
            chooseMode.addItem(mode);
        }
        chooseMode.addActionListener(e -> {
            this.manipulatorArea.getCurve().setMode((Colorcurve.Mode) chooseMode.getSelectedItem());
        });
        deletePoint.addActionListener(e -> {
            this.manipulatorArea.getCurve().removePointInPipe();
        });
        this.manipulatorArea.getCurve().getIsPipelineEmpty().addValueListener(e -> {
            if (this.manipulatorArea.getCurve().getIsPipelineEmpty().getValue()) {
                deletePoint.setEnabled(true);
            } else {
                deletePoint.setEnabled(false);
            }
        });
        this.manipulatorArea.getCurve().getCurveMapRGBChanged().addValueListener(e -> {
            this.currentSlider.setValue(this.manipulatorArea.getCurve());
            manipulateGradation();
        });
        this.manipulatorArea.getCurve().getCurveMapRedChanged().addValueListener(e -> {
            this.currentSlider.setValue(this.manipulatorArea.getCurve());
            manipulateGradation();
        });
        this.manipulatorArea.getCurve().getCurveMapGreenChanged().addValueListener(e -> {
            this.currentSlider.setValue(this.manipulatorArea.getCurve());
            manipulateGradation();
        });
        this.manipulatorArea.getCurve().getCurveMapBlueChanged().addValueListener(e -> {
            this.currentSlider.setValue(this.manipulatorArea.getCurve());
            manipulateGradation();
        });

        gradationWrapper.add(this.manipulatorArea.getCurve());
        buttonWrapper.add(deletePoint);
        buttonWrapper.add(chooseMode);
        wrapper.add(confirmGradationWrapper);
        wrapper.add(buttonWrapper);
        wrapper.add(gradationWrapper);
        return wrapper;
    }


    protected void blendImage() {
        if (this.getBrushActive().getValue()) {
            CustomImagePanel customImagePanel = (CustomImagePanel) imageContainer.getPanel();
            Img blendedImage = customImagePanel.blendImage();
            this.imageModelManager.getModel().setCurrentImg(blendedImage);
        }
    }

    protected void manipulateGradation() {
        if (!this.brushSettings.getBrushActive().getValue()) {
            Img newImage = this.manipulatorArea.getUniversalFilter().manipulateImg();
            if (newImage != null) {
                this.imageModelManager.getModel().setCurrentImg(newImage);
            }
        } else {
            Img newImage = this.manipulatorArea.getUniversalFilter().manipulateImg();
            if (newImage != null) {
                this.imageModelManager.getBrushEditedImage().setCurrentImg(newImage);
            }
        }
        blendImage();
    }

    public abstract void resetSliderValues ();

    /**
     * Brushes section
     */
    protected Container createBrushSection() {
        Container wrapper = new Container();
        Container sizeWrap = new Container();
        Container labelWrap = new Container();
        labelWrap.setLayout(new FlowLayout());
        sizeWrap.setLayout(new FlowLayout());
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.PAGE_AXIS));
        JLabel sizeLabel = new JLabel("Pinselgröße (in px)");
        sizeLabel.setForeground(Colors.textColor);
        JLabel sizeValue = new JLabel(this.brushSettings.getBrushSize().getValue().toString());
        sizeValue.setForeground(Colors.textColor);
        JSlider sizeSlider = new JSlider(0, 300, this.brushSettings.getBrushSize().getValue());
        this.brushSettings.getBrushSize().addValueListener(e -> sizeValue.setText(this.brushSettings.getBrushSize().getValue().toString()));
        sizeSlider.addChangeListener(e -> {
            this.brushSettings.getBrushSize().setValue(sizeSlider.getValue());
        });

        Container selectBrushMode = new Container();
        selectBrushMode.setLayout(new FlowLayout());
        JRadioButton addBrush = new JRadioButton("Hinzufügen");
        JRadioButton removeBrush = new JRadioButton("Löschen");
        addBrush.setForeground(Colors.textColor);
        removeBrush.setForeground(Colors.textColor);
        selectBrushMode.add(addBrush);
        selectBrushMode.add(removeBrush);
        addBrush.setSelected(true);

        addBrush.addActionListener( e -> this.brushSettings.getBrushMode().setValue(true));
        removeBrush.addActionListener( e -> this.brushSettings.getBrushMode().setValue(false));

        ButtonGroup selectGroup = new ButtonGroup();
        selectGroup.add(addBrush);
        selectGroup.add(removeBrush);

        sizeWrap.add(sizeValue);
        sizeWrap.add(sizeSlider);
        labelWrap.add(sizeLabel);
        wrapper.add(labelWrap);
        wrapper.add(sizeWrap);
        wrapper.add(selectBrushMode);
        return wrapper;
    }

    protected Container showMaskSection() {
        Container wrapper = new Container();
        wrapper.setLayout(new FlowLayout());
        JCheckBox maskCheckbox = new JCheckBox("Maske anzeigen");
        maskCheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged (ItemEvent e) {
                if (getBrushActive().getValue()) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        toggleMask();
                    } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                        toggleMask();
                    }
                }
            }
        });
        this.brushSettings.getShowMask().addValueListener(e -> {
            if (!this.brushSettings.getShowMask().getValue()) {
                maskCheckbox.setSelected(false);
            }
        });
        maskCheckbox.setForeground(Colors.textColor);
        wrapper.add(maskCheckbox);
        return wrapper;
    }

    protected Container resetSelection() {
        Container wrapper = new Container();
        wrapper.setLayout(new FlowLayout());
        JButton resetSelection = new JButton("Zurücksetzen");
        JButton invertButton = new JButton("Invertieren");
        resetSelection.addActionListener(e -> {
            this.imageModelManager.resetMasks();
            blendImage();
        });
        invertButton.addActionListener(e -> {
            this.imageModelManager.invertMasks();
            blendImage();
        });
        wrapper.add(invertButton);
        wrapper.add(resetSelection);
        return wrapper;
    }

    protected Container saveSection() {
        Container wrapper = new Container();
        wrapper.setLayout(new FlowLayout());
        JButton saveBtn = new JButton("Speichern");
        saveBtn.addActionListener(e -> {
            this.imageModelManager.updateTempModel();
            this.brushSettings.getBrushActive().setValue(false);
            this.brushSettings.getShowMask().setValue(false);
            resetSliderValues();
        });
        wrapper.add(saveBtn);
        return wrapper;
    }

    private void toggleMask () {
        this.brushSettings.getShowMask().setValue(!this.brushSettings.getShowMask().getValue());
    }

    public ValueModel<Boolean> getBrushActive () {
        return this.brushSettings.getBrushActive();
    }
}
