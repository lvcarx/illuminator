package UI.Tools.LevelingPreview;

import Model.ImageModelManager;
import UI.Resources.Colors;
import hageldave.imagingkit.core.util.ImagePanel;

import javax.swing.*;
import java.awt.*;

public class PreviewContainer extends JPanel {

    private final ImagePanel previewPanel;
    private final ImagePanel currentPanel;

    public PreviewContainer(final ImageModelManager imageModelManager) {
        this.setLayout(new GridLayout(1, 2, 5, 0));
        this.setBackground(Colors.imageBackground);

        this.previewPanel = new ImagePanel();
        this.currentPanel = new ImagePanel();

        this.previewPanel.setBackground(Colors.imageBackground);
        this.currentPanel.setBackground(Colors.imageBackground);

        imageModelManager.getModel().addCurrentImgListener(e-> {
            currentPanel.setImg(e);
        });
        imageModelManager.getModel().addPreviewImgListener(e -> {
            previewPanel.setImg(e);
        });

        this.add(currentPanel);
        this.add(previewPanel);
    }
}
