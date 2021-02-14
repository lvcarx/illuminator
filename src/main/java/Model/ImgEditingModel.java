package Model;

import hageldave.imagingkit.core.Img;

import java.util.Stack;

/**
 * The image editing model holds two images, the current state image,
 * and a preview image of the next state (e.g. previewing a manipulation).
 * When setting the current image, the preview will be set to a copy of
 * it automatically.
 */
public class ImgEditingModel {

    private static class ImgModel extends ValueModel<Img> {
        @Override
        public void setValue(Img val) {
            if(!imgEquals(val, value)) {
                Img old = value;
                value = val;
                notifyListeners(old);
            }
        }

        private boolean imgEquals(Img img1, Img img2) {
            return false;
        }
    }

    private ImgModel current = new ImgModel();
    private ImgModel preview = new ImgModel();

    /**
     * Sets the current image. This will also change the preview image.
     * @param img new image
     */
    public void setCurrentImg(Img img) {
        setPreviewImg(img==null?null:img.copy());
        current.setValue(img);
    }

    /**
     * Sets the preview image (e.g. an image of a manipulation of current)
     * @param img new image
     */
    public void setPreviewImg(Img img) {
        preview.setValue(img);
    }

    /**
     * Adds a value listener for listening to current image updates
     * @param l the listener
     */
    public void addCurrentImgListener(ValueListener<Img> l) {
        current.addValueListener(l);
    }

    /**
     * Adds a value listener for listening to preview image updates
     * @param l the listener
     */
    public void addPreviewImgListener(ValueListener<Img> l) {
        preview.addValueListener(l);
    }

    /**
     * @return the current state image
     */
    public Img getCurrentImg() {
        return current.getValue();
    }

    /**
     * @return the preview image
     */
    public Img getPreviewImg() {
        return preview.getValue();
    }



}
