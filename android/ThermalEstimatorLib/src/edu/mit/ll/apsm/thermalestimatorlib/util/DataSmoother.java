package edu.mit.ll.apsm.thermalestimatorlib.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DataSmoother {

    Buffer<Double> measurements;

    Integer minimumValuesToAverage;


    public DataSmoother(Integer maxValuesToKeep, Integer minimumValuesToAverage) {
        measurements = new Buffer<Double>(maxValuesToKeep);
        this.minimumValuesToAverage = Math.max(3, minimumValuesToAverage);
    }


    public void clear() {
        measurements.clear();
    }


    public void addMeasurement(Double measurement) {
        measurements.push(measurement);
    }


    private void removeNullValues(List<Double> values) {
        Iterator<Double> itr = values.iterator();
        while (itr.hasNext()) {
            if (itr.next() == null) {
                itr.remove();
            }
        }
    }


    private void removeMinAndMax(List<Double> values) {
        values.remove(0);
        values.remove(values.size() - 1);
    }


    private Double getAverageValue(List<Double> values) {
        if (values.size() > 0) {
            // Average the remaining values
            Double averageHr = 0.0;
            for (Double value : values) {
                averageHr += value;
            }

            return averageHr / values.size();
        } else {
            return null;
        }
    }


    public Double getSmoothedValue() {
        // Create a copy of the list to manipulate
        List<Double> values = measurements.getItems();

        removeNullValues(values);
        Collections.sort(values);

        if (values.size() >= minimumValuesToAverage + 2) {
            removeMinAndMax(values);
        }

        return getAverageValue(values);
    }
}
