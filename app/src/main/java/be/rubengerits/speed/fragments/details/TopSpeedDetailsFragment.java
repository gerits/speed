package be.rubengerits.speed.fragments.details;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.rubengerits.speed.R;
import be.rubengerits.speed.speedservices.SpeedServiceRepository;
import be.rubengerits.speed.speedservices.TopSpeedService;
import be.rubengerits.speed.utils.SpeedUtils;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopSpeedDetailsFragment extends AbstractDetailsFragment {

    private TopSpeedService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = (TopSpeedService) SpeedServiceRepository.getInstance().getSpeedService(TopSpeedService.ID);
        service.addValueChangeListener(new TopSpeedService.TopSpeedValueChangeListener() {
            @Override
            public void onValueChanged(Float value) {
                updateValueDisplay();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView titleView = ButterKnife.findById(getView(), R.id.list_item_entry_title);
        titleView.setText(getName());

        updateValueDisplay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_item_action, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected String getName() {
        return getResources().getString(R.string.details_max_speed);
    }

    @Override
    protected String getValue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        String locality = "kmh";
        if (preferences != null) {
            locality = preferences.getString("pref_locality", locality);
        }

        String translatedLocality = SpeedUtils.getTranslatedLocality(locality, getActivity().getApplicationContext());

        return Math.round(SpeedUtils.calculateLocalSpeed(service.getValue(), getActivity().getApplicationContext())) + " " + translatedLocality;
    }

    @OnClick(R.id.list_item_entry_action)
    public void onRestClicked(View v) {
        service.reset();
    }

}
