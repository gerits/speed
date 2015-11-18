package be.rubengerits.speed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import be.rubengerits.android.imagetogglebar.ImageToggleBar;
import be.rubengerits.android.imagetogglebar.ImageToggleBarValueChangeListener;
import be.rubengerits.speed.fragments.SpeedFragment;
import be.rubengerits.speed.location.LocationChangeListener;
import be.rubengerits.speed.location.LocationEvent;
import be.rubengerits.speed.speedservices.GpsStatusService;
import be.rubengerits.speed.speedservices.SpeedServiceRepository;
import be.rubengerits.speed.speedservices.StopWatchSpeedService;
import be.rubengerits.speed.speedservices.TopSpeedService;
import be.rubengerits.speedview.SpeedUnit;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpeedActivity extends AppCompatActivity implements ImageToggleBarValueChangeListener, GpsStatusService.GpsStatusValueChangeListener {

    public static final String SPEED_TYPE = "speedType";

    public static final String SPEED_LOCALITY = "speedLocality";
    public static final String SPEED_LIMIT_CAR = "speedLimitCar";
    public static final String SPEED_LIMIT_BIKE = "speedLimitBike";
    public static final String SPEED_LIMIT_WALK = "speedLimitWalk";

    private LocationManager mLocationManager;
    private LocationChangeListener mLocationChangeListener;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolBar = ButterKnife.findById(this, R.id.speedToolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setupSpeedServices();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationChangeListener = new LocationChangeListener();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupSpeedType();

        SeekBar speedAdjuster = ButterKnife.findById(this, R.id.speedAdjuster);
        speedAdjuster.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SpeedFragment fragment = (SpeedFragment) getSupportFragmentManager().findFragmentById(R.id.speedFragment);

                if (fragment != null) {
                    LocationEvent locationEvent = new LocationEvent();
                    Location location = new Location("");
                    location.setSpeed(seekBar.getProgress());
                    locationEvent.setLocation(location);
                    fragment.onEvent(locationEvent);
                }
            }
        });

    }

    private void setupSpeedServices() {
        SpeedServiceRepository.getInstance().registerService(new StopWatchSpeedService());
        SpeedServiceRepository.getInstance().registerService(new TopSpeedService(this));
        SpeedServiceRepository.getInstance().registerService(new GpsStatusService());
    }

    private void setupSpeedType() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ImageToggleBar speedTypeBar = ButterKnife.findById(this, R.id.image_toggle_bar);
        speedTypeBar.addItem(R.drawable.ic_directions_car, preferences.getFloat(SPEED_LIMIT_CAR, 41.666666666667f));
        speedTypeBar.addItem(R.drawable.ic_directions_bike, preferences.getFloat(SPEED_LIMIT_BIKE, 19.444444444444f));
        speedTypeBar.addItem(R.drawable.ic_directions_walk, preferences.getFloat(SPEED_LIMIT_WALK, 8.3333333333333f));
        speedTypeBar.addValueChangeListener(this);

        int speedType = preferences.getInt(SPEED_TYPE, 0);
        speedTypeBar.select(speedType);
    }

    @Override
    public void onValueChanged(int location, float value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SpeedActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SPEED_TYPE, location);
        editor.apply();

        SpeedFragment fragment = (SpeedFragment) getSupportFragmentManager().findFragmentById(R.id.speedFragment);
        if (fragment != null) {
            fragment.setMaxValue(value);
        }

        View speedAdjusterPanel = ButterKnife.findById(this, R.id.speedAdjusterPanel);
        if (View.VISIBLE == speedAdjusterPanel.getVisibility()) {
            SeekBar speedAdjuster = ButterKnife.findById(this, R.id.speedAdjuster);
            ImageToggleBar speedToggleBar = ButterKnife.findById(this, R.id.image_toggle_bar);
            speedAdjuster.setProgress(Math.round(speedToggleBar.getSelectedSpeed()));
        }
    }

    @OnClick(R.id.tools_button)
    public void showToolsPanel(View v) {
        Intent intent = new Intent(this, ToolsActivity.class);

        TransitionSet set = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            set = new TransitionSet();
            Transition changeBounds = new ChangeBounds();
            changeBounds.addTarget(R.id.tools_panel);
            changeBounds.addTarget(R.id.tools_button);
            set.addTransition(changeBounds);
        }

        LinearLayout toolPanel = ButterKnife.findById(this, R.id.tools_panel);
        String transitionName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            transitionName = toolPanel.getTransitionName();
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, toolPanel, transitionName);
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationChangeListener);

        GpsStatusService gpsStatusService = (GpsStatusService) SpeedServiceRepository.getInstance().getSpeedService(GpsStatusService.ID);
        gpsStatusService.addValueChangeListener(this);
        gpsStatusService.setStatus(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocationManager.removeUpdates(mLocationChangeListener);

        GpsStatusService gpsStatusService = (GpsStatusService) SpeedServiceRepository.getInstance().getSpeedService(GpsStatusService.ID);
        gpsStatusService.removeValueChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.speed, menu);

        resetMenuSelection(menu);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        menu.getItem(getSpeedLocality(menu, preferences)).setChecked(true);

        return true;
    }

    private int getSpeedLocality(Menu menu, SharedPreferences preferences) {
        int selectedItem = preferences.getInt(SPEED_LOCALITY, 0);
        if (selectedItem >= menu.size()) {
            selectedItem = 0;
        }
        return selectedItem;
    }

    private void resetMenuSelection(Menu menu) {
        menu.findItem(R.id.menu_locality_metric).setChecked(false);
        menu.findItem(R.id.menu_locality_imperial).setChecked(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_locality_imperial == item.getItemId() || R.id.menu_locality_metric == item.getItemId()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SPEED_LOCALITY, item.getItemId() == R.id.menu_locality_metric ? 0 : 1);
            editor.apply();

            resetMenuSelection(menu);

            item.setChecked(true);

            SpeedFragment fragment = (SpeedFragment) getSupportFragmentManager().findFragmentById(R.id.speedFragment);
            if (fragment != null) {
                fragment.setLocality(R.id.menu_locality_imperial == item.getItemId() ? SpeedUnit.imperial : SpeedUnit.metric);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.speedAdjusterConfirm)
    public void saveMaxSpeed(View v) {
        View speedAdjusterPanel = ButterKnife.findById(this, R.id.speedAdjusterPanel);
        if (View.VISIBLE == speedAdjusterPanel.getVisibility()) {
            speedAdjusterPanel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_down));
            speedAdjusterPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGpsStatusChanged(int status) {
        ImageButton gpsStatus = ButterKnife.findById(this, R.id.gps_status);

        if (status == LocationProvider.OUT_OF_SERVICE) {
            gpsStatus.setImageResource(R.drawable.ic_gps_off);
        } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            gpsStatus.setImageResource(R.drawable.ic_gps_not_fixed);
        } else {
            gpsStatus.setImageResource(R.drawable.ic_gps_fixed);
        }
    }

}
