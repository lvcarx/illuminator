package UI.Buttons;

import Model.LevelingSettings;
import UI.Resources.Sizes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HorizonButton extends JButton {
    private BufferedImage image;
    private LevelingSettings levelingSettings;

    public HorizonButton(final LevelingSettings levelingSettings) {
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/horizon-white.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.levelingSettings = levelingSettings;
        this.setPreferredSize(new Dimension(Sizes.iconSize, Sizes.iconSize));
        this.setContentAreaFilled(false);
        this.levelingSettings.getStraightenActive().addValueListener(e -> {
            if (this.levelingSettings.getStraightenActive().getValue()) {
                try {
                    image = ImageIO.read(this.getClass().getResourceAsStream("/horizon_active.png"));
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            } else {
                try {
                    image = ImageIO.read(this.getClass().getResourceAsStream("/horizon-white.png"));
                } catch (IOException exce) {
                    exce.printStackTrace();
                }
            }
            this.repaint();
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(this.image, 0, 0, Sizes.iconSize, Sizes.iconSize, this);
    }

}
