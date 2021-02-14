package Model;

import hageldave.imagingkit.core.Img;

import java.util.Stack;

/**
 * stores overwritten image to add the ability to restore changes made to the image
 */
public class RestoreStack {
    private final Stack<Img> oldImages;
    private final ValueModel<Integer> stackListener = new ValueModel<Integer>();
    private ImageModelManager imageModelManager;

    public RestoreStack () {
        this.oldImages = new Stack<>();
        this.stackListener.setValue(0);
    }

    public void setModels (final ImageModelManager imageModelManager) {
        this.imageModelManager = imageModelManager;
    }

    /**
     * adds image to the stack
     *
     * @param image
     */
    public void addToStack (final Img image) {
        if (this.oldImages.size() < 10) {
            this.oldImages.push(image);
            this.stackListener.setValue(getLength());
        } else {
            this.oldImages.remove(9);
            this.oldImages.push(image);
        }
    }

    /**
     * restores latest image
     */
    public void restoreLatestImage () {
        if (!this.oldImages.empty()) {
            if (this.oldImages.size() > 1) {
                this.oldImages.pop();
            }
            Img restoredImg = this.oldImages.peek();
            this.imageModelManager.getModel().setCurrentImg(restoredImg);
            this.imageModelManager.getTempModel().setCurrentImg(restoredImg);
            this.imageModelManager.getBrushEditedImage().setCurrentImg(restoredImg);
            this.imageModelManager.getBrushEditedTempImage().setCurrentImg(restoredImg);
            this.imageModelManager.resetMasks();
            this.stackListener.setValue(getLength());
        }
    }

    public void clear () {
        this.oldImages.clear();
    }

    public Integer getLength() {
        return this.oldImages.size();
    }

    public ValueModel<Integer> getStackListener () {
        return stackListener;
    }
}
