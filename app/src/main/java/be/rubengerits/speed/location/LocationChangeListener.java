package be.rubengerits.speed.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import de.greenrobot.event.EventBus;

public class LocationChangeListener implements LocationListener {

    private final LocationEvent event = new LocationEvent();

    @Override
    public void onLocationChanged(Location location) {
        event.setLocation(location);
        post();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        event.setStatus(status);
        post();
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            event.setStatus(Math.max(LocationProvider.TEMPORARILY_UNAVAILABLE, event.getStatus()));
            post();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            event.setStatus(LocationProvider.OUT_OF_SERVICE);
            event.setLocation(null);
            post();
        }
    }

    private void post() {
        EventBus.getDefault().post(event);
    }
}
