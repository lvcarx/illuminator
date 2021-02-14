package UI.Tools.ManipulatorSliders;

import Filters.SaturationFilter;
import Model.ImageModelManager;
import Model.ImgEditingModel;
import Model.ValueModel;

import javax.swing.*;

public class Saturation extends Manipulator {
    public Saturation(final ImgEditingModel imgEditingModel) {
        super(imgEditingModel, "Sättigung");
    }
}
