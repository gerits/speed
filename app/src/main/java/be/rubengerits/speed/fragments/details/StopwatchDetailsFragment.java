package be.rubengerits.speed.fragments.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import be.rubengerits.speed.R;
import be.rubengerits.speed.speedservices.SpeedServiceRepository;
import be.rubengerits.speed.speedservices.StopWatchSpeedService;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StopwatchDetailsFragment extends AbstractDetailsFragment {

    private StopWatchSpeedService speedService;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_item_action, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        speedService = (StopWatchSpeedService) SpeedServiceRepository.getInstance().getSpeedService(StopWatchSpeedService.ID);
        speedService.addValueChangeListener(new StopWatchSpeedService.StopWatchValueChangeListener() {
            @Override
            public void onValueChanged(String value) {
                if (getView() != null) {
                    updateValueDisplay();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView titleView = ButterKnife.findById(getView(), R.id.list_item_entry_title);
        titleView.setText(getName());

        updateIcon();
        updateValueDisplay();
    }

    @Override
    protected String getName() {
        return getResources().getString(R.string.details_stopwatch);
    }

    @Override
    protected String getValue() {
        return speedService.getValue();
    }

    @OnClick(R.id.list_item_entry_action)
    public void actionClicked() {
        if (StopWatchSpeedService.Status.RESET.equals(speedService.getStatus())) {
            speedService.start();
        } else if (StopWatchSpeedService.Status.STARTED.equals(speedService.getStatus())) {
            speedService.stop();
        } else {
            speedService.reset();
        }
        updateIcon();
        updateValueDisplay();
    }

    private void updateIcon() {
        ImageButton actionButton = ButterKnife.findById(getView(), R.id.list_item_entry_action);
        if (StopWatchSpeedService.Status.RESET.equals(speedService.getStatus())) {
            actionButton.setImageResource(R.drawable.ic_play_arrow);
        } else if (StopWatchSpeedService.Status.STARTED.equals(speedService.getStatus())) {
            actionButton.setImageResource(R.drawable.ic_stop);
        } else {
            actionButton.setImageResource(R.drawable.ic_replay);
        }
    }
}
