package be.rubengerits.speed.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;
import be.rubengerits.speedview.SpeedUnit;
import be.rubengerits.speedview.SpeedView;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class SpeedFragment extends Fragment implements SensorEventListener {

    @Bind(R.id.speedView)
    protected SpeedView mSpeedView;

    private SensorManager mSensorManager;
    private Sensor mLight;

    public SpeedFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_speed, container, false);
        ButterKnife.bind(this, result);
        return result;
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

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        mSensorManager.unregisterListener(this);
    }

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

    @Override
    public void onSensorChanged(SensorEvent event) {
//        TODO enable light/dark theme?
//        if (event.values[0] > 50) {
//            // light theme
//        } else {
//            // dark theme
//            getActivity().getSystemService(Context.UI_MODE_SERVICE);
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
