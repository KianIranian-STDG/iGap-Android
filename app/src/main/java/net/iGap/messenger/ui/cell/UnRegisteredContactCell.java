
package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.structs.StructListOfContact;

public class UnRegisteredContactCell extends FrameLayout {
    private final ImageView ivAdd;
    private final TextView tvContactName;
    private final TextView tvContactPhone;
    private final boolean isRtl = G.isAppRtl;

    public UnRegisteredContactCell(@NonNull Context context) {
        super(context);
        int leftMargin;
        int rightMargin;

        ivAdd = new ImageView(context);
        ivAdd.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.add_chat_background), getContext(), Theme.getColor(Theme.key_theme_color)));
        leftMargin = 16;
        rightMargin = 16;
        addView(ivAdd, LayoutCreator.createFrame(52, 52, Gravity.CENTER | Gravity.START, leftMargin, 8, rightMargin, 8));

        tvContactName = new TextView(context);
        tvContactName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tvContactName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        tvContactName.setTextColor(Color.BLACK);
        tvContactName.setSingleLine();
        leftMargin = isRtl ? 0 : 74;
        rightMargin = isRtl ? 74 : 0;
        addView(tvContactName, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.START, leftMargin, 0, rightMargin, 12));

        tvContactPhone = new TextView(context);
        tvContactPhone.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        tvContactPhone.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        tvContactPhone.setTextColor(Color.BLACK);
        tvContactPhone.setSingleLine();
        addView(tvContactPhone, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER | Gravity.START, leftMargin, 10, rightMargin, 0));

        setLayoutParams(LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT,56,Gravity.CENTER,0,4,0,8));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(isRtl ? 4 : ivAdd.getRight(), getMeasuredHeight() - 1, isRtl ? ivAdd.getLeft() : getWidth(), getMeasuredHeight(), Theme.dividerPaint);
    }

    public void setValues(StructListOfContact contact) {
        tvContactName.setText(contact.getDisplayName());
        tvContactPhone.setText(contact.getPhone());
    }

    public void setTextColor(int color) {
        tvContactName.setTextColor(color);
        tvContactPhone.setTextColor(color);
        invalidate();
    }
}
