package net.iGap.module;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import net.iGap.G;
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
    }

    public CustomToggleButton(Context context) {
        this(context, null);
    }

    private void init() {
        setTextOff("");
        setTextOn("");
        if (G.isDarkTheme) {
            setButtonDrawable(R.drawable.st_switch_button_dark);
        } else {
            setButtonDrawable(R.drawable.st_switch_button);
        }
    }

}
