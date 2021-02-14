package UI.Tools.ManipulatorSliders;

import Filters.BrightnessFilter;
import Model.ImageModelManager;
import Model.ImgEditingModel;
import Model.ValueModel;

import javax.swing.*;

public class Brightness extends Manipulator {
    public Brightness(final ImgEditingModel imgEditingModel) {
        super(imgEditingModel, "Helligkeit");
    }
}
