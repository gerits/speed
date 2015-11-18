package be.rubengerits.speed.location;

import android.location.Location;

public class LocationEvent {
    private Location location;
    private int status;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
