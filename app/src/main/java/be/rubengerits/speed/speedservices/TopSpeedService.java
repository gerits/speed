package be.rubengerits.speed.speedservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;

public class TopSpeedService implements SpeedService {

    public static final String ID = "TopSpeed";
    private final Context context;
    private List<TopSpeedValueChangeListener> listeners = new ArrayList<>();

    @Override
    public String getId() {
        return ID;
    }

    public TopSpeedService(Context context) {
        this.context = context;

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(LocationEvent event) {
        if (event.getLocation() != null && event.getLocation().hasSpeed()) {
            float speed = event.getLocation().getSpeed();
            Float maxSpeed = getValue();
            if (speed > maxSpeed) {
                setValue(speed);
            }
        }
    }

    public float getValue() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getFloat(context.getString(R.string.max_speed_preference), 0f);
    }

    private void setValue(Float value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(context.getString(R.string.max_speed_preference), value);
        editor.apply();

        notifyValueChanged(value);
    }

    public void reset() {
        setValue(0f);
    }

    public void addValueChangeListener(TopSpeedValueChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyValueChanged(Float value) {
        for (TopSpeedValueChangeListener listener : listeners) {
            listener.onValueChanged(value);
        }
    }

    public interface TopSpeedValueChangeListener {
        void onValueChanged(Float value);
    }

}
