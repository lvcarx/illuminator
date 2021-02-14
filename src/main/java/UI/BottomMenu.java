package UI;

import Model.ImageModelManager;
import UI.Resources.Colors;
import UI.Sidepanel.BrushMenu;
import UI.Sidepanel.GlobalMenu;
import hageldave.imagingkit.core.io.ImageSaver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *  Bottom menu which hosts the save and undo button
 */
public class BottomMenu extends JPanel {
    private final ImageModelManager imageModelManager;
    private final ImageContainer imageContainer;
    private final BrushMenu brushMenu;
    private final GlobalMenu globalMenu;

    public BottomMenu(final ImageModelManager imageModelManager, final ImageContainer imageContainer, final GlobalMenu globalMenu, final BrushMenu brushMenu) {
        this.imageModelManager = imageModelManager;
        this.imageContainer = imageContainer;
        this.brushMenu = brushMenu;
        this.globalMenu = globalMenu;
        this.setBackground(Colors.bottomBarBackground);
        this.setLayout(new BorderLayout());
        Container wrapper = createComponents();
        this.setBorder(new EmptyBorder(4, 4, 4, 4));
        this.add(wrapper, BorderLayout.SOUTH);
    }

    private Container createComponents() {
        Container container = new Container();
        container.setLayout(new BorderLayout());
        JButton saveBtn = this.createSaveBtn();
        JButton undoButton = this.createUndoBtn();
        container.add(saveBtn, BorderLayout.EAST);
        container.add(undoButton, BorderLayout.WEST);
        return container;
    }

    private JButton createSaveBtn() {
        JButton saveButton = new JButton();
        saveButton.setText("Speichern");
        saveButton.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog(new Frame(), "Datei speichern", FileDialog.SAVE);
            fileDialog.setFile("myImage.png");
            fileDialog.setVisible(true);
            if (fileDialog.getFile() != null) {
                ImageSaver.saveImage(this.imageModelManager.getModel().getCurrentImg().getRemoteBufferedImage(), fileDialog.getDirectory() + fileDialog.getFile());
            }
        });
        return saveButton;
    }

    private JButton createUndoBtn () {
        JButton undoButton = new JButton("Rückgängig machen");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> {
            this.imageModelManager.getRestoreStack().restoreLatestImage();
            this.imageContainer.notifyImagePanel();
            this.globalMenu.resetSliderValues();
            this.brushMenu.resetSliderValues();
        });
        this.imageModelManager.getRestoreStack().getStackListener().addValueListener(e -> {
            if (this.imageModelManager.getRestoreStack().getLength() > 1) {
                undoButton.setEnabled(true);
            } else {
                undoButton.setEnabled(false);
            }
        });
        return undoButton;
    }
}
