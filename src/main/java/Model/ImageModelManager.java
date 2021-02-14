package Model;

import hageldave.imagingkit.core.Img;

import java.awt.*;

/**
 * stores all the needed imaging models
 */
public class ImageModelManager {
    private final ImgEditingModel model;
    private final ImgEditingModel tempModel;
    private final ImgEditingModel brushMask;
    private final ImgEditingModel tempBrushMask;
    private final ImgEditingModel brushEditedImage;
    private final ImgEditingModel brushEditedTempImage;
    private final RestoreStack restoreStack;

    public ImageModelManager(final RestoreStack restoreStack) {
        this.model = new ImgEditingModel();
        this.tempModel = new ImgEditingModel();
        this.brushMask = new ImgEditingModel();
        this.tempBrushMask = new ImgEditingModel();
        this.brushEditedImage = new ImgEditingModel();
        this.brushEditedTempImage = new ImgEditingModel();
        this.restoreStack = restoreStack;
    }

    /**
     * updates the current temporary model, that holds the original image, where all the manipulations are added to
     * - this happens, when brush button is clicked, image is saved or a transition from brush panel to "normal" side panel happens
     *
     */
    public void updateTempModel() {
        this.tempModel.setCurrentImg(this.model.getCurrentImg());
        this.brushEditedImage.setCurrentImg(this.model.getCurrentImg());
        this.brushEditedTempImage.setCurrentImg(this.model.getCurrentImg());
        this.restoreStack.addToStack(this.model.getCurrentImg());
    }

    /**
     * all models are updated - used when new image is loaded
     *
     * @param newImg
     */
    public void updateAllModels(final Img newImg) {
        model.setCurrentImg(newImg);
        tempModel.setCurrentImg(newImg);
        brushEditedImage.setCurrentImg(newImg);
        brushEditedTempImage.setCurrentImg(newImg);
        resetMasks();
        restoreStack.clear();
        restoreStack.addToStack(newImg);
    }

    /**
     * resets the masks (when image is rotated or new image is loaded)
     */
    public void resetMasks () {
        Img maskImage = new Img(model.getCurrentImg().getWidth(), model.getCurrentImg().getHeight());
        maskImage.paint(t -> t.fillRect(0, 0, maskImage.getWidth(), maskImage.getHeight()));
        brushMask.setCurrentImg(maskImage);
        tempBrushMask.setCurrentImg(maskImage);
    }

    /**
     * inverts the mask color (from white to black)
     */
    public void invertMasks() {
        Img maskImage = new Img(model.getCurrentImg().getWidth(), model.getCurrentImg().getHeight());
        maskImage.paint(t -> {
            t.setColor(Color.BLACK);
            t.fillRect(0, 0, maskImage.getWidth(), maskImage.getHeight());
        });
        brushMask.setCurrentImg(maskImage);
        tempBrushMask.setCurrentImg(maskImage);
    }

    /* GETTERS */

    public ImgEditingModel getModel () {
        return model;
    }

    public ImgEditingModel getTempModel () {
        return tempModel;
    }

    public ImgEditingModel getBrushMask () {
        return brushMask;
    }

    public ImgEditingModel getTempBrushMask () {
        return tempBrushMask;
    }

    public ImgEditingModel getBrushEditedImage () {
        return brushEditedImage;
    }

    public ImgEditingModel getBrushEditedTempImage () {
        return brushEditedTempImage;
    }

    public RestoreStack getRestoreStack () {
        return restoreStack;
    }
}
