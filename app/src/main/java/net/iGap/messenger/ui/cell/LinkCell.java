package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;

public class LinkCell extends FrameLayout {

    private final IconView iconView;
    private final TextView textView;
    private final LinearLayout linearLinks;
    private final boolean isRtl = G.isAppRtl;

    public LinkCell(Context context) {
        super(context);

        setWillNotDraw(false);

        iconView = new IconView(context);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_multi_select_light));
        iconView.setIcon(R.string.ic_attach);
        addView(iconView);

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setClickable(false);
        textView.setFocusable(false);
        textView.setLinksClickable(false);
        textView.setLinkTextColor(Theme.getColor(Theme.key_title_text));
        textView.setTextColor(Theme.getColor(Theme.key_title_text));
        textView.setText("Link Content - Link Content - Link Content - Link Content - Link Content - Link Content - Link Content - Link Content - Link Content");
        textView.setMaxLines(3);
        addView(textView);

        linearLinks = new LinearLayout(context);
        linearLinks.setOrientation(LinearLayout.VERTICAL);
        linearLinks.setMinimumHeight(LayoutCreator.dp(20));
        TextView link = new TextView(context);
        link.setText("https://stackoverflow.com/questions/55472005/nested-recyclerview-onclick-not-triggered");
        linearLinks.addView(link, LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT);
        addView(linearLinks);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        iconView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52), MeasureSpec.EXACTLY));
        textView.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - iconView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec((getMeasuredHeight() - LayoutCreator.dp(8)) / 2, MeasureSpec.AT_MOST));
        linearLinks.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - iconView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec((getMeasuredHeight() - LayoutCreator.dp(8)) / 2, MeasureSpec.AT_MOST));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(100));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int coverTextTop = (getMeasuredHeight() - iconView.getMeasuredHeight()) / 2;
        int coverTextBottom = coverTextTop + iconView.getMeasuredHeight();
        int coverTextLeft = isRtl ? getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int coverTextRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + iconView.getMeasuredWidth();
        iconView.layout(coverTextLeft, coverTextTop, coverTextRight, coverTextBottom);

        int messageTextTop = LayoutCreator.dp(8);
        int messageTextBottom = messageTextTop + textView.getMeasuredHeight();
        int messageTextLeft = isRtl ? coverTextLeft - textView.getMeasuredWidth() - LayoutCreator.dp(8) : coverTextRight + LayoutCreator.dp(8);
        int messageTextRight = isRtl ? coverTextLeft - LayoutCreator.dp(8) : messageTextLeft + textView.getMeasuredWidth();
        textView.layout(messageTextLeft, messageTextTop, messageTextRight, messageTextBottom);

        int linearLinksBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int linearLinksTop = linearLinksBottom - linearLinks.getMeasuredHeight();
        int linearLinksLeft = isRtl ? coverTextLeft - linearLinks.getMeasuredWidth() - LayoutCreator.dp(8) : coverTextRight + LayoutCreator.dp(8);
        int linearLinksRight = isRtl ? coverTextLeft - LayoutCreator.dp(8) : linearLinksLeft + linearLinks.getMeasuredWidth();
        linearLinks.layout(linearLinksLeft, linearLinksTop, linearLinksRight, linearLinksBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : iconView.getRight(), getMeasuredHeight() - 1, isRtl ? iconView.getLeft() : getWidth(), getMeasuredHeight(), Theme.dividerPaint);
        super.onDraw(canvas);
    }
}
