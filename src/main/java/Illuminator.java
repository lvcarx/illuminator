import Model.*;

import java.awt.*;

import UI.*;
import UI.Resources.Colors;
import UI.Resources.Sizes;
import UI.Sidepanel.BrushMenu;
import UI.Sidepanel.GlobalMenu;
import UI.Sidepanel.SidePanel;
import UI.Tools.LevelingPreview.PreviewContainer;
import UI.Tools.LevelingPreview.PreviewToolbar;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.io.ImageLoader;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Illuminator {

    public static void main(String[] args) {
        startIlluminator();
    }

    private static void attemptSetSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void startIlluminator() {
        /* making sure the application is running on the AWT event dispatching thread */
        SwingUtilities.invokeLater(Illuminator::launchApp);
    }

    private static void launchApp() {
        attemptSetSystemLookAndFeel();
        JFrame frame = new JFrame();
        frame.setTitle("Illuminator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(Sizes.imagePanelSizeWidth, Sizes.imagePanelSizeHeight));
        frame.setVisible(true);
        setupComponents(frame);
    }

    /**
     * Initializes the needed GUI/Model Components
     *
     * @param frame
     */
    private static void setupComponents(JFrame frame) {
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        RestoreStack restoreStack = new RestoreStack();
        ImageModelManager imageModelManager = new ImageModelManager(restoreStack);
        restoreStack.setModels(imageModelManager);

        // straighten tool
        LevelingSettings levelingSettings = new LevelingSettings();

        // brush resources
        BrushSettings brushSettings = new BrushSettings();

        Container leftPanel = new Container();
        Container rightPanel = new Container();
        leftPanel.setLayout(new BorderLayout());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // image container
        JPanel imageContainerWrapper = new JPanel();
        imageContainerWrapper.setBackground(Colors.imageBackground);
        imageContainerWrapper.setLayout(new FlowLayout());
        ImageContainer imageContainer = new ImageContainer(imageModelManager, brushSettings, levelingSettings);
        imageContainerWrapper.add(imageContainer);
        JScrollPane panelPane = new JScrollPane(imageContainerWrapper);
        panelPane.setBackground(Colors.imageBackground);

        // Sidepanel
        GlobalMenu sidepanel = new GlobalMenu(imageModelManager, brushSettings, imageContainer);
        JScrollPane sideMenuPanel = new JScrollPane();
        sideMenuPanel.setBackground(Colors.sidebarBackground);
        sideMenuPanel.setViewportView(sidepanel);

        // Brushmenu
        BrushMenu brushMenu = new BrushMenu(imageModelManager, brushSettings, imageContainer);
        JScrollPane brushMenuPanel = new JScrollPane();
        brushMenuPanel.setBackground(Colors.sidebarBackground);
        brushMenuPanel.setViewportView(brushMenu);

        // standard toolbar
        Toolbar standardToolbar = new Toolbar(imageModelManager, imageContainer, brushSettings.getBrushActive(), levelingSettings);

        // Bottom menu
        BottomMenu bottomMenu = new BottomMenu(imageModelManager, imageContainer, sidepanel, brushMenu);

        // Preview Toolbar
        PreviewToolbar previewToolbar = new PreviewToolbar(imageModelManager, levelingSettings);

        // Preview Container
        PreviewContainer previewContainer = new PreviewContainer(imageModelManager);

        // configure left panel
        leftPanel.add(standardToolbar, BorderLayout.NORTH);
        leftPanel.add(panelPane, BorderLayout.CENTER);
        leftPanel.add(bottomMenu, BorderLayout.SOUTH);

        // switches side- and brushpanel, when needed
        brushSettings.getBrushActive().addValueListener(e -> {
            if (brushSettings.getBrushActive().getValue()) {
                rightPanel.removeAll();
                rightPanel.add(brushMenuPanel);
                rightPanel.revalidate();
                rightPanel.repaint();
            } else {
                brushSettings.getBrushActive().setValue(false);
                rightPanel.removeAll();
                rightPanel.add(sideMenuPanel);
                rightPanel.revalidate();
                rightPanel.repaint();
                imageModelManager.resetMasks();
            }
        });

        rightPanel.add(sideMenuPanel);
        rightPanel.setPreferredSize(new Dimension(Sizes.sidePanelWidth, 30));

        // hides sidepanel when leveling is activated
        levelingSettings.getPreviewActive().addValueListener(e -> {
            leftPanel.removeAll();
            if (levelingSettings.getPreviewActive().getValue()) {
                rightPanel.setPreferredSize(new Dimension(0,0));
                leftPanel.add(previewToolbar, BorderLayout.NORTH);
                leftPanel.add(previewContainer, BorderLayout.CENTER);
            } else {
                rightPanel.setPreferredSize(new Dimension(Sizes.sidePanelWidth, 30));
                leftPanel.add(standardToolbar, BorderLayout.NORTH);
                leftPanel.add(panelPane, BorderLayout.CENTER);
                leftPanel.add(bottomMenu, BorderLayout.SOUTH);
            }
            rightPanel.revalidate();
            rightPanel.repaint();
            leftPanel.revalidate();
            leftPanel.repaint();
            imageContainer.repaint();
        });

        contentPane.add(leftPanel, BorderLayout.CENTER);
        contentPane.add(rightPanel, BorderLayout.EAST);

        imageContainer.getPanel().setPreferredSize(new Dimension(contentPane.getWidth() - (int) rightPanel.getPreferredSize().getWidth() - 20,
                    contentPane.getWidth() - (int) rightPanel.getPreferredSize().getWidth()));
        imageContainer.revalidate();
        createMenu(frame, imageModelManager, brushSettings, levelingSettings, sidepanel, brushMenu);
    }

    /**
     * resets all settings (when new image is loaded)
     *
     * @param brushSettings
     * @param levelingSettings
     * @param sideMenu
     * @param brushMenu
     */
    private static void resetSettings(final BrushSettings brushSettings, final LevelingSettings levelingSettings, final SidePanel sideMenu, final SidePanel brushMenu) {
        sideMenu.resetSliderValues();
        brushMenu.resetSliderValues();
        brushSettings.resetSettings();
        levelingSettings.resetSettings();
    }

    private static void createMenu(final JFrame frame, final ImageModelManager imageModelManager, final BrushSettings brushSettings,
                                   final LevelingSettings levelingSettings, final SidePanel sideMenu, final SidePanel brushMenu) {
        JMenuBar menubar = new JMenuBar();
        JMenu filemenu = new JMenu("Datei");
        createLoadImageMenuItem(frame, filemenu, imageModelManager, brushSettings, levelingSettings, sideMenu, brushMenu);
        menubar.add(filemenu);
        frame.setJMenuBar(menubar);
    }

    private static void createLoadImageMenuItem(JFrame frame, JMenu menu, final ImageModelManager imageModelManager, final BrushSettings brushSettings,
                                                final LevelingSettings levelingSettings, final SidePanel sideMenu, final SidePanel brushMenu) {
        JMenuItem loadItem = new JMenuItem("Datei öffnen...");
        loadItem.addActionListener(e->{
            FileDialog dialog = new FileDialog(frame, "Open Image", FileDialog.LOAD);
            dialog.setVisible(true);
            File[] files = dialog.getFiles();
            if(files.length > 0) {
                try {
                    BufferedImage bimg = ImageLoader.loadImage(files[0], BufferedImage.TYPE_INT_ARGB);
                    resetSettings(brushSettings, levelingSettings, sideMenu, brushMenu);
                    imageModelManager.updateAllModels(Img.createRemoteImg(bimg));
                } catch(ImageLoader.ImageLoaderException ex) {
                    ex.printStackTrace();
                }
            }
        });
        menu.add(loadItem);
    }
}
