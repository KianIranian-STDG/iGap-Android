package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;

public class NearByTitleCell extends LinearLayout {
    private ImageView titleIcon;
    private TextView titleTextView;
    private TextView detailTextView;

    public NearByTitleCell(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setLayoutParams(LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        titleIcon = new ImageView(context);
        addView(titleIcon, LayoutCreator.createLinear(50, 50, Gravity.CENTER, 0, 15, 0, 0));

        titleTextView = new TextView(context);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.BOLD);
        addView(titleTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 8, 0, 0));

        detailTextView = new TextView(context);
        detailTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        detailTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(detailTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 20, 15, 20, 15));
    }

    public void setTitleTextColor(int color) {
        this.titleTextView.setTextColor(color);
    }

    public void setDetailTextColor(int color) {
        this.detailTextView.setTextColor(color);
    }

    public void setValues(int icon, String title, String detail) {
        titleIcon.setImageDrawable(getContext().getResources().getDrawable(icon));
        titleTextView.setText(title);
        detailTextView.setText(detail);
    }

    public void setTextColor(int color) {
        this.titleTextView.setTextColor(color);
        this.detailTextView.setTextColor(color);
        invalidate();
    }
}
