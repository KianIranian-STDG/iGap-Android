package net.iGap.module;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import net.iGap.R;

public class CustomToggleButton extends ToggleButton {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public CustomToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CustomToggleButton(Context context) {
        this(context, null);
        init();
    }

    private void init() {
        setTextOff("");
        setTextOn("");

        ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), Theme.getInstance().getTheme(getContext()));

        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_pressed}, VectorDrawableCompat.create(getResources(), R.drawable.toggle_state_on, wrapper.getTheme()));
        listDrawable.addState(new int[]{android.R.attr.state_checked, -android.R.attr.state_pressed}, VectorDrawableCompat.create(getResources(), R.drawable.toggle_state_on, wrapper.getTheme()));
        listDrawable.addState(new int[]{-android.R.attr.state_checked, android.R.attr.state_pressed}, VectorDrawableCompat.create(getResources(), R.drawable.toggle_state_off, wrapper.getTheme()));
        listDrawable.addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_pressed}, VectorDrawableCompat.create(getResources(), R.drawable.toggle_state_off, wrapper.getTheme()));
        setButtonDrawable(listDrawable);
    }

}
