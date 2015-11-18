package be.rubengerits.speed;

import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageButton;

import be.rubengerits.speed.speedservices.GpsStatusService;
import be.rubengerits.speed.speedservices.SpeedServiceRepository;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToolsActivity extends AppCompatActivity implements GpsStatusService.GpsStatusValueChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tools);

        ButterKnife.bind(this);

        Toolbar toolBar = ButterKnife.findById(this, R.id.speedToolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());

            Transition transition = new ChangeBounds();
            transition.addTarget(R.id.tools_panel);
            transition.addTarget("tools");
            getWindow().setSharedElementEnterTransition(transition);
        }
    }

    @OnClick(R.id.back_button)
    public void onBackClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GpsStatusService gpsStatusService = (GpsStatusService) SpeedServiceRepository.getInstance().getSpeedService(GpsStatusService.ID);
        gpsStatusService.addValueChangeListener(this);
        onGpsStatusChanged(gpsStatusService.getStatus());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GpsStatusService gpsStatusService = (GpsStatusService) SpeedServiceRepository.getInstance().getSpeedService(GpsStatusService.ID);
        gpsStatusService.removeValueChangeListener(this);
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
