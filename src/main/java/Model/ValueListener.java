package Model;

public interface ValueListener<T> {
    /**
     * is called when a value changed
     * @param valNew the new value
     */
    void valueChanged(T valNew);
}
