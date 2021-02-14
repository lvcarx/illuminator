package UI;

import Model.ImageModelManager;
import Model.LevelingSettings;
import Model.ValueModel;
import UI.Buttons.BrushesButton;
import UI.Buttons.HorizonButton;
import UI.Buttons.ZoomButton;
import UI.Buttons.ZoomOutButton;
import UI.Resources.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Toolbar extends JPanel {
    private final ImageContainer imageContainer;
    private final ImageModelManager imageModelManager;
    private Float zoomInLevel;
    private final ValueModel<Boolean> brushActive;
    private final LevelingSettings levelingSettings;

    public Toolbar (final ImageModelManager imageModelManager, final ImageContainer imageContainer,
                    final ValueModel<Boolean> brushActive, final LevelingSettings levelingSettings) {
        this.setBackground(Colors.sidebarBackground);
        this.setLayout(new BorderLayout());
        this.imageContainer = imageContainer;
        this.imageModelManager = imageModelManager;
        this.zoomInLevel = 1.0f;
        this.brushActive = brushActive;
        this.levelingSettings = levelingSettings;
        this.setBorder(new EmptyBorder(7, 15, 7, 15));
        this.add(createToolbarButtons(), BorderLayout.EAST);
        this.add(createZoomButtons(), BorderLayout.WEST);
    }

    private Container createToolbarButtons () {
        Container wrapper = new Container();
        wrapper.setLayout(new BorderLayout(10, 0));
        HorizonButton horizonButton = new HorizonButton(this.levelingSettings);
        BrushesButton brushesButton = new BrushesButton(this.brushActive);
        horizonButton.addActionListener(e -> {
            if (!this.brushActive.getValue() && imageModelManager.getModel().getCurrentImg() != null) {
                this.levelingSettings.getStraightenActive().setValue(
                        !this.levelingSettings.getStraightenActive().getValue());
            }
        });
        brushesButton.addActionListener(e -> {
            if (!this.levelingSettings.getStraightenActive().getValue() && imageModelManager.getModel().getCurrentImg() != null) {
                this.imageModelManager.updateTempModel();
                brushActive.setValue(true);
            }
        });
        wrapper.add(horizonButton, BorderLayout.WEST);
        wrapper.add(brushesButton, BorderLayout.EAST);
        return wrapper;
    }

    private Container createZoomButtons () {
        ZoomButton zoomButton = new ZoomButton();
        ZoomOutButton zoomOutButton = new ZoomOutButton();
        Container zoomWrap = new Container();
        zoomWrap.setLayout(new BorderLayout(7, 0));
        zoomWrap.add(zoomButton, BorderLayout.EAST);
        zoomWrap.add(zoomOutButton, BorderLayout.WEST);
        zoomButton.addActionListener(e -> {
            zoomIn(imageContainer);
        });

        zoomOutButton.addActionListener(e -> {
            zoomOut(imageContainer);
        });
        return zoomWrap;
    }

    /**
     * Responsible for zooming in on the image
     *
     * @param imageContainer
     */
    private void zoomIn (final ImageContainer imageContainer) {
        if (this.zoomInLevel < 2.8f) {
            this.zoomInLevel += 0.1f;
            imageContainer.notifyImagePanel();
            imageContainer.setPreferredSize(new Dimension((int) ( imageContainer.getPreferredSize().getWidth() * 1.1f ),
                    (int) ( imageContainer.getPreferredSize().getHeight() * 1.1f )));
            imageContainer.revalidate();
            imageContainer.repaint();
        }
    }

    /**
     * Responsible for zooming out of the image
     *
     * @param imageContainer
     */
    private void zoomOut (final ImageContainer imageContainer) {
        if (this.zoomInLevel > 0.2f) {
            this.zoomInLevel -= 0.1f;
            imageContainer.notifyImagePanel();
            imageContainer.setPreferredSize(new Dimension((int) ( imageContainer.getPreferredSize().getWidth() * 0.9f ),
                    (int) ( imageContainer.getPreferredSize().getHeight() * 0.9f )));
            imageContainer.revalidate();
            imageContainer.repaint();
        }
    }
}
