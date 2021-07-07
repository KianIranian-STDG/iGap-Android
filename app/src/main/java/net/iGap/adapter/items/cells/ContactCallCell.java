package net.iGap.adapter.items.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.customView.CheckBox;

public class ContactCallCell extends FrameLayout {
    private CheckBox checkBox;
    private CircleImageView avatarImage;
    private TextView titleView;
    private TextView phoneNumberView;
    private IconView callIcon;
    private boolean isRtl = G.isAppRtl;

    public ContactCallCell(@NonNull Context context) {
        super(context);
        initLayout(context);
    }

    public ContactCallCell(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public void setCellData(CharSequence title, String phoneNumber) {
        titleView.setText(title);
        phoneNumberView.setText(phoneNumber);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public CircleImageView getAvatarImage() {
        return avatarImage;
    }

    private void initLayout(Context context) {
        checkBox = new CheckBox(context);
        checkBox.setVisibility(GONE);
        addView(checkBox);

        avatarImage = new CircleImageView(context);
        addView(avatarImage);

        titleView = new TextView(context);
        titleView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
        titleView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(titleView);

        phoneNumberView = new TextView(context);
        phoneNumberView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        phoneNumberView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        phoneNumberView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(phoneNumberView);

        callIcon = new IconView(context);
        callIcon.setText(R.string.icon_voice_call);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int titleLeft = 0;

        setMeasuredDimension(width, LayoutCreator.dp(70));
        if (checkBox.getVisibility() != GONE) {
            checkBox.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(8), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        }

        if (avatarImage.getVisibility() != GONE) {
            avatarImage.measure(MeasureSpec.makeMeasureSpec(52, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(52,MeasureSpec.EXACTLY));
            titleLeft = LayoutCreator.dp(60);
        }
        if (callIcon.getVisibility() != GONE) {
            callIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY), callIcon.getMeasuredWidth());
        }
        if (titleView.getVisibility() != GONE) {
            int titleWidth = width - (callIcon.getVisibility() == VISIBLE ? callIcon.getMeasuredWidth() : 0) - titleLeft;
            titleView.measure(MeasureSpec.makeMeasureSpec(titleWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
            phoneNumberView.measure(MeasureSpec.makeMeasureSpec(titleWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int titleLeft;
        int avatarLeft;
        int avatarRight;
        int standardMargin = LayoutCreator.dp(5);
        int topMargin = LayoutCreator.dp(3);
        int avatarBottom;

        avatarLeft = isRtl ? getMeasuredWidth() - standardMargin - avatarImage.getMeasuredWidth() : standardMargin;
        avatarRight = isRtl ? standardMargin : standardMargin + avatarImage.getMeasuredWidth();
        avatarBottom = avatarImage.getMeasuredHeight() - topMargin;
        if (avatarImage.getVisibility() != GONE) {
            avatarImage.layout(avatarLeft, topMargin, avatarRight, avatarBottom);
        }

        if (checkBox.getVisibility() != GONE) {
            checkBox.layout(isRtl ? avatarLeft : avatarRight - LayoutCreator.dp(44), avatarBottom - LayoutCreator.dp(20), isRtl ? avatarLeft + checkBox.getMeasuredWidth() : avatarRight, avatarBottom);
        }

        if (titleView.getVisibility() != GONE) {
            int titleTop = getMeasuredHeight() / 2 - titleView.getMeasuredHeight();
            titleLeft = isRtl ? avatarLeft + standardMargin + getMeasuredWidth() : avatarRight + standardMargin;
            int titleRight = isRtl ? avatarLeft + standardMargin : titleLeft + titleView.getMeasuredWidth();
            titleView.layout(titleLeft, titleTop, titleRight, getMeasuredHeight() / 2);

            if (phoneNumberView.getVisibility() != GONE) {
                int phoneNumberTop = getMeasuredHeight() / 2 + standardMargin;
                phoneNumberView.layout(titleLeft, phoneNumberTop, titleRight, phoneNumberTop + phoneNumberView.getMeasuredHeight());
            }
        }

        if (callIcon.getVisibility() != GONE) {
            int callLeft = isRtl ? standardMargin : getMeasuredWidth() - standardMargin - callIcon.getMeasuredWidth();
            int callRight = isRtl ? standardMargin + callIcon.getMeasuredWidth() : getMeasuredWidth() - standardMargin;
            callIcon.layout(callLeft, topMargin, callRight, topMargin + getMeasuredHeight());
        }
    }
}
