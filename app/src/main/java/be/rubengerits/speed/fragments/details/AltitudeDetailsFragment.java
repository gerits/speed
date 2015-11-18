package be.rubengerits.speed.fragments.details;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;

public class AltitudeDetailsFragment extends AbstractDetailsFragment {

    private double mLastKnowValue = 0;

    @Override
    protected String getName() {
        return getResources().getString(R.string.details_altitude);
    }

    @Override
    protected String getValue() {
        return String.valueOf(mLastKnowValue);
    }

    @Override
    public void onEvent(LocationEvent event) {
        if (event.getLocation().hasAltitude()) {
            mLastKnowValue = event.getLocation().getAltitude();
        } else {
            mLastKnowValue = 0d;
        }
        updateValueDisplay();
    }

}
