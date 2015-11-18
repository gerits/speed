package be.rubengerits.speed.speedservices;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class StopWatchSpeedService implements SpeedService {

    public static final String ID = "StopWatch";

    private static final int REFRESH_RATE = 100;
    private static final String DEFAULT_TIME = "00:00:00:0";

    private String value = DEFAULT_TIME;
    private Long startTime;
    private Long endTime;
    private Status status = Status.RESET;

    private Handler mHandler = new Handler();
    private Runnable timer;

    private List<StopWatchValueChangeListener> listeners = new ArrayList<>();

    @Override
    public String getId() {
        return ID;
    }

    public StopWatchSpeedService() {
        timer = new Runnable() {
            public void run() {
                if (startTime != null && endTime == null) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    updateTimer(elapsedTime);
                    mHandler.postDelayed(this, REFRESH_RATE);
                }
            }
        };
    }

    private void updateTimer(Long elapsedTime) {
        long millisecond = (elapsedTime / 100) % 10;
        long second = (elapsedTime / 1000) % 60;
        long minute = (elapsedTime / (1000 * 60)) % 60;
        long hour = (elapsedTime / (1000 * 60 * 60)) % 24;

        value = String.format("%02d:%02d:%02d:%01d", hour, minute, second, millisecond);

        notifyValueChanged(value);
    }

    public String getValue() {
        return value;
    }

    public void start() {
        startTime = System.currentTimeMillis();

        mHandler.removeCallbacks(timer);
        mHandler.postDelayed(timer, 0);

        status = Status.STARTED;
    }

    public void stop() {
        endTime = System.currentTimeMillis();

        mHandler.removeCallbacks(timer);

        status = Status.STOPPED;
    }

    public void reset() {
        startTime = null;
        endTime = null;

        value = DEFAULT_TIME;

        status = Status.RESET;
    }

    public Status getStatus() {
        return status;
    }

    public void addValueChangeListener(StopWatchValueChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyValueChanged(String value) {
        for (StopWatchValueChangeListener listener : listeners) {
            listener.onValueChanged(value);
        }
    }

    public interface StopWatchValueChangeListener {
        void onValueChanged(String value);
    }

    public enum Status {
        STOPPED,
        STARTED,
        RESET
    }
}
