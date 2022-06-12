package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;

public class EditCell extends FrameLayout {

    private IconView iconView;
    private EditText editText;
    private TextView errorText;

    private boolean isRtl = G.isAppRtl;
    private int padding = 16;
    private int iconWidth = 30;

    public EditCell(@NonNull Context context) {
        super(context);

        iconView = new IconView(context);
        iconView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon_new));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28);
        addView(iconView, LayoutCreator.createFrame(iconWidth, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, padding, (padding * 2) + 6, padding, 0));

        int maxLength = 180;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        editText = new EditText(context);
        editText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        editText.setFilters(fArray);
        addView(editText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, isRtl ? padding : padding * 3, (padding * 2), isRtl ? padding * 3 : padding, padding * 4));

        errorText = new TextView(context);
        errorText.setSingleLine();
        errorText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        errorText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(errorText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, isRtl ? padding : (padding * 3) + 6, padding * 3, isRtl ? (padding * 3) + 6 : padding, (padding * 3)));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void setValues(int icon, @Nullable String title, String hint, String content) {
        iconView.setIcon(icon);
        editText.setHint(hint);
        if (title != null)
            editText.setText(title);
        errorText.setText(content);
    }

    public void setTextColor(int titleColor, int contentColor, int iconColor) {
        iconView.setIconColor(iconColor);
        editText.setTextColor(titleColor);
        errorText.setTextColor(contentColor);
        invalidate();
    }
}
