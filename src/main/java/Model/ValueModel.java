package Model;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple model class for storing a single value
 * and notification mechanism for value changes.
 *
 * @author haegeldd
 * @param <T> type of the value held by this model
 */
public class ValueModel<T> {

    protected T value;
    protected List<ValueListener<T>> listeners = new LinkedList<>();

    public ValueModel() {

    }
    public ValueModel(final T value) {
        this.value = value;
    }

    /**
     * adds a new value listener to this model.
     * @param l listener to add
     */
    public void addValueListener(ValueListener<T> l) {listeners.add(l);}

    public void addValueListener(OldNewValueListener<T> l) {listeners.add(l);}

    /**
     * notifies all listeners that value has changed
     */
    public void notifyListeners(T old) {
        listeners.forEach(l->{
            if(l instanceof OldNewValueListener) {
                ((OldNewValueListener<T>)l).valueChanged(old, value);
            } else {
                l.valueChanged(value);
            }
        });
    }

    /**
     * @return current value
     */
    public T getValue() {return value;}

    /**
     * Sets new value. This will notify all listeners.
     * @param val new value
     */
    public void setValue(T val) {
        if(!val.equals(value)) {
            T old = value;
            value = val;
            notifyListeners(old);
        }
    }

    public T setAndGet(T val) {
        if(!val.equals(value)) {
            T old = value;
            value = val;
            notifyListeners(old);
        }
        return val;
    }
}
