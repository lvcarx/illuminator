package UI.Buttons;

import UI.Resources.Sizes;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ZoomButton extends JButton {
    private BufferedImage image;

    public ZoomButton() {
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/zoom-white.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setContentAreaFilled(false);
        this.setPreferredSize(new Dimension(Sizes.iconSize, Sizes.iconSize));
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
