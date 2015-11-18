package be.rubengerits.android.imagetogglebar;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ImageToggleButton extends ImageButton {

    private float value;

    public ImageToggleButton(Context context) {
        super(context);
        throw new UnsupportedOperationException("Constructor should not be used");
    }

    public ImageToggleButton(Context context, int buttonSize, int resource, float value) {
        super(context);

        this.value = value;

        setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        setImageResource(resource);
        setBackground(ContextCompat.getDrawable(context, R.drawable.imagebutton_selector));
    }

    public void select() {
        setImageAlpha(255);
    }

    public void unselect() {
        setImageAlpha(127);
    }

    public float getValue() {
        return value;
    }
}
