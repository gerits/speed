package be.rubengerits.speed.speedservices;

import android.location.LocationProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import be.rubengerits.speed.location.LocationEvent;

public class GpsStatusService implements SpeedService {

    public static final String ID = "GpsStatus";

    private int status = LocationProvider.OUT_OF_SERVICE;

    private List<GpsStatusValueChangeListener> listeners = new ArrayList<>();

    @Override
    public String getId() {
        return ID;
    }

    public GpsStatusService() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(LocationEvent event) {
        setStatus(event.getStatus());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        notifyValueChanged(status);
    }

    public void addValueChangeListener(GpsStatusValueChangeListener listener) {
        listeners.add(listener);
    }

    public void removeValueChangeListener(GpsStatusValueChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyValueChanged(int value) {
        for (GpsStatusValueChangeListener listener : listeners) {
            listener.onGpsStatusChanged(value);
        }
    }

    public interface GpsStatusValueChangeListener {
        void onGpsStatusChanged(int status);
    }
}
