package net.iGap.module.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class ProgressButton extends FrameLayout {
    private TextView buttonTv;
    private ProgressBar progressBar;

    private int radius = LayoutCreator.dpToPx(32);
    private Paint paint;
    private int mode;


    public ProgressButton(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        progressBar = new ProgressBar(getContext());
        progressBar.getIndeterminateDrawable().setColorFilter(Theme.getColor(Theme.key_white), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(GONE);
        addView(progressBar, LayoutCreator.createFrame(30, 30, Gravity.CENTER));


        buttonTv = new TextView(getContext());
        buttonTv.setTextColor(Theme.getColor(Theme.key_title_text));
        buttonTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        buttonTv.setLines(1);
        buttonTv.setTextColor(Theme.getColor(Theme.key_white));
        buttonTv.setMaxLines(1);
        buttonTv.setSingleLine(true);
        buttonTv.setEllipsize(TextUtils.TruncateAt.END);
        boolean isRtl = G.isAppRtl;
        buttonTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);

        addView(buttonTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 6, 0, 6, 0));
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
    }

    public void changeProgressTo(int visibility) {
        progressBar.setVisibility(visibility);

        if (visibility == VISIBLE) {
            buttonTv.setText("");
            setEnabled(false);
        } else {
            setEnabled(true);
        }

    }

    public void setBackgroundColor(@ColorRes int backgroundColor) {
        if (paint != null) paint.setColor(backgroundColor);
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = LayoutCreator.dpToPx(radius);
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == 0) {
            buttonTv.setText(getResources().getString(R.string.remove));
            setBackgroundColor(Theme.getColor(Theme.key_red));
        } else {
            buttonTv.setText(getResources().getString(R.string.Add));
            setBackgroundColor(Theme.getColor(Theme.key_button_background));
        }
    }

    public void setText(@StringRes int text) {
        buttonTv.setText(text);
    }

    public void setText(CharSequence text) {
        buttonTv.setText(text);
    }

    public int getMode() {
        return mode;
    }
}
