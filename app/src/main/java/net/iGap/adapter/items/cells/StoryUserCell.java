package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.module.CircleImageView;
import net.iGap.module.Theme;

public class StoryUserCell extends FrameLayout {

    private CircleImageView circleImage;
    private TextView topText;
    private TextView bottomText;
    private IconView icon;
    private IconView icon2;

    private int padding = 16;
    private boolean isRtl = G.isAppRtl;
    private boolean needDivider;


    public StoryUserCell(@NonNull Context context) {
        super(context);

        circleImage = new CircleImageView(getContext());
        circleImage.setImageResource(R.color.pink);
        addView(circleImage, LayoutCreator.createFrame(46, 46, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 0));

        topText = new TextView(context);
        topText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        topText.setText("top");
        topText.setMaxLines(1);
        topText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        topText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(topText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 20, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : (56 + padding), 11.5f, isRtl ? (56 + padding) : padding, 0));


        bottomText = new TextView(context);
        bottomText.setMaxLines(4);
        bottomText.setText("bottom");
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        bottomText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(bottomText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 20, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : (56 + padding), 34.5f, isRtl ? (56 + padding) : padding, 0));


        icon = new IconView(getContext());
        icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon.setText(R.string.md_send_button);
        icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        icon.setTextColor(Color.GRAY);
        addView(icon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, padding, 0, padding, 0));

        icon2 = new IconView(getContext());
        icon2.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon2.setIcon(R.string.more_icon);
        icon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        icon2.setTextColor(Color.GRAY);
        addView(icon2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 30
        ) : padding, 0, isRtl ? padding : (30 + padding), 0));
    }


    public void setText(String leftText, String bottomText, boolean needDivider) {
        this.needDivider = needDivider;
        this.topText.setText(leftText);
        this.bottomText.setText(bottomText);
    }

    public void setTextColor(int colorLeftText, int colorBottomText) {
        this.topText.setTextColor(colorLeftText);
        this.bottomText.setTextColor(colorBottomText);
    }

    public void setIcons(int icon, int icon2) {
        this.icon.setIcon(icon);
        this.icon2.setIcon(icon2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 5, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 2, Theme.getInstance().getDividerPaint(getContext()));
        }
    }
}
