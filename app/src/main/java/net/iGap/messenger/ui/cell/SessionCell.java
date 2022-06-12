package net.iGap.messenger.ui.cell;


import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.structs.StructSessions;

import java.util.Locale;

public class SessionCell extends FrameLayout {

    private TextView nameTextView;
    private TextView onlineTextView;
    private TextView detailTextView;
    private TextView detailExTextView;
    private long sessionId;

    public SessionCell(Context context) {
        super(context);
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setWeightSum(1);
        addView(linearLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 30, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (G.isAppRtl ? 15 : 21), 11, (G.isAppRtl ? 21 : 15), 0));

        nameTextView = new TextView(context);
        nameTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setLines(1);
        nameTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        nameTextView.setMaxLines(1);
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        nameTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);

        onlineTextView = new TextView(context);
        onlineTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        onlineTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        onlineTextView.setGravity((G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP);

        if (G.isAppRtl) {
            linearLayout.addView(onlineTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.TOP, 0, 2, 0, 0));
            linearLayout.addView(nameTextView, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1.0f, Gravity.RIGHT | Gravity.TOP, 10, 0, 0, 0));
        } else {
            linearLayout.addView(nameTextView, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1.0f, Gravity.LEFT | Gravity.TOP, 0, 0, 10, 0));
            linearLayout.addView(onlineTextView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT | Gravity.TOP, 0, 2, 0, 0));
        }

        detailTextView = new TextView(context);
        detailTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        detailTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        detailTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        detailTextView.setLines(1);
        detailTextView.setMaxLines(1);
        detailTextView.setSingleLine(true);
        detailTextView.setEllipsize(TextUtils.TruncateAt.END);
        detailTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(detailTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 36, 21, 0));

        detailExTextView = new TextView(context);
        detailExTextView.setTextColor(Theme.getColor(Theme.key_icon));
        detailExTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        detailExTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        detailExTextView.setLines(1);
        detailExTextView.setMaxLines(1);
        detailExTextView.setSingleLine(true);
        detailExTextView.setEllipsize(TextUtils.TruncateAt.END);
        detailExTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(detailExTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 59, 21, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(91) , MeasureSpec.EXACTLY));
    }

    public void setSession(StructSessions structSessions) {
        setSessionId(structSessions.getSessionId());
        nameTextView.setText(String.format(Locale.US, "%s %s", structSessions.getDeviceName(), structSessions.getPlatform()));
        setTag(Theme.key_title_text);
        onlineTextView.setText(getContext().getString(R.string.Online));
        onlineTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        StringBuilder stringBuilder = new StringBuilder();
        if ( structSessions.getIp().length() != 0) {
            stringBuilder.append(getContext().getString(R.string.ip));
            stringBuilder.append(structSessions.getIp());
        }
        if (structSessions.getCountry().length() != 0) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append("— " + getContext().getString(R.string.country));
            stringBuilder.append(structSessions.getCountry());
        }
        detailExTextView.setText(stringBuilder);
        stringBuilder = new StringBuilder();
        stringBuilder.append(getContext().getString(R.string.create_time));
        stringBuilder.append(HelperCalander.checkHijriAndReturnTime(structSessions.getCreateTime()));
        stringBuilder.append(", " + getContext().getString(R.string.last_connectivity));
        stringBuilder.append(HelperCalander.checkHijriAndReturnTime(structSessions.getActiveTime()));
        detailTextView.setText(stringBuilder);
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
    }

    public void setTextColor(int color) {
        nameTextView.setTextColor(color);
        onlineTextView.setTextColor(color);
        detailTextView.setTextColor(color);
        detailExTextView.setTextColor(color);
        invalidate();
    }
}
