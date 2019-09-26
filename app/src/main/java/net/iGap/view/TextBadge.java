package net.iGap.view;

import android.content.Context;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;

public class TextBadge extends AppCompatTextView {

    public static final int DARK_MODE = 0;
    public static final int MUTE_MODE = 1;
    public static final int UNMUTE_MODE = 2;

    public TextBadge(Context context) {
        super(context);
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setTextSize(10);
        setGravity(Gravity.CENTER);
        setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        setBackground(getResources().getDrawable(R.drawable.background_badge));
    }

    public void setBadgeColor(int color) {
        switch (color) {
            case DARK_MODE:
                setBackgroundResource(R.drawable.background_badge_dark);
                break;
            case MUTE_MODE:
                setBackgroundResource(R.drawable.background_badge_mute);
                break;
            case UNMUTE_MODE:
                setBackgroundResource(R.drawable.background_badge);
                break;
        }
    }

}
