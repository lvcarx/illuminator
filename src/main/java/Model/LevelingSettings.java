package Model;

/**
 * stores current leveling settings
 */
public class LevelingSettings {
    private final ValueModel<Boolean> straightenActive;
    private final ValueModel<Boolean> previewActive;

    public LevelingSettings () {
        this.straightenActive = new ValueModel<Boolean>(false);
        this.previewActive = new ValueModel<Boolean>(false);
    }

    public void resetSettings() {
        this.straightenActive.setValue(false);
        this.previewActive.setValue(false);
    }

    public ValueModel<Boolean> getStraightenActive () {
        return straightenActive;
    }

    public ValueModel<Boolean> getPreviewActive () {
        return previewActive;
    }
}
