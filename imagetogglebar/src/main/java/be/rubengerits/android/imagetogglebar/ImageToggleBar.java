package be.rubengerits.android.imagetogglebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ImageToggleBar extends LinearLayout {

    private int buttonSize;

    private List<ImageToggleBarValueChangeListener> listeners = new ArrayList<>();

    private List<ImageToggleButton> allButtons = new ArrayList<>();
    private float selectedValue = 0f;

    public ImageToggleBar(Context context) {
        super(context);

        setOrientation(HORIZONTAL);
    }

    public ImageToggleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageToggleBar, 0, 0);
        try {
            buttonSize = a.getDimensionPixelSize(R.styleable.ImageToggleBar_buttonSize, LinearLayout.LayoutParams.WRAP_CONTENT);
        } finally {
            a.recycle();
        }

        setOrientation(HORIZONTAL);
    }

    public ImageToggleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageToggleBar, 0, 0);
        try {
            buttonSize = a.getDimensionPixelSize(R.styleable.ImageToggleBar_buttonSize, LinearLayout.LayoutParams.WRAP_CONTENT);
        } finally {
            a.recycle();
        }

        setOrientation(HORIZONTAL);
    }

    public void addItem(int resource, float value) {
        ImageToggleButton button = new ImageToggleButton(getContext(), buttonSize, resource, value);

        button.setOnClickListener(new ImageToggleButtonListener());

        allButtons.add(button);
        super.addView(button, -1, button.getLayoutParams());
    }

    public float getSelectedSpeed() {
        return selectedValue;
    }

    public void addValueChangeListener(ImageToggleBarValueChangeListener listener) {
        listeners.add(listener);
    }

    public void select(int location) {
        selectButton(allButtons.get(location));
    }

    private void notifyValueChanged(int location, float value) {
        for (ImageToggleBarValueChangeListener listener : listeners) {
            listener.onValueChanged(location, value);
        }
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        allButtons.clear();
    }

    private void selectButton(ImageToggleButton selectedButton) {
        for (ImageToggleButton button : allButtons) {
            button.unselect();
        }
        selectedButton.select();
        selectedValue = selectedButton.getValue();
        notifyValueChanged(allButtons.indexOf(selectedButton), selectedButton.getValue());
    }

    @Override
    public void addView(View child) {
        throw new IllegalStateException("Cannot add children to this type of view.");
    }

    @Override
    public void addView(View child, int index) {
        throw new IllegalStateException("Cannot add children to this type of view.");
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        throw new IllegalStateException("Cannot add children to this type of view.");
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        throw new IllegalStateException("Cannot add children to this type of view.");
    }

    @Override
    public void addView(View child, int width, int height) {
        throw new IllegalStateException("Cannot add children to this type of view.");
    }

    private class ImageToggleButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            selectButton((ImageToggleButton) v);
        }
    }
}
