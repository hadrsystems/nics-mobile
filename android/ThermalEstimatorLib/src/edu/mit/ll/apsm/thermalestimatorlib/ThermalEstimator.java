package edu.mit.ll.apsm.thermalestimatorlib;

import java.util.List;


public interface ThermalEstimator {
    public void setCoreTemp(Double coreTemp);


    public int getPredictionSamples();


    public void addReading(Double reading);


    public void clearReadings();


    public Double getCoreTempEstimate();


    public List<Double> getCoreTempPredictions();


    public List<Double> getTCoreHistory();


    public int getSize();
    
    //GAC Oct 10, 2013
    public void setM_TCoreStart(Double TCoreStart);
}
