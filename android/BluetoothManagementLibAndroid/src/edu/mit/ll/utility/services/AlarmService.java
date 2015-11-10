package edu.mit.ll.utility.services;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import edu.mit.ll.bluetoothmanagement.R;


public class AlarmService {
    private Context mContext;
    private boolean m_Alarming = false;
    private boolean m_AudibleEnabled;
    private boolean m_VibrateEnabled;

    private Vibrator vib;
    private int m_AlarmStreamId = 0;
    private int m_AlarmSoundId = 0;
    private SoundPool m_SoundPool;


    public AlarmService(Context context) {
        this(context, true, true);
    }


    public AlarmService(Context context, boolean enableVibrate, boolean enableAudible) {
        mContext = context;

        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        m_SoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        m_AlarmSoundId = m_SoundPool.load(context, R.raw.beep, 1);

        setVibrateEnabled(enableVibrate);
        setAudibleEnabled(enableAudible);
    }


    public void setAudibleEnabled(boolean enable) {
        m_AudibleEnabled = enable;
        updateAlarms();
    }


    public void setVibrateEnabled(boolean enable) {
        m_VibrateEnabled = enable;
        updateAlarms();
    }


    public void setAlarming(boolean alarming) {
        m_Alarming = alarming;
        updateAlarms();
    }


    protected void updateAlarms() {

        if (!m_VibrateEnabled) {
            endVibrateAlarm();
        }

        if (!m_AudibleEnabled) {
            endAudibleAlarm();
        }

        if (m_Alarming) {
            if (m_VibrateEnabled) {
                beginVibrateAlarm();
            }

            if (m_AudibleEnabled) {
                beginAudibleAlarm();
            }
        } else {
            endVibrateAlarm();
            endAudibleAlarm();
        }

    }


    protected void beginVibrateAlarm() {
        long[] pattern = { 0, 200, 500, 900, 600 };
        vib.vibrate(pattern, 0);
    }


    protected void endVibrateAlarm() {
        vib.cancel();
    }


    protected void beginAudibleAlarm() {
        m_AlarmStreamId = m_SoundPool.play(m_AlarmSoundId, 1.0f, 1.0f, 1, -1,
                1.0f);
    }


    protected void endAudibleAlarm() {
        m_SoundPool.stop(m_AlarmStreamId);
    }

}
