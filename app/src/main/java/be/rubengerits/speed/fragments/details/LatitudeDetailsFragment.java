package be.rubengerits.speed.fragments.details;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;

public class LatitudeDetailsFragment extends AbstractDetailsFragment {

    private double mLastKnowValue = 0;

    @Override
    protected String getName() {
        return getResources().getString(R.string.details_latitude);
    }

    @Override
    protected String getValue() {
        return String.valueOf(mLastKnowValue);
    }

    @Override
    public void onEvent(LocationEvent event) {
        mLastKnowValue = event.getLocation().getLatitude();
        updateValueDisplay();
    }

}
