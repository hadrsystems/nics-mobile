/* The following code for detecting shakes was written by Matthew Wiggins and is
 * released under the APACHE 2.0 license
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 */
package edu.mit.ll.utility.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;


public class WakeService implements SensorEventListener
{
    private static final int FORCE_THRESHOLD = 10000;

    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private static final int LIGHT_DELTA_THRESHOLD = 1000;
    // private static final int LIGHT_ABS_THRESHOLD = 2000;

    private SensorManager mSensorMgr;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private OnWakeListener mShakeListener;
    private Context mContext;
    private Sensor mAccelerometer;
    private Sensor mProximity;
    private Sensor mLight;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    private float lastLight = 0.0f;

    private boolean m_ShakeEnabled;
    private double m_ShakeSensitivity;
    private boolean m_LightEnabled;
    private double m_LightSensitivity;

    public interface OnWakeListener{
        public void onWake();
    }


    public WakeService(Context context) {
        this(context, true, 50, true, 50);
    }


    public WakeService(Context context, boolean enableShake, double shakeSensitivity, boolean enableLight, double lightSensitivity) {
        mContext = context;


        setShakeEnabled(enableShake);
        setShakeSensitivity(shakeSensitivity);

        setLightEnabled(enableLight);
        setLightSensitivity(lightSensitivity);

        resume();
    }

    public static void wakeDevice(PowerManager pwrmgr) {

        PowerManager.WakeLock screenOn = pwrmgr.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "ActivateDisplay");
        try {
            screenOn.acquire();
        } finally {
            screenOn.release();
        }
    }

    public void setShakeEnabled(boolean enabled) {
        m_ShakeEnabled = enabled;
    }


    public void setLightEnabled(boolean enabled) {
        m_LightEnabled = enabled;
    }


    public void setShakeSensitivity(double sensitivity) {
        if (sensitivity > 1.0) {
            sensitivity = 1.0;
        }
        else if (sensitivity < 0.0) {
            sensitivity = 0.0;
        }

        m_ShakeSensitivity = sensitivity;
    }


    public void setLightSensitivity(double sensitivity) {
        if (sensitivity > 1.0) {
            sensitivity = 1.0;
        }
        else if (sensitivity < 0.0) {
            sensitivity = 0.0;
        }

        m_LightSensitivity = sensitivity;
    }


    public void setOnWakeListener(OnWakeListener listener)
    {
        mShakeListener = listener;
    }


    public void resume() {
        mSensorMgr = (SensorManager) mContext
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorMgr == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLight = mSensorMgr.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Check for accelerometer support
        boolean supported = mSensorMgr.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);
        if (!supported) {
            Log.d("WakeService", "Accelerometer not supported.");
            mSensorMgr.unregisterListener(this, mAccelerometer);
        }

        // Check for proximity sensor support
        supported = mSensorMgr.registerListener(this, mProximity,
                SensorManager.SENSOR_DELAY_GAME);
        if (!supported) {
            Log.d("WakeService", "Proximity sensor not supported.");
            mSensorMgr.unregisterListener(this, mProximity);
        }

        // Check for ambient light sensor support
        supported = mSensorMgr.registerListener(this, mLight,
                SensorManager.SENSOR_DELAY_FASTEST);
        if (!supported) {
            Log.d("WakeService", "Ambient light sensor not supported.");
            mSensorMgr.unregisterListener(this, mLight);
        }

    }


    public void pause() {
        if (mSensorMgr != null) {
            mSensorMgr.unregisterListener(this, mAccelerometer);
            mSensorMgr.unregisterListener(this, mProximity);
            mSensorMgr.unregisterListener(this, mLight);
            mSensorMgr = null;
        }
    }


    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    public void onSensorChanged(SensorEvent event) {

        if (m_ShakeEnabled && event.sensor == mAccelerometer) {
            long now = System.currentTimeMillis();

            if ((now - mLastForce) > SHAKE_TIMEOUT) {
                mShakeCount = 0;
            }

            if ((now - mLastTime) > TIME_THRESHOLD) {
                long diff = now - mLastTime;
                float speed = Math.abs(event.values[SensorManager.DATA_X] +
                        event.values[SensorManager.DATA_Y] +
                        event.values[SensorManager.DATA_Z] - mLastX - mLastY -
                        mLastZ) /
                        diff * 10000;
                if (speed > (FORCE_THRESHOLD * (1 - m_ShakeSensitivity))) {
                    if ((++mShakeCount >= SHAKE_COUNT) &&
                            (now - mLastShake > SHAKE_DURATION)) {
                        mLastShake = now;
                        mShakeCount = 0;
                        if (mShakeListener != null) {
                            mShakeListener.onWake();
                        }
                    }
                    mLastForce = now;
                }
                mLastTime = now;
                mLastX = event.values[SensorManager.DATA_X];
                mLastY = event.values[SensorManager.DATA_Y];
                mLastZ = event.values[SensorManager.DATA_Z];
            }
        } else if (event.sensor == mProximity) {
        } else if (m_LightEnabled && event.sensor == mLight) {
            // Log.d("Light Delta", String.format("%f > %f", (lastLight -
            // event.values[0]), (LIGHT_DELTA_THRESHOLD * (1-m_LightSensitivity)
            // )));
            // Log.d("Light ABS", String.format("%f <= %f",event.values[0], (10
            // + (LIGHT_ABS_THRESHOLD * m_LightSensitivity))));
            if ((lastLight - event.values[0]) > (LIGHT_DELTA_THRESHOLD * (1 - m_LightSensitivity)) /* &&
                                                                                                    * (
                                                                                                    * event
                                                                                                    * .
                                                                                                    * values
                                                                                                    * [
                                                                                                    * 0
                                                                                                    * ]
                                                                                                    * <=
                                                                                                    * (
                                                                                                    * 100
                                                                                                    * +
                                                                                                    * (
                                                                                                    * LIGHT_ABS_THRESHOLD
                                                                                                    * *
                                                                                                    * m_LightSensitivity
                                                                                                    * )
                                                                                                    * )
                                                                                                    * ) */) {
                if (mShakeListener != null) {
                    mShakeListener.onWake();
                }
            }
            lastLight = event.values[0];
        }

    }

}
