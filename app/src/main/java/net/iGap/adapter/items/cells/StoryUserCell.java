package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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

    private IconClicked iconClicked;


    public StoryUserCell(@NonNull Context context, boolean needDivider, IconClicked iconClicked) {
        super(context);
        this.needDivider = needDivider;
        this.iconClicked = iconClicked;
        setWillNotDraw(!needDivider);

        circleImage = new CircleImageView(getContext());
        addView(circleImage, LayoutCreator.createFrame(46, 46, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? 0 : padding, 8, isRtl ? padding : 0, 0));

        topText = new TextView(context);
        topText.setSingleLine();
        topText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        topText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        topText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(topText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 20, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : (56 + padding), 11.5f, isRtl ? (56 + padding) : padding, 0));


        bottomText = new TextView(context);
        bottomText.setSingleLine();
        bottomText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        bottomText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        bottomText.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(bottomText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, 20, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, isRtl ? padding : (56 + padding), 34.5f, isRtl ? (56 + padding) : padding, 0));


        icon = new IconView(getContext());
        icon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, padding, 0, padding, 0));

        icon2 = new IconView(getContext());
        icon2.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon));
        icon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        addView(icon2, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? (padding + 30) : padding, 0, isRtl ? padding : (30 + padding), 0));

        this.iconClicked.clickedIcon(icon, icon2);

    }


    public void setText(String topText, String bottomText) {
        this.topText.setText(topText);
        this.bottomText.setText(bottomText);
    }

    public void setTextColor(int colorLeftText, int colorBottomText) {
        this.topText.setTextColor(colorLeftText);
        this.bottomText.setTextColor(colorBottomText);
    }

    public void setIconsValue(int icon, int icon2) {
        this.setIconsValue(icon, icon2, Color.GRAY, Color.GRAY);
    }

    public void setIconsValue(int icon, int icon2, int color, int color2) {
        this.icon.setIcon(icon);
        this.icon2.setIcon(icon2);
        this.icon.setTextColor(color);
        this.icon2.setTextColor(color2);
    }

    public void setImage(int imageId) {
        this.circleImage.setImageResource(imageId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
        }
    }

    public interface IconClicked {
        void clickedIcon(View icon, View icon2);
    }

}
