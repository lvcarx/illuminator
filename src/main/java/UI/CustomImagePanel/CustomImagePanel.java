package UI.CustomImagePanel;

import Filters.BlendingFilter;
import Filters.BlurFilter;
import Leveling.Leveling;
import Model.BrushSettings;
import Model.ImageModelManager;
import Model.LevelingSettings;
import Model.ValueModel;
import UI.Resources.Colors;
import UI.Resources.Sizes;
import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;
import hageldave.imagingkit.core.util.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;


/**
 * Extended version of ImagePanel with abilities to display & register brushes and leveling points
 */
public class CustomImagePanel extends ImagePanel {
    private final ImageModelManager imageModelManager;
    private final Point mousePos = new Point(0, 0);
    private final BrushSettings brushSettings;
    private boolean mouseClicked = false;
    private Integer imageWidth;
    private Integer imageHeight;
    private double factorWidth;
    private double factorHeight;

    private BlendingFilter blendingFilter;
    private final BlurFilter blurFilter;

    // resources for straighten
    private final LevelingSettings levelingSettings;

    private Point startPoint = new Point();
    private Point endPoint = new Point();
    private boolean startPointSet = false;
    private boolean endPointSet = false;

    // precomputed values for faster displaying of brush in paint method
    int preComputedSizeHeight;
    int preComputedSizeHeightHalf;
    int preComputedWidth;
    int preComputedWidthHalf;
    float brushSizeHalf;
    int offsetWidth;
    int offsetHeight;

    public CustomImagePanel (final ImageModelManager imageModelManager, final BrushSettings brushSettings, final LevelingSettings levelingSettings) {
        super();
        this.imageModelManager = imageModelManager;
        this.brushSettings = brushSettings;
        this.levelingSettings = levelingSettings;
        this.blurFilter = new BlurFilter(35);

        recalculatePrecomputedValues();

        this.brushSettings.getBrushActive().addValueListener(e -> {
            if (this.brushSettings.getBrushActive().getValue()) {
                if (this.imageModelManager.getBrushMask().getCurrentImg() != null) {
                    calcFactorWidth();
                    calcFactorHeight();

                    this.offsetWidth = (int) ( ( ( this.getPanelHeight() / getFactorWidth() ) - this.imageHeight ) / 2 );
                    this.offsetHeight = (int) ( ( ( this.getPanelWidth() / getFactorHeight() ) - this.imageWidth ) / 2 );
                }
                blendingFilter = new BlendingFilter(this.imageModelManager.getBrushEditedImage().getCurrentImg());
            }
        });

        this.imageModelManager.getBrushMask().addCurrentImgListener(e -> {
            if (this.brushSettings.getBrushActive().getValue()) {
                Img blendedImage = blendImage();
                this.imageModelManager.getModel().setCurrentImg(blendedImage);
            }
        });

        this.brushSettings.getBrushSize().addValueListener(e -> {
            recalculatePrecomputedValues();
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked (MouseEvent e) {
                super.mouseClicked(e);
                if (getStraightenActive().getValue()) {
                    if (!isStartPointSet() && !isEndPointSet()) {
                        setStartingPoint(e.getPoint());
                    }
                    if (isStartPointSet() && isEndPointSet()) {
                        turnImage();
                        resetHorizonPoints();
                    }
                }
            }

            @Override
            public void mousePressed (MouseEvent e) {
                if (getImageModelManager().getBrushMask().getCurrentImg() != null) {
                    calcFactorHeight();
                    calcFactorWidth();
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    CustomImagePanel.super.clickPoint = null;
                }
                if (getBrushActive().getValue()) {
                    mouseClicked = true;
                    updateMousePosition(e);
                    drawOnMask(e);
                    getImageModelManager().getModel().setCurrentImg(blendImage());
                    repaint();
                }
            }

            @Override
            public void mouseReleased (MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    CustomImagePanel.super.clickPoint = null;
                }
                if (getBrushActive().getValue() && !getStraightenActive().getValue()) {
                    mouseClicked = false;
                    updateMousePosition(e);
                    repaint();
                }
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged (MouseEvent e) {
                if (clickPoint != null) {
                    CustomImagePanel.super.clickPoint = null;
                }
                if (getBrushActive().getValue()) {
                    drawOnMask(e);
                    getImageModelManager().getModel().setCurrentImg(blendImage());
                    repaint();
                }
            }

            @Override
            public void mouseMoved (MouseEvent e) {
                super.mouseMoved(e);
                if (isStartPointSet()) {
                    // calc line from startPoint to this
                    setEndPoint(e.getPoint());
                    repaint();
                }
            }
        });
    }

    /**
     * added the ability to display leveling line or brush
     *
     * @param g
     */
    @Override
    public void paint (Graphics g) {
        super.paint(g);
        if (mouseClicked && this.brushSettings.getBrushActive().getValue()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            Ellipse2D.Double brush = new Ellipse2D.Double(this.mousePos.getX() - this.brushSizeHalf,
                    this.mousePos.getY() - this.brushSizeHalf,
                    this.brushSettings.getBrushSize().getValue(), this.brushSettings.getBrushSize().getValue());
            g2d.draw(brush);
        } else if (this.levelingSettings.getStraightenActive().getValue()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Colors.levelingPoint);
            if (this.startPointSet) {
                Ellipse2D.Double startingPoint = new Ellipse2D.Double(this.startPoint.getX() - (float) Sizes.levelingPoints / 2, this.startPoint.getY() - (float) Sizes.levelingPoints / 2,
                        Sizes.levelingPoints, Sizes.levelingPoints);
                g2d.fill(startingPoint);
            }
            if (this.endPointSet) {
                Ellipse2D.Double endPoint = new Ellipse2D.Double(this.endPoint.getX() - (float) Sizes.levelingPoints / 2, this.endPoint.getY() - (float) Sizes.levelingPoints / 2,
                        Sizes.levelingPoints, Sizes.levelingPoints);
                g2d.fill(endPoint);

                g2d.setStroke(new BasicStroke(2));
                Line2D.Double line = new Line2D.Double(this.startPoint.getX(), this.startPoint.getY(), this.endPoint.getX(), this.endPoint.getY());
                g2d.draw(line);
            }
        }
    }

    /**
     * @param event
     */
    private void updateMousePosition (final MouseEvent event) {
        this.mousePos.setLocation(event.getX(), event.getY());
    }

    /**
     * draws on the mask image on current mouse positions
     *
     * @param f
     */
    private void drawOnMask (final MouseEvent f) {
        this.mousePos.setLocation(f.getX(), f.getY());
        Img drawImage = this.imageModelManager.getTempBrushMask().getCurrentImg();
        drawImage.paint(e -> {
            if (this.brushSettings.getBrushMode().getValue()) {
                e.setColor(Color.BLACK);
            } else {
                e.setColor(Color.WHITE);
            }
            e.setStroke(new BasicStroke(this.brushSettings.getBrushSize().getValue()));
            Ellipse2D.Double point;

            if (imageWidth > imageHeight) {
                point = new Ellipse2D.Double(( f.getX() / getFactorWidth() - this.preComputedWidthHalf ),
                        ( ( f.getY() / getFactorWidth() ) - this.offsetWidth - this.preComputedWidthHalf ),
                        this.preComputedWidth, this.preComputedWidth);
            } else {
                point = new Ellipse2D.Double(( f.getX() / getFactorHeight() - this.offsetHeight - this.preComputedSizeHeightHalf ),
                        ( ( f.getY() / getFactorHeight() ) - this.preComputedSizeHeightHalf ), this.preComputedSizeHeight, this.preComputedSizeHeight);
            }
            e.fill(point);
        });
    }

    /**
     * Blends the brush image with the mask
     *
     * @return blended image
     */
    public Img blendImage () {
        Img copiedEditedImg = this.imageModelManager.getBrushEditedImage().getCurrentImg();
        Img brushImg = this.imageModelManager.getTempBrushMask().getCurrentImg();
        Img blendedImage = null;
        // copies rgb elements to the alpha channel, so that less operations have to be done, when blurring the image
        if (brushImg != null) {
            brushImg.stream().parallel().forEach(px -> {
                Pixel ePx = copiedEditedImg.getPixel(px.getX(), px.getY());
                ePx.setA(0);
                Pixel pix = brushImg.getPixel(px.getX(), px.getY());
                double greyValue = pix.getLuminance();
                ePx = copiedEditedImg.getPixel(px.getX(), px.getY());
                greyValue = greyValue / 255;
                ePx.setA_fromDouble(greyValue);
            });
            // blur here bc values are in alpha channel
            Img blurredImage = this.blurFilter.manipulateAlpha(copiedEditedImg);
            blendedImage = this.blendingFilter.manipulateImg(blurredImage);
        }
        return blendedImage;
    }

    private void turnImage () {
        this.levelingSettings.getPreviewActive().setValue(true);
        Img turnedImg = Leveling.turnImage(Leveling.calcAngle(this.startPoint, this.endPoint),
                this.imageModelManager.getModel().getCurrentImg());
        this.imageModelManager.getModel().setPreviewImg(turnedImg);
    }

    private void resetHorizonPoints () {
        this.startPoint = new Point();
        this.endPoint = new Point();
        this.startPointSet = false;
        this.endPointSet = false;
    }


    /* GETTERS AND SETTERS */

    private void setStartingPoint (final Point point) {
        this.startPoint = point;
        this.startPointSet = true;
    }

    private void setEndPoint (final Point point) {
        this.endPoint = point;
        this.endPointSet = true;
    }

    public ValueModel<Boolean> getBrushActive () {
        return this.brushSettings.getBrushActive();
    }

    public void setImageSize (final Integer imageWidth, final Integer imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * calculates scaling factor between imagepanel and brushmask image (width)
     */
    public void calcFactorWidth () {
        if (this.imageModelManager.getBrushMask().getCurrentImg() != null) {
            this.factorWidth = (double) this.getWidth() / this.imageModelManager.getBrushMask().getCurrentImg().getWidth();
            recalculatePrecomputedValues();
        }
    }

    /**
     * calculates scaling factor between imagepanel and brushmask image (height)
     */
    public void calcFactorHeight () {
        if (this.imageModelManager.getBrushMask().getCurrentImg() != null) {
            this.factorHeight = (double) this.getHeight() / ( this.imageModelManager.getBrushMask().getCurrentImg().getHeight() );
            recalculatePrecomputedValues();
        }
    }

    /**
     * updates precomputed values
     */
    private void recalculatePrecomputedValues () {
        this.preComputedSizeHeight = (int) ( this.brushSettings.getBrushSize().getValue() / factorHeight );
        this.preComputedSizeHeightHalf = (int) ( ( this.brushSettings.getBrushSize().getValue() / factorHeight ) / 2 );
        this.preComputedWidth = (int) ( this.brushSettings.getBrushSize().getValue() / factorWidth );
        this.preComputedWidthHalf = (int) ( ( this.brushSettings.getBrushSize().getValue() / factorWidth ) / 2 );
        this.brushSizeHalf = (float) ( this.brushSettings.getBrushSize().getValue() / 2 );
    }

    public Integer getPanelHeight () {
        return this.getHeight();
    }

    public Integer getPanelWidth () {
        return this.getWidth();
    }

    public double getFactorWidth () {
        return factorWidth;
    }

    public double getFactorHeight () {
        return factorHeight;
    }

    public ValueModel<Boolean> getStraightenActive () {
        return levelingSettings.getStraightenActive();
    }

    public ImageModelManager getImageModelManager () {
        return imageModelManager;
    }

    public boolean isStartPointSet () {
        return startPointSet;
    }

    public boolean isEndPointSet () {
        return endPointSet;
    }
}
