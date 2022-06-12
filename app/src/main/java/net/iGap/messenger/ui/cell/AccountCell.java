package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;

import android.view.Gravity;
import android.widget.FrameLayout;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;

public class AccountCell extends FrameLayout {

    private CircleImageView avatarImageView;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;
    private long userId;

    private boolean needDivider;
    private boolean isRTL = G.isAppRtl;
    private int padding = 16;


    public AccountCell(Context context) {
        super(context);
        avatarImageView = new CircleImageView(context);
        addView(avatarImageView, LayoutCreator.createFrame(46, 46, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 0 : 7 + padding, 8, G.isAppRtl ? 7 + padding : 0, 0));

        nameTextView = new SimpleTextView(context);
        nameTextView.setTextSize(17);
        nameTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        nameTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        nameTextView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 28 + 18 : (68 + padding), 11.5f, G.isAppRtl ? (68 + padding) : 28 + 18, 0));

        statusTextView = new SimpleTextView(context);
        statusTextView.setTextSize(14);
        statusTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        statusTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        statusTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(statusTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 28 : (68 + padding), 34.5f, G.isAppRtl ? (68 + padding) : 28, 0));
    }

    public void setValues(String name,String phoneNumber ,boolean needDivider) {
        this.needDivider = needDivider;
        nameTextView.setText(name);
        statusTextView.setText(phoneNumber);
        setWillNotDraw(!needDivider);
    }

    public CircleImageView getAvatarImageView(){
        return avatarImageView;
    }

    public void setTextColor(int nameColor,int statusColor) {
        nameTextView.setTextColor(nameColor);
        statusTextView.setTextColor(statusColor);
        invalidate();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider && Theme.dividerPaint != null) {
            canvas.drawLine(isRTL ? 0 : LayoutCreator.dp(68), getMeasuredHeight() - 1, getMeasuredWidth() - (isRTL ? LayoutCreator.dp(68) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
