package edu.mit.ll.apsm.thermalestimatorlib;

import java.util.ArrayList;
import java.util.List;


// import org.apache.log4j.Logger;

// import edu.mit.ll.apsm.activities.APSMActivity;

/**
 * Estimate the core body temperature from minute distributed heart rate
 * measurements;
 * 
 * @author Micah Lee<micah.lee@ll.mit.edu>
 * 
 */
public class ThermalEstimatorOriginal implements ThermalEstimator {

    private static final int PREDICTION_SAMPLES = 15;
    // Constants
    private static final Double m_Intercepts[] = { -1459.09, -3215.10,
            -1839.46, -376.60 };
    private static final Double m_Coefficients[] = { 41.51, 87.93, 51.83, 13.91 };
    private static final Double m_Variances[] = { Math.pow(18, 2),
            Math.pow(37, 2), Math.pow(9, 2), Math.pow(5, 2) };
    private static final Double m_Mapping = 1.0;
    private static final Double m_Gamma = Math.pow(0.024, 2);

    // Class instance variables
    private List<Double> m_Readings = new ArrayList<Double>();
    private Double m_TCoreStart = 37.1;
    private Double m_TCoreStartConfidence = 0.0;

    // Cache to store computed values
    private List<Double> m_Cache = null;


    public ThermalEstimatorOriginal() {

    }


    public ThermalEstimatorOriginal(Double tcorestart, Double confidence) {
        m_TCoreStart = tcorestart;
        m_TCoreStartConfidence = confidence;
    }


    public int getPredictionSamples()
    {
        return PREDICTION_SAMPLES;
    }


    // Added for debugging how the thermal estimator works
    public int getSize() {
        return m_Readings.size();
    }


    public void addReading(Double reading) {
        m_Readings.add(reading);
        m_Cache = null;
    }


    public void clearReadings() {
        m_Readings.clear();
        m_Cache = null;
    }


    public Double getCoreTempEstimate() {
        // Return the most recent reading
        List<Double> history = getTCoreHistory();
        // return history.get(history.size()-1);
        if (history.size() == 1)
            return history.get(0);
        return history.get(history.size() - 1 - PREDICTION_SAMPLES); // account
                                                                     // for
                                                                     // history
    }


    public List<Double> getCoreTempPredictions() {
        List<Double> fullHistory = getTCoreHistory();
        List<Double> predictions = new ArrayList<Double>();
        if (fullHistory.size() < 15)
            return predictions;
        // for(int i=fullHistory.size()-1;
        // i>fullHistory.size()-1-PREDICTION_SAMPLES;i--)
        for (int i = fullHistory.size() - 1 - PREDICTION_SAMPLES; i < fullHistory
                .size(); i++)
        {
            predictions.add(fullHistory.get(i));
        }
        return predictions;
    }


    public List<Double> getTCoreHistory() {
        // If the cached value is invalid, compute the values
        if (m_Cache == null) {
            List<Double> tcoreEst = new ArrayList<Double>();
            tcoreEst.add(m_TCoreStart);

            Double x, v, x_pred, v_pred, reading, k;
            Integer segment;

            x = m_TCoreStart;
            v = m_TCoreStartConfidence;

            // +15 for 15 minute prediction
            for (int i = 0; i < m_Readings.size() + PREDICTION_SAMPLES; ++i) {
                if (m_Readings.size() == 0)
                    break;
                // Equation A1
                x_pred = m_Mapping * x;

                // Equation A2
                v_pred = v + m_Gamma;

                // Get reading at this timestep (or in the case of future,
                // repeat the final HR value)
                if (i < m_Readings.size())
                    reading = m_Readings.get(i);
                else
                    reading = m_Readings.get(m_Readings.size() - 1); // most
                                                                     // recent
                                                                     // measurement

                if (x_pred >= 37.83 && x_pred < 38.10) {
                    segment = 1;
                } else if (x_pred >= 38.10 && x_pred < 38.58) {
                    segment = 2;
                } else if (x_pred >= 38.58) {
                    segment = 3;
                } else {
                    segment = 0;
                }

                // Equation A3
                k = (v_pred * m_Coefficients[segment]) /
                        (Math.pow(m_Coefficients[segment], 2) * v_pred + m_Variances[segment]);

                // Equation A4
                x = x_pred +
                        k *
                        (reading - (m_Coefficients[segment] * x_pred + m_Intercepts[segment]));

                // Equation A5: Update variance
                v = (1 - k * m_Coefficients[segment]) * v_pred;

                // Debug prints
                /* System.out.println(String.format("Executing Step %d", i));
                 * System.out.println(String.format("\tx_pred: %f", x_pred));
                 * System.out.println(String.format("\tv_pred: %f", v_pred));
                 * System.out.println(String.format("\treading: %f", reading));
                 * System.out.println(String.format("\tk: %f", k));
                 * System.out.println(String.format("\tx: %f", x));
                 * System.out.println(String.format("\tv: %f", v)); */

                tcoreEst.add(x);
            }

            m_Cache = tcoreEst;
        }

        return m_Cache;
    }


    @Override
    public void setCoreTemp(Double coreTemp) {
        // TODO Auto-generated method stub

    }
    
    /**
     * Only sets the core temperature unlike setCoreTemp method which also modifies the core temperature readings
     * 
     * @param Double coreTemp [degC]
     *            Set starting core temperature to coreTemp
     * 
     * GAC Oct 10, 2013
     */
    public void setM_TCoreStart(Double coreTemp){
        m_TCoreStart = coreTemp;
    }

}
