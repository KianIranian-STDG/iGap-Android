package net.iGap.module.customView;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class InlineButton extends FrameLayout {
    private ProgressBar progressBar;
    private TextView textView;
    private boolean progressIsVisible = false;

    public InlineButton(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.background_bot_inline);
        progressBar = new ProgressBar(getContext());
        textView = new TextView(getContext());

        setPadding(LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2), LayoutCreator.dp(2));

        textView.setTextColor(Theme.getColor(Theme.key_white));
        setTextSize(textView, R.dimen.standardTextSize);
        textView.setAllCaps(false);
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext() , R.font.main_font));
        textView.setText("دکمه ی شیشه ای");
        textView.setGravity(Gravity.CENTER);

        progressBar.getIndeterminateDrawable().setColorFilter(Theme.getColor(Theme.key_white), PorterDuff.Mode.SRC_IN);

//        progressBar.setVisibility(GONE);
        addView(progressBar, LayoutCreator.createFrame(15, 15, Gravity.TOP | Gravity.RIGHT, 0, 4, 4, 0));
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 35, Gravity.CENTER));

    }

    public void setProgressVisibility(int visibility) {
        progressIsVisible = visibility == VISIBLE;
        progressBar.setVisibility(visibility);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    protected void setTextSize(TextView v, int sizeSrc) {
        ViewMaker.setTextSize(v, sizeSrc);
    }

    public boolean isProgressIsVisible() {
        return progressIsVisible;
    }

    public void setProgressIsVisible(boolean progressIsVisible) {
        this.progressIsVisible = progressIsVisible;
    }
}
