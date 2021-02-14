package Model;

/**
 * stores all the brushsettings
 */
public class BrushSettings {
    private final ValueModel<Integer> brushSize;
    private final ValueModel<Boolean> showMask;
    private final ValueModel<Boolean> brushActive;
    private final ValueModel<Boolean> brushMode;

    public BrushSettings() {
        this.brushSize = new ValueModel<Integer>(20);
        this.showMask = new ValueModel<Boolean>(false);
        this.brushActive = new ValueModel<Boolean>(false);
        this.brushMode = new ValueModel<Boolean>(true);
    }

    public void resetSettings() {
        this.brushSize.setValue(20);
        this.showMask.setValue(false);
        this.brushActive.setValue(false);
        this.brushMode.setValue(true);
    }

    public ValueModel<Integer> getBrushSize () {
        return brushSize;
    }

    public ValueModel<Boolean> getShowMask () {
        return showMask;
    }

    public ValueModel<Boolean> getBrushActive () {
        return brushActive;
    }

    public ValueModel<Boolean> getBrushMode () {
        return brushMode;
    }
}
