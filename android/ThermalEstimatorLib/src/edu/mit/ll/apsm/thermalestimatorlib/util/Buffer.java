package edu.mit.ll.apsm.thermalestimatorlib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Buffer<T> implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4362505599765698268L;

    private List<T> Items = new ArrayList<T>();
    private Integer Capacity;


    public Buffer(Integer capacity) {
        setCapacity(capacity);
    }


    public Integer getCapacity() {
        return Capacity;
    }


    public void setCapacity(Integer capacity) {
        Capacity = capacity;
    }


    @SuppressWarnings("unchecked")
    public List<T> getItems() {
        return (List<T>) ((ArrayList<T>) Items).clone();
    }


    public void clear() {
        Items.clear();
    }


    private void removeItemsIfCapacityExceeded() {
        if (Items.size() > Capacity) {
            Items.remove(0);
        }
    }


    public boolean push(T object) {
        if (!Items.add(object)) {
            return false;
        }

        removeItemsIfCapacityExceeded();

        return true;
    }

}
