package UI.Buttons;

import Model.ValueModel;
import UI.Resources.Sizes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BrushesButton extends JButton {
    private BufferedImage image;
    private final ValueModel<Boolean> brushActive;

    public BrushesButton(final ValueModel<Boolean> brushActive) {
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/brush-white.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.brushActive = brushActive;
        this.setPreferredSize(new Dimension(Sizes.iconSize, Sizes.iconSize));
        this.setContentAreaFilled(false);
        this.brushActive.addValueListener(e -> {
            if (this.brushActive.getValue()) {
                try {
                    image = ImageIO.read(this.getClass().getResourceAsStream("/brush_active.png"));
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            } else {
                try {
                    image = ImageIO.read(this.getClass().getResourceAsStream("/brush-white.png"));
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
