package Model;

public interface OldNewValueListener<T> extends ValueListener<T> {

    @Override
    default void valueChanged(T valNew) {
        valueChanged(valNew, valNew);
    }

    /**
     * is called when value changed
     * @param valOld the previous value
     * @param valNew the current value that was just set
     */
    public void valueChanged(T valOld, T valNew);
}
