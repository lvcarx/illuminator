package UI.Tools.ManipulatorSliders;

import Filters.UniversalFilter;
import Model.ImgEditingModel;
import Model.ValueModel;
import UI.Resources.Colors;
import hageldave.imagingkit.core.Img;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * GUI component of each manipulator slider
 */
public abstract class Manipulator extends Container {
    private final ImgEditingModel imgEditingModel;
    private final ValueModel<Integer> sliderVal;
    private JSlider jslider;
    private UniversalFilter filter;

    public Manipulator(final ImgEditingModel imgEditingModel,
                       final String manipulatorName) {
        this.imgEditingModel = imgEditingModel;
        this.sliderVal = new ValueModel<Integer>(100);
        this.jslider = new JSlider(0, 200, this.sliderVal.getValue());

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        Container sliderAndValWrap = new Container();
        sliderAndValWrap.setLayout(new FlowLayout());

        Box box = Box.createHorizontalBox();
        JLabel brightnessLabel = new JLabel(manipulatorName + ": ");
        brightnessLabel.setForeground(Colors.textColor);
        this.jslider = new JSlider(0, 200, this.sliderVal.getValue());
        JLabel sliderLabel = new JLabel(this.sliderVal.getValue().toString());
        sliderAndValWrap.add(this.jslider);
        sliderLabel.setForeground(Colors.textColor);
        this.jslider.addChangeListener(e -> {
            this.sliderVal.setValue(this.jslider.getValue());
            if (this.imgEditingModel.getCurrentImg() != null && this.filter != null) {
                manipulateImage(this.filter);
            }
        });
        this.sliderVal.addValueListener(e -> {
            jslider.setValue(this.sliderVal.getValue());
            sliderLabel.setText(e.toString());
        });

        box.add(brightnessLabel);
        box.add(sliderLabel);
        box.add(Box.createHorizontalGlue());
        box.setBorder(new EmptyBorder(10, 20, 5, 0));
        this.add(box);
        this.add(sliderAndValWrap);
    }

    /**
     * manipulates image with given filter and sets it to current image
     *
     * @param filter
     */
    private void manipulateImage(final UniversalFilter filter) {
        Img newImage = filter.manipulateImg();
        this.imgEditingModel.setCurrentImg(newImage);
    }

    public void setFilter(final UniversalFilter filter) {
        this.filter = filter;
    }

    public void resetManipulator() {
        this.sliderVal.setValue(100);
    }

    public ValueModel<Integer> getSliderVal () {
        return sliderVal;
    }
}
