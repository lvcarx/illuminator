package Leveling;

import hageldave.imagingkit.core.Img;
import hageldave.imagingkit.core.Pixel;

import java.awt.*;

/**
 * responsible for rotating and cutting the image
 */
public class Leveling {
    /**
     * @param startingPoint
     * @param endPoint
     * @return
     */
    public static Integer calcAngle (final Point startingPoint, final Point endPoint) {
        return (int) Math.toDegrees(Math.atan2(endPoint.y - startingPoint.y, endPoint.x - startingPoint.x));
    }

    public static Img turnImage (final Integer turnAngle, final Img currentImg) {
        int centerX = currentImg.getWidth() / 2;
        int centerY = currentImg.getHeight() / 2;

        int y1;
        int y2;
        int x1;
        int x2;
        int newTurnAngle;
        int width;
        int height;
        int offsetX;
        int offsetY;
        Img newImage;

        newTurnAngle = turnAngle;
        if (( turnAngle > 1 && turnAngle < 90 ) || ( turnAngle < -90 && turnAngle > -179 )) {
            if (turnAngle < 0) {
                newTurnAngle = 180 + turnAngle;
            }
            // calculate inner rectangle coordinates if turned to the left
            y1 = (int) Math.abs(( -Math.sin(Math.toRadians(newTurnAngle)) * ( -centerX ) + Math.cos(Math.toRadians(newTurnAngle)) * ( -centerY ) ) + centerY);
            y2 = (int) Math.abs(( -Math.sin(Math.toRadians(newTurnAngle)) * ( currentImg.getWidth() - centerX ) + Math.cos(Math.toRadians(newTurnAngle)) * ( currentImg.getHeight() - centerY ) ) + centerY);
            x2 = (int) ( Math.cos(Math.toRadians(newTurnAngle)) * ( currentImg.getWidth() - centerX ) + Math.sin(Math.toRadians(newTurnAngle)) * ( -centerY ) + centerX );
            x1 = (int) ( Math.cos(Math.toRadians(newTurnAngle)) * ( -centerX ) + Math.sin(Math.toRadians(newTurnAngle)) * ( currentImg.getHeight() - centerY ) + centerX );

        } else {
            if (turnAngle > 0) {
                newTurnAngle = -180 + turnAngle;
            }
            // calculate inner rectangle coordinates if turned to the right
            y1 = (int) Math.abs(( -Math.sin(Math.toRadians(newTurnAngle)) * ( currentImg.getWidth() - centerX ) + Math.cos(Math.toRadians(newTurnAngle)) * ( -centerY ) ) + centerY);
            y2 = (int) Math.abs(( -Math.sin(Math.toRadians(newTurnAngle)) * ( -centerX ) + Math.cos(Math.toRadians(newTurnAngle)) * ( currentImg.getHeight() - centerY ) ) + centerY);
            x2 = (int) ( Math.cos(Math.toRadians(newTurnAngle)) * ( currentImg.getWidth() - centerX ) + Math.sin(Math.toRadians(newTurnAngle)) * ( currentImg.getHeight() - centerY ) + centerX );
            x1 = (int) ( Math.cos(Math.toRadians(newTurnAngle)) * ( -centerX ) + Math.sin(Math.toRadians(newTurnAngle)) * ( -centerY ) + centerX );
        }

        // calculate the width and height of the rotated image
        if (x2 > x1) {
            width = x2 - x1;
        } else {
            width = x1 - x2;
        }
        if (y2 > y1) {
            height = y2 - y1;
        } else {
            height = y1 - y2;
        }

        offsetX = currentImg.getWidth() - Math.abs(width);
        offsetY = currentImg.getHeight() - Math.abs(height);
        newImage = new Img(width, height);

        // turn image black before to see errors
        for (Pixel px : newImage) {
            px.setRGB(0, 0, 0);
        }

        Img copiedImg = new Img(currentImg.getWidth(), currentImg.getHeight());

        // get pixel values of non rotated image and set it in rotated image
        for (int i = 0; i < copiedImg.getHeight(); i++) {
            for (int j = 0; j < copiedImg.getWidth(); j++) {
                int newX = (int) ( ( Math.cos(Math.toRadians(-newTurnAngle)) * ( j - centerX ) + Math.sin(Math.toRadians(-newTurnAngle)) * ( i - centerY ) ) + centerX );
                int newY = (int) ( ( -Math.sin(Math.toRadians(-newTurnAngle)) * ( j - centerX ) + Math.cos(Math.toRadians(-newTurnAngle)) * ( i - centerY ) ) + centerY );

                if (newX > 0 && newX < currentImg.getWidth() && newY > 0 && newY < currentImg.getHeight()) {
                    Pixel currentPix = currentImg.getPixel(newX, newY);
                    copiedImg.setValue(j, i, currentPix.getValue());
                }
            }
        }

        // cut image
         for (int i = 0; i < copiedImg.getHeight(); i++) {
            for (int j = 0; j < copiedImg.getWidth(); j++) {
                Pixel currentPix = copiedImg.getPixel(j, i);
                if (j > ( offsetX / 2 ) && j < width + ( offsetX / 2 ) && i > ( offsetY / 2 ) && i < height + ( offsetY / 2 )) {
                    newImage.setValue(j- ( offsetX / 2 ), i - ( offsetY / 2 ), currentPix.getValue());
                }
            }
        }
        return newImage;
    }
}
