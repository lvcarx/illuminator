package UI;

import Model.BrushSettings;
import Model.ImageModelManager;
import Model.LevelingSettings;
import UI.CustomImagePanel.CustomImagePanel;
import UI.Resources.Colors;
import hageldave.imagingkit.core.util.ImagePanel;

import java.awt.*;

public class ImageContainer extends Container {
    private final ImageModelManager imageModelManager;
    private final CustomImagePanel panel;
    private final BrushSettings brushSettings;

    public ImageContainer(final ImageModelManager imageModelManager, final BrushSettings brushSettings, final LevelingSettings levelingSettings) {
        this.imageModelManager = imageModelManager;
        this.brushSettings = brushSettings;
        this.panel = new CustomImagePanel(imageModelManager, brushSettings, levelingSettings);
        this.panel.setBackground(Colors.imageBackground);

        imageModelManager.getModel().addCurrentImgListener(i -> {
            if (!this.brushSettings.getShowMask().getValue() && i != null) {
                panel.setImg(i);
                panel.setImageSize(i.getWidth(), i.getHeight());
            }
        });
        this.brushSettings.getShowMask().addValueListener(e -> {
            if (this.brushSettings.getShowMask().getValue()) {
                if (imageModelManager.getBrushMask().getCurrentImg() != null) {
                    panel.setImg(imageModelManager.getBrushMask().getCurrentImg());
                }
            } else {
                if (imageModelManager.getModel().getCurrentImg() != null) {
                    panel.setImg(this.imageModelManager.getModel().getCurrentImg());
                }
            }
        });
        imageModelManager.getBrushMask().addCurrentImgListener(e -> {
            if (this.brushSettings.getShowMask().getValue()) {
                if (imageModelManager.getBrushMask().getCurrentImg() != null) {
                    panel.setImg(e);
                }
            } else {
                if (imageModelManager.getModel().getCurrentImg() != null) {
                    panel.setImg(this.imageModelManager.getModel().getCurrentImg());
                }
            }
        });
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    /**
     * notifies the image panel that a change in size (e.g. zoom) has happened
     */
    public void notifyImagePanel() {
        this.panel.calcFactorHeight();
        this.panel.calcFactorWidth();
    }
    public ImagePanel getPanel() {
        return panel;
    }
}
