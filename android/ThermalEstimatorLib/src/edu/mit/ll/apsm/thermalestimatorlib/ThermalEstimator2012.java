package edu.mit.ll.apsm.thermalestimatorlib;

import java.util.ArrayList;
import java.util.List;


// import org.apache.log4j.Logger;

// import edu.mit.ll.apsm.activities.APSMActivity;

/**
 * Estimate the core body temperature from minute distributed heart rate
 * measurements
 * 
 * @author Jeff Simpson <jeffs@ll.mit.edu>
 * 
 *         Adapted from ThermalEstimator
 * 
 */
public class ThermalEstimator2012 implements ThermalEstimator {

    private static final int PREDICTION_SAMPLES = 15;

    // Constants
    private static final Double m_A = 1.0;
    private static final Double m_Gamma = Math.pow(0.022, 2);
    private static final Double m_B[] = { -7887.1, 384.4286, -4.5714 };
    private static final Double m_Sigma = Math.pow(18.88, 2);

    // Class instance variables
    private List<Double> m_Readings = new ArrayList<Double>();
    private Double m_TCoreStart = 37.1;
    private Double m_TCoreStartConfidence = 0.0;

    // Cache to store computed values
    private List<Double> m_Cache = null;


    public ThermalEstimator2012() {}

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

    public void setCoreTemp(Double coreTemp)
    {
        // Got a real core temp reading

        // Set the initial core temp to be the current core temp
        m_TCoreStart = coreTemp;

        // grab most recent heart rate, if there is one
        if (m_Readings.size() > 0)
        {
            Double currentHR = m_Readings.get(m_Readings.size() - 1); // most
                                                                      // recent
                                                                      // measurement
            // remove old readings
            clearReadings();

            // re-insert most recent HR
            addReading(currentHR);
        }


    }


    public ThermalEstimator2012(Double tcorestart, Double confidence) {
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

            Double x, v, x_pred, v_pred, z, k, c_vc;
            Integer segment;

            x = m_TCoreStart;
            v = m_TCoreStartConfidence; // zero

            // +15 for 15 minute prediction
            for (int i = 0; i < m_Readings.size() + PREDICTION_SAMPLES; ++i) {

                if (m_Readings.size() == 0)
                    break;

                // Equation 3
                x_pred = m_A * x;

                // Equation 4
                v_pred = (Math.pow(m_A, 2)) * v + m_Gamma;

                // Equation 5

                // Equation A2
                v_pred = v + m_Gamma;

                // Get reading at this timestep (or in the case of future,
                // repeat the final HR value)
                if (i < m_Readings.size())
                    z = m_Readings.get(i);
                else
                    z = m_Readings.get(m_Readings.size() - 1); // most recent
                                                               // measurement

                // Equation 5
                c_vc = 2 * m_B[2] * x_pred + m_B[1];

                // Equation 6
                k = (v_pred * c_vc) / (Math.pow(c_vc, 2) * v_pred + m_Sigma);

                // Equation 7
                x = x_pred +
                        k *
                        (z - (m_B[2] * Math.pow(x_pred, 2) + m_B[1] * x_pred + m_B[0]));

                // Equation 8
                v = (1 - k * c_vc) * v_pred;

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

}
