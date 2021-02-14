package UI.Tools;

import Model.ValueModel;
import UI.Resources.Colors;
import UI.Resources.Sizes;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * GUI Component of the gradation curve
 */
public class Colorcurve extends JPanel {
    private Mode mode;
    private final LinkedList<Point> pointsRGB = new LinkedList<Point>();
    private final LinkedList<Line2D.Double> curvesRGB = new LinkedList<Line2D.Double>();
    private final HashMap<Integer, Integer> curveMapRGB;

    private final LinkedList<Point> pointsRed = new LinkedList<Point>();
    private final LinkedList<Line2D.Double> curvesRed = new LinkedList<Line2D.Double>();
    private final HashMap<Integer, Integer> curveMapRed;

    private final LinkedList<Point> pointsGreen = new LinkedList<Point>();
    private final LinkedList<Line2D.Double> curvesGreen = new LinkedList<Line2D.Double>();
    private final HashMap<Integer, Integer> curveMapGreen;

    private final LinkedList<Point> pointsBlue = new LinkedList<Point>();
    private final LinkedList<Line2D.Double> curvesBlue = new LinkedList<Line2D.Double>();
    private final HashMap<Integer, Integer> curveMapBlue;

    private Optional<Point> pointPipeline = Optional.empty();
    private final ValueModel<Boolean> isPipelineEmpty = new ValueModel<Boolean>(false);
    private final ValueModel<Boolean> curveMapRGBChanged = new ValueModel<Boolean>(false);
    private final ValueModel<Boolean> curveMapRedChanged = new ValueModel<Boolean>(false);
    private final ValueModel<Boolean> curveMapGreenChanged = new ValueModel<Boolean>(false);
    private final ValueModel<Boolean> curveMapBlueChanged = new ValueModel<Boolean>(false);

    private boolean initialCalc = false;

    public enum Mode {
        RGB,
        RED,
        GREEN,
        BLUE
    }

    public Colorcurve (final Mode mode) {
        this.mode = mode;
        this.curveMapRGB = new HashMap<Integer, Integer>();
        this.curveMapRed = new HashMap<Integer, Integer>();
        this.curveMapGreen = new HashMap<Integer, Integer>();
        this.curveMapBlue = new HashMap<Integer, Integer>();

        this.setBackground(Colors.sidebarBackground);
        this.setPreferredSize(new Dimension(Sizes.gradationSize, Sizes.gradationSize));
        addStartingPoints();
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked (MouseEvent e) {

            }

            @Override
            public void mousePressed (MouseEvent e) {
                clearPipeline();
                setPipelineModelEmpty();
                if (getMode() == Mode.RGB) {
                    rearrangeCurve(e, curveMapRGB, pointsRGB);
                } else if (getMode() == Mode.RED) {
                    rearrangeCurve(e, curveMapRed, pointsRed);
                } else if (getMode() == Mode.GREEN) {
                    rearrangeCurve(e, curveMapGreen, pointsGreen);
                } else if (getMode() == Mode.BLUE) {
                    rearrangeCurve(e, curveMapBlue, pointsBlue);
                }
                repaint();
            }

            @Override
            public void mouseReleased (MouseEvent e) {
                e.getComponent().setCursor(new Cursor(Cursor.MOVE_CURSOR));
                if (getMode() == Mode.RED) {
                    processMovement(e, getPointsRed());
                    calculateMap(getPointsRed(), getCurveMapRed(), getCurveMapRedChanged());
                } else if (getMode() == Mode.GREEN) {
                    processMovement(e, getPointsGreen());
                    calculateMap(getPointsGreen(), getCurveMapGreen(), getCurveMapGreenChanged());
                } else if (getMode() == Mode.BLUE) {
                    processMovement(e, getPointsBlue());
                    calculateMap(getPointsBlue(), getCurveMapBlue(), getCurveMapBlueChanged());
                } else {
                    processMovement(e, getPointsRGB());
                    calculateMap(getPointsRGB(), getCurveMapRGB(), getCurveMapRGBChanged());
                }

            }

            @Override
            public void mouseEntered (MouseEvent e) {

            }

            @Override
            public void mouseExited (MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged (MouseEvent e) {
                e.getComponent().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                if (getMode() == Mode.RED) {
                    processMovement(e, getPointsRed());
                    calculateMap(getPointsRed(), getCurveMapRed(), getCurveMapRedChanged());
                } else if (getMode() == Mode.GREEN) {
                    processMovement(e, getPointsGreen());
                    calculateMap(getPointsGreen(), getCurveMapGreen(), getCurveMapGreenChanged());
                } else if (getMode() == Mode.BLUE) {
                    processMovement(e, getPointsBlue());
                    calculateMap(getPointsBlue(), getCurveMapBlue(), getCurveMapBlueChanged());
                } else {
                    processMovement(e, getPointsRGB());
                    calculateMap(getPointsRGB(), getCurveMapRGB(), getCurveMapRGBChanged());
                }
            }

            @Override
            public void mouseMoved (MouseEvent e) {

            }
        });
    }

    /**
     *
     * @param e
     * @param curveMap
     * @param points
     */
    private void rearrangeCurve(final MouseEvent e, final HashMap<Integer, Integer> curveMap, final LinkedList<Point> points) {
        if (Math.abs(curveMap.get(e.getX()) - e.getY()) < 7) {
            if (!containsX(points, e.getX()) && (e.getX() > 0 && e.getX() < 255)  && (e.getY() > 0 && e.getY() < 255)) {
                points.add(new Point(e.getX(), curveMap.get(e.getX())));
                sortPoints();
            }
        }
        if (containsX(points, e.getX()) && containsY(points, e.getY())) {
            pointPipeline = Optional.of(getPoint(points, e.getX()));
            setPipelineModelFull();
        }
    }

    /**
     * Sorts the points in the corresponding pointList after their x coordinate,
     * so that the calculating of the curve works correctly
     */
    private void sortPoints () {
        // sort points after x
        Collections.sort(this.pointsRGB, (o1, o2) -> (int) (o1.getX() - o2.getX()));
        Collections.sort(this.pointsRed, (o1, o2) -> (int) (o1.getX() - o2.getX()));
        Collections.sort(this.pointsGreen, (o1, o2) -> (int) (o1.getX() - o2.getX()));
        Collections.sort(this.pointsBlue, (o1, o2) -> (int) (o1.getX() - o2.getX()));
    }

    /**
     * sets the new location of the point in the pipeline
     *
     * @param e
     * @param pointList
     */
    private void processMovement(MouseEvent e, final LinkedList<Point> pointList) {
        if (this.pointPipeline.isPresent()) {
            if (e.getX() > 0 && e.getX() < 255  && e.getY() > 0 && e.getY() < 255) {
                int pointIndex;
                // get pointindex of corresponding pointlist
                pointIndex = indexOf(this.pointPipeline.get(), pointList);

                if (pointIndex > 0 && pointList.size() > 2) {
                    if (this.pointPipeline.get().getX() == pointList.get(pointIndex - 1).getX() ||
                            this.pointPipeline.get().getX() < pointList.get(pointIndex - 1).getX()) {
                        this.pointPipeline.get().setLocation(pointList.get(pointIndex - 1).getX() + 5, e.getY());
                    } else if (this.pointPipeline.get().getX() == pointList.get(pointIndex + 1).getX() ||
                            this.pointPipeline.get().getX() > pointList.get(pointIndex + 1).getX()) {
                        this.pointPipeline.get().setLocation(pointList.get(pointIndex + 1).getX() - 5, e.getY());
                    } else {
                        this.pointPipeline.get().setLocation(e.getX(), e.getY());
                    }
                }
            }
            repaint();
        }
    }

    /**
     *
     * @param point
     * @param pointList
     * @return index of given point in the list
     */
    private int indexOf(final Point point, final LinkedList<Point> pointList) {
        int index = 0;
        for (Point pointInList : pointList) {
            if (pointInList.equals(point)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void clearPipeline() {
        this.pointPipeline = Optional.empty();
    }

    public boolean containsX(final LinkedList<Point> list, final Integer X){
        return list.stream().anyMatch(o -> Math.abs(o.getX() - X) < 7);
    }

    public boolean containsY(final LinkedList<Point> list, final Integer Y){
        return list.stream().anyMatch(o -> Math.abs(o.getY() - Y) < 7);
    }

    public Point getPoint(final LinkedList<Point> list, final Integer X){
        return list.stream().filter(o -> Math.abs(o.getX() - X) < 7).findFirst().get();
    }

    /**
     * removes point that is currently in the pipe and recalculates map
     */
    public void removePointInPipe() {
        Point pointToDelete = this.pointPipeline.get();
        if (getMode() == Mode.RED) {
            this.pointsRed.remove(pointToDelete);
            repaint();
            calculateMap(getPointsRed(), getCurveMapRed(), this.curveMapRedChanged);
        } else if (getMode() == Mode.GREEN) {
            this.pointsGreen.remove(pointToDelete);
            repaint();
            calculateMap(getPointsGreen(), getCurveMapGreen(), this.curveMapGreenChanged);
        } else if (getMode() == Mode.BLUE) {
            this.pointsBlue.remove(pointToDelete);
            repaint();
            calculateMap(getPointsBlue(), getCurveMapBlue(), this.curveMapBlueChanged);
        } else {
            this.pointsRGB.remove(pointToDelete);
            repaint();
            calculateMap(getPointsRGB(), getCurveMapRGB(), this.curveMapRGBChanged);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawOuterBox(g);

        if (this.mode == Mode.RED) {
            paintEachCurveComponents(g, this.pointsRed, this.curvesRed, this.curveMapRed, this.curveMapRedChanged);
        } else if (this.mode == Mode.GREEN) {
            paintEachCurveComponents(g, this.pointsGreen, this.curvesGreen, this.curveMapGreen, this.curveMapGreenChanged);
        } else if (this.mode == Mode.BLUE) {
            paintEachCurveComponents(g, this.pointsBlue, this.curvesBlue, this.curveMapBlue, this.curveMapBlueChanged);
        } else {
            paintEachCurveComponents(g, this.pointsRGB, this.curvesRGB, this.curveMapRGB, this.curveMapRGBChanged);
        }
    }

    /**
     * draws each of the curves (rgb, red, green, blue)
     *
     * @param g
     * @param points
     * @param curvesRed
     * @param curveMap
     * @param curveMapRedChanged
     */
    private void paintEachCurveComponents (final Graphics g, final LinkedList<Point> points, final LinkedList<Line2D.Double> curvesRed, final HashMap<Integer, Integer> curveMap,
                                           final ValueModel<Boolean> curveMapRedChanged) {
        drawCurve(g, points, curvesRed);
        drawPoints(g, points);
        if (!initialCalc) {
            calculateMap(points, curveMap, curveMapRedChanged);
            initialCalc = true;
        }
    }

    /**
     * Draws the initial outer box of the curve
     *
     * @param g
     */
    private void drawOuterBox (final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // set Background
        g2d.setColor(Colors.gradationBackground);
        g2d.fillRect(0, 0,  Sizes.gradationSize,  Sizes.gradationSize);

        // vertical
        g2d.setColor(Colors.gradationStrokes);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(1, 0, 1,  Sizes.gradationSize);

        // horizontal
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0,  Sizes.gradationSize,  Sizes.gradationSize,  Sizes.gradationSize);
    }

    /**
     * Draws the points in the curve
     *
     * @param g
     */
    private void drawPoints(final Graphics g, final LinkedList<Point> pointList) {
        Graphics2D g2d = (Graphics2D) g;
        for (final Point point : pointList) {
            if (this.pointPipeline.isPresent()) {
                if (point.x == this.pointPipeline.get().x) {
                    g2d.setColor(Colors.gradationActivePoint);
                    g2d.draw(new Ellipse2D.Double(point.x - ((float) Sizes.activePointSize / 2), point.y - ((float) Sizes.activePointSize / 2),
                            Sizes.activePointSize, Sizes.activePointSize));
                    g2d.setColor(Colors.gradationBackground);
                    g2d.fill(new Ellipse2D.Double(point.x - ((float) (Sizes.activePointSize - 3) / 2), point.y - ((float) (Sizes.activePointSize - 3) / 2),
                            Sizes.activePointSize - 3, Sizes.activePointSize - 3));
                } else {
                    g2d.setColor(Colors.gradationInactivePoint);
                    g2d.fill(new Ellipse2D.Double(point.x - ((float) Sizes.inactivePointSize / 2), point.y - ((float) Sizes.inactivePointSize / 2),
                            Sizes.inactivePointSize, Sizes.inactivePointSize));
                }
            } else {
                g2d.setColor(Colors.gradationInactivePoint);
                g2d.fill(new Ellipse2D.Double(point.x - ((float) Sizes.inactivePointSize / 2), point.y - ((float) Sizes.inactivePointSize / 2),
                        Sizes.inactivePointSize, Sizes.inactivePointSize));
            }
        }
    }

    /**
     * draws the curves in the correct color, by traversing the pointList
     *
     * @param g
     */
    private void drawCurve(final Graphics g, final LinkedList<Point> pointList, final LinkedList<Line2D.Double> curveList) {
        Graphics2D g2d = (Graphics2D) g;
        curveList.clear();
        g2d.setStroke(new BasicStroke(2));
        if (this.mode == Mode.RED) {
            g2d.setColor(Colors.gradationCurveRed);
        } else if (this.mode == Mode.GREEN) {
            g2d.setColor(Colors.gradationCurveGreen);
        } else if (this.mode == Mode.BLUE) {
            g2d.setColor(Colors.gradationCurveBlue);
        } else {
            g2d.setColor(Colors.gradationCurveRGB);
        }
        for (int i = 0; i < pointList.size() - 1; i++) {
            Line2D.Double line = new Line2D.Double(pointList.get(i).x, pointList.get(i).y, pointList.get(i+1).x, pointList.get(i+1).y);
            g2d.draw(line);
            curveList.add(line);
        }
    }

    /**
     *
     * calculates all values (0-255) in map, by interpolating the linear curve segments
     *
     * @param pointList
     * @param curveMap
     * @param curveMapChanged
     */
    private void calculateMap(final LinkedList<Point> pointList, final HashMap<Integer, Integer> curveMap, final ValueModel<Boolean> curveMapChanged) {
        PolynomialSplineFunction splineFunction;
        for (int i = 0; i < pointList.size()-1; i++) {
            LinearInterpolator interpolator = new LinearInterpolator();
            splineFunction = interpolator.interpolate(new double[]{pointList.get(i).getX(), pointList.get(i+1).getX()},
                    new double[]{ pointList.get(i).getY(), pointList.get(i+1).getY()});
            for (int j = (int) pointList.get(i).getX() + 1; j < pointList.get(i+1).getX(); j++) {
                curveMap.put(j, (int) splineFunction.value(j));
            }
        }
        curveMapChanged.setValue(!curveMapChanged.getValue());
    }

    /**
     * adds the two starting points to the pointLists and Maps, so that initial curve works
     */
    private void addStartingPoints() {
        pointsRGB.add(new Point(-1 , Sizes.gradationSize));
        pointsRGB.add(new Point( Sizes.gradationSize, -1 ));

        pointsRed.add(new Point(-1, Sizes.gradationSize));
        pointsRed.add(new Point( Sizes.gradationSize, -1));

        pointsGreen.add(new Point(-1, Sizes.gradationSize));
        pointsGreen.add(new Point( Sizes.gradationSize, -1));

        pointsBlue.add(new Point(-1, Sizes.gradationSize));
        pointsBlue.add(new Point( Sizes.gradationSize, -1));

        // fill hashtables
        for (int i = 0; i < 256; i++) {
            this.curveMapRGB.put(i, 255-i);
            this.curveMapRed.put(i, 255-i);
            this.curveMapGreen.put(i, 255-i);
            this.curveMapBlue.put(i, 255-i);
        }
    }

    /**
     * resets all curve data
     */
    private void resetCurveData () {
        this.pointsRGB.clear();
        this.pointsRed.clear();
        this.pointsGreen.clear();
        this.pointsBlue.clear();
        this.curvesRGB.clear();
        this.curvesRed.clear();
        this.curvesGreen.clear();
        this.curvesBlue.clear();
    }

    public void resetCurve() {
        resetCurveData();
        addStartingPoints();
        this.repaint();
    }

    public Mode getMode () {
        return mode;
    }

    public void setMode (Mode mode) {
        this.mode = mode;
        repaint();
    }

    private void setPipelineModelFull() {
        this.isPipelineEmpty.setValue(true);
    }
    private void setPipelineModelEmpty() {
        this.isPipelineEmpty.setValue(false);
    }
    public ValueModel<Boolean> getIsPipelineEmpty () {
        return isPipelineEmpty;
    }
    public ValueModel<Boolean> getCurveMapRGBChanged () {
        return curveMapRGBChanged;
    }
    public HashMap<Integer, Integer> getCurveMapRGB () {
        return curveMapRGB;
    }
    public LinkedList<Point> getPointsRGB () {
        return pointsRGB;
    }
    public LinkedList<Point> getPointsRed () {
        return pointsRed;
    }
    public HashMap<Integer, Integer> getCurveMapRed () {
        return curveMapRed;
    }
    public LinkedList<Point> getPointsGreen () {
        return pointsGreen;
    }
    public HashMap<Integer, Integer> getCurveMapGreen () {
        return curveMapGreen;
    }
    public LinkedList<Point> getPointsBlue () {
        return pointsBlue;
    }
    public HashMap<Integer, Integer> getCurveMapBlue () {
        return curveMapBlue;
    }
    public ValueModel<Boolean> getCurveMapRedChanged () {
        return curveMapRedChanged;
    }
    public ValueModel<Boolean> getCurveMapGreenChanged () {
        return curveMapGreenChanged;
    }
    public ValueModel<Boolean> getCurveMapBlueChanged () {
        return curveMapBlueChanged;
    }
}
