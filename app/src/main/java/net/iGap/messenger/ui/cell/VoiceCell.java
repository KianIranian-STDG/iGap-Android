package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;

public class VoiceCell extends FrameLayout {

    private final ImageView imageView;
    private final IconView foregroundIcon;
    private final TextView creationDate;
    private final TextView forwardChannelName;
    private final IconView iconView;
    private final TextView forwardChannelDes;
    private final TextView fromChannelName;

    private final boolean isRtl = G.isAppRtl;

    public VoiceCell(Context context) {
        super(context);

        setWillNotDraw(false);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_rounded_corners_stroke));
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        addView(imageView);

        foregroundIcon = new IconView(context);
        foregroundIcon.setText(getResources().getString(R.string.ic_download));
        foregroundIcon.setTextColor(Theme.getColor(Theme.key_red));
        foregroundIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        foregroundIcon.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        foregroundIcon.setSingleLine();
        foregroundIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        addView(foregroundIcon);

        creationDate = new TextView(context);
        creationDate.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        creationDate.setPadding(0, 0, 0, LayoutCreator.dp(1));
        creationDate.setText("Feb 07 at 12:30 AM");
        creationDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        creationDate.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        creationDate.setSingleLine();
        addView(creationDate);

        forwardChannelName = new TextView(context);
        forwardChannelName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        forwardChannelName.setSingleLine();
        forwardChannelName.setText("TOBTC");
        forwardChannelName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        forwardChannelName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        forwardChannelName.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        addView(forwardChannelName);

        iconView = new IconView(context);
        iconView.setText(getResources().getString(R.string.ic_back));
        iconView.setTextColor(getResources().getColor(R.color.md_yellow_A700));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        iconView.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        iconView.setSingleLine();
        iconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        addView(iconView);

        forwardChannelDes = new TextView(context);
        forwardChannelDes.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        forwardChannelDes.setSingleLine();
        forwardChannelDes.setText("Technical Analysis");
        forwardChannelDes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        forwardChannelDes.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        forwardChannelDes.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        addView(forwardChannelDes);

        fromChannelName = new TextView(context);
        fromChannelName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        fromChannelName.setSingleLine();
        fromChannelName.setText("Channel Name");
        fromChannelName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        fromChannelName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        fromChannelName.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        addView(fromChannelName);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY));
        foregroundIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY));
        creationDate.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        forwardChannelName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        iconView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(14), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(14), MeasureSpec.EXACTLY));
        forwardChannelDes.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        fromChannelName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(16)) / 2, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(52));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int imageViewTop = (getMeasuredHeight() - imageView.getMeasuredHeight()) / 2;
        int imageViewBottom = imageViewTop + imageView.getMeasuredHeight();
        int imageViewLeft = isRtl ? getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int imageViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + imageView.getMeasuredWidth();
        imageView.layout(imageViewLeft, imageViewTop, imageViewRight, imageViewBottom);

        int foregroundIconTop = (getMeasuredHeight() - foregroundIcon.getMeasuredHeight()) / 2;
        int foregroundIconBottom = imageViewTop + foregroundIcon.getMeasuredHeight();
        int foregroundIconLeft = isRtl ? getMeasuredWidth() - foregroundIcon.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int foregroundIconRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + foregroundIcon.getMeasuredWidth();
        foregroundIcon.layout(foregroundIconLeft, foregroundIconTop, foregroundIconRight, foregroundIconBottom);

        int creationDateTop = LayoutCreator.dp(8);
        int creationDateBottom = creationDateTop + creationDate.getMeasuredHeight();
        int creationDateLeft = isRtl ? imageViewLeft - creationDate.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int creationDateRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : creationDateLeft + creationDate.getMeasuredWidth();
        creationDate.layout(creationDateLeft, creationDateTop, creationDateRight, creationDateBottom);

        int forwardChannelNameBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int forwardChannelNameTop = forwardChannelNameBottom - forwardChannelName.getMeasuredHeight();
        int forwardChannelNameLeft = isRtl ? imageViewLeft - forwardChannelName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int forwardChannelNameRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : forwardChannelNameLeft + forwardChannelName.getMeasuredWidth();
        forwardChannelName.layout(forwardChannelNameLeft, forwardChannelNameTop, forwardChannelNameRight, forwardChannelNameBottom);

        int iconViewBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int iconViewTop = iconViewBottom - iconView.getMeasuredHeight();
        int iconViewLeft = isRtl ? forwardChannelNameLeft - iconView.getMeasuredWidth() - LayoutCreator.dp(4) : forwardChannelNameRight + LayoutCreator.dp(4);
        int iconViewRight = isRtl ? forwardChannelNameLeft - LayoutCreator.dp(4) : iconViewLeft + iconView.getMeasuredWidth();
        iconView.layout(iconViewLeft, iconViewTop, iconViewRight, iconViewBottom);

        int forwardChannelDesBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int forwardChannelDesTop = forwardChannelDesBottom - forwardChannelDes.getMeasuredHeight();
        int forwardChannelDesLeft = isRtl ? iconViewLeft - forwardChannelDes.getMeasuredWidth() - LayoutCreator.dp(4) : iconViewRight + LayoutCreator.dp(4);
        int forwardChannelDesRight = isRtl ? iconViewLeft - LayoutCreator.dp(4) : forwardChannelDesLeft + forwardChannelDes.getMeasuredWidth();
        forwardChannelDes.layout(forwardChannelDesLeft, forwardChannelDesTop, forwardChannelDesRight, forwardChannelDesBottom);

        int fromChannelNameBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int fromChannelNameTop = fromChannelNameBottom - fromChannelName.getMeasuredHeight();
        int fromChannelNameLeft = isRtl ? LayoutCreator.dp(8) : getMeasuredWidth() - LayoutCreator.dp(8) - fromChannelName.getMeasuredWidth();
        int fromChannelNameRight = isRtl ? fromChannelNameLeft + fromChannelName.getMeasuredWidth() : getMeasuredWidth() - LayoutCreator.dp(8);
        fromChannelName.layout(fromChannelNameLeft, fromChannelNameTop, fromChannelNameRight, fromChannelNameBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(), Theme.dividerPaint);
        super.onDraw(canvas);
    }

    public void setTextColor(int color) {
        creationDate.setTextColor(color);
        forwardChannelName.setTextColor(color);
        forwardChannelDes.setTextColor(color);
        fromChannelName.setTextColor(color);
        invalidate();
    }
}