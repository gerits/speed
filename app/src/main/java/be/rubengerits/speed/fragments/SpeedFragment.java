package be.rubengerits.speed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;
import be.rubengerits.speedview.SpeedUnit;
import be.rubengerits.speedview.SpeedView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SpeedFragment extends Fragment {

    @BindView(R.id.speedView)
    SpeedView mSpeedView;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_speed, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSpeedView != null) {
            mSpeedView.invalidate();
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Subscribe
    public void onEvent(LocationEvent event) {
        if (event.getLocation() != null && event.getLocation().hasSpeed()) {
            mSpeedView.animateProperty(event.getLocation().getSpeed());
        } else {
            mSpeedView.animateProperty(0f);
        }
    }

    public void setMaxValue(float maxValue) {
        mSpeedView.animateMaxValue(maxValue);
    }

    public void setLocality(SpeedUnit locality) {
        mSpeedView.setLocality(locality);
    }

}
