package net.iGap.view;

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
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.helper.LayoutCreator;

public class ProgressButton extends FrameLayout {
    private TextView buttonTv;
    private ProgressBar progressBar;

    private int radius = LayoutCreator.dpToPx(32);
    private Paint paint;
    private boolean isRtl = G.isAppRtl;
    private int mode;


    public ProgressButton(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        progressBar = new ProgressBar(getContext());
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(GONE);
        addView(progressBar, LayoutCreator.createFrame(30, 30, Gravity.CENTER));


        buttonTv = new TextView(getContext());
        buttonTv.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
        buttonTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        buttonTv.setLines(1);
        buttonTv.setTextColor(getResources().getColor(R.color.white));
        buttonTv.setMaxLines(1);
        buttonTv.setSingleLine(true);
        buttonTv.setEllipsize(TextUtils.TruncateAt.END);
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

        if (visibility == VISIBLE)
            buttonTv.setText("");

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
            setBackgroundColor(getContext().getResources().getColor(R.color.red));
        } else {
            buttonTv.setText(getResources().getString(R.string.Add));
            setBackgroundColor(Theme.getInstance().getButtonColor(getContext()));
        }
    }

    public int getMode() {
        return mode;
    }
}