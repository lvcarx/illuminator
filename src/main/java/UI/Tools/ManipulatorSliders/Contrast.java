package UI.Tools.ManipulatorSliders;

import Filters.ContrastFilter;
import Model.ImageModelManager;
import Model.ImgEditingModel;
import Model.ValueModel;

import javax.swing.*;

public class Contrast extends Manipulator {
    public Contrast(final ImgEditingModel imgEditingModel) {
        super(imgEditingModel, "Kontrast");
    }
}
