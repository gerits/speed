package be.rubengerits.speed.fragments.details;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.rubengerits.speed.R;
import be.rubengerits.speed.location.LocationEvent;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public abstract class AbstractDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_item, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = ButterKnife.findById(view, R.id.list_item_entry_title);
        title.setText(getName());

        updateValueDisplay();
    }

    protected void updateValueDisplay() {
        if (getView() != null) {
            TextView textView = ButterKnife.findById(getView(), R.id.list_item_entry_summary);

            if (getValue() != null) {
                textView.setText(String.valueOf(getValue()));
            } else {
                textView.setText("-");
            }
        }
    }

    protected abstract String getName();

    protected abstract String getValue();

    public void onEvent(LocationEvent event) {
        // required for EventBus support in sub classes
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
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

}
