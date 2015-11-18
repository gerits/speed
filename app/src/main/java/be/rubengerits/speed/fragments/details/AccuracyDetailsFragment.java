package be.rubengerits.speed.fragments.details;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;

public class AccuracyDetailsFragment extends AbstractDetailsFragment {

    private float mLastKnowAccuracy = 0;

    @Override
    protected String getName() {
        return getResources().getString(R.string.details_accuracy);
    }

    @Override
    protected String getValue() {
        return String.valueOf(mLastKnowAccuracy);
    }

    @Override
    public void onEvent(LocationEvent event) {
        if (event.getLocation().hasAccuracy()) {
            mLastKnowAccuracy = event.getLocation().getAccuracy();
        } else {
            mLastKnowAccuracy = 0.0f;
        }
        updateValueDisplay();
    }

}
