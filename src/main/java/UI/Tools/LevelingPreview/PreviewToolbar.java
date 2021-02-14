package UI.Tools.LevelingPreview;

import Model.ImageModelManager;
import Model.LevelingSettings;
import UI.Resources.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PreviewToolbar extends JPanel {
    private final ImageModelManager imageModelManager;
    private final LevelingSettings levelingSettings;

    public PreviewToolbar(final ImageModelManager imageModelManager, final LevelingSettings levelingSettings) {
        this.setBackground(Colors.sidebarBackground);
        this.setLayout(new BorderLayout());
        this.imageModelManager = imageModelManager;
        this.levelingSettings = levelingSettings;

        this.setBorder(new EmptyBorder(7, 15, 7, 15));
        this.add(createConfirmButton(), BorderLayout.EAST);
        this.add(createAbortButton(), BorderLayout.WEST);
        this.add(createLabel(), BorderLayout.CENTER);
    }

    private JButton createConfirmButton() {
        JButton confirmButton = new JButton("Bestätigen");
        confirmButton.addActionListener(e -> {
            this.levelingSettings.getPreviewActive().setValue(false);
            this.levelingSettings.getStraightenActive().setValue(false);
            this.imageModelManager.updateTempModel();
            this.imageModelManager.getModel().setCurrentImg(this.imageModelManager.getModel().getPreviewImg());
            this.imageModelManager.updateTempModel();
            this.imageModelManager.resetMasks();
        });
        return confirmButton;
    }

    private JLabel createLabel() {
        JLabel heading = new JLabel("Bilddrehung übernehmen?");
        heading.setForeground(Colors.textColor);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        return heading;
    }

    private JButton createAbortButton() {
        JButton abortButton = new JButton("Abbrechen");
        abortButton.addActionListener(e -> {
            this.levelingSettings.getStraightenActive().setValue(false);
            this.levelingSettings.getPreviewActive().setValue(false);
        });
        return abortButton;
    }
}
