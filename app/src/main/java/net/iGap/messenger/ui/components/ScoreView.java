package net.iGap.messenger.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class ScoreView extends LinearLayout {
    private final TextView tvRank;
    private final TextView tvDescription;
    private final TextView tvScore;
    private final TextView tvTotalRanks;
    private final Paint paint;

    public ScoreView(@NonNull Context context) {
        super(context);
        paint = new Paint();
        setOrientation(HORIZONTAL);
        GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Theme.getColor(Theme.key_popup_background), Theme.getColor(Theme.key_popup_background)});
        background.setCornerRadius(30F);
        setBackground(background);
        FrameLayout rightLayout = new FrameLayout(context);
        FrameLayout leftLayout = new FrameLayout(context);
        TextView tvRankTitle = new TextView(context);
        tvRankTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvRankTitle.setTextColor(Theme.getColor(Theme.key_default_text));
        tvRankTitle.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font_bold));
        tvRankTitle.setText(getResources().getString(R.string.rank));
        tvRank = new TextView(context);
        tvRank.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvRank.setTextColor(Theme.getColor(Theme.key_dark_theme_color));
        tvRank.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font));
        tvDescription = new TextView(context);
        tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvDescription.setTextColor(Theme.getColor(Theme.key_default_text));
        tvDescription.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font));
        TextView tvScoreTitle = new TextView(context);
        tvScoreTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvScoreTitle.setTextColor(Theme.getColor(Theme.key_default_text));
        tvScoreTitle.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font_bold));
        tvScoreTitle.setText(getResources().getString(R.string.score));
        tvScore = new TextView(context);
        tvScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvScore.setTextColor(Theme.getColor(Theme.key_dark_red));
        tvScore.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font));
        tvTotalRanks = new TextView(context);
        tvTotalRanks.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvTotalRanks.setTextColor(Theme.getColor(Theme.key_default_text));
        tvTotalRanks.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font));
        addView(rightLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 0.5F));
        addView(leftLayout, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, 0.5F));
        rightLayout.addView(tvRankTitle, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER, 0, 8, 0, 0));
        rightLayout.addView(tvRank, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
        rightLayout.addView(tvTotalRanks, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER, 0, 0, 0, 8));
        leftLayout.addView(tvScoreTitle, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER, 0, 8, 0, 0));
        leftLayout.addView(tvScore, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
        leftLayout.addView(tvDescription, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER, 0, 0, 0, 8));
    }

    public void setRankText(String rank) {
        tvRank.setText(rank);
    }

    public void setScoreText(String score) {
        tvScore.setText(score);
    }

    public void setTotalRanksText(String allRanks) {
        tvTotalRanks.setText(String.format("%s%s", getResources().getString(R.string.from), allRanks));
    }

    public void setDescriptionText(String some) {
        tvDescription.setText(some);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Theme.getColor(Theme.key_line));
        int width = getMeasuredWidth() / 2;
        canvas.drawRect(width - 2, 0, width + 2, getMeasuredHeight(), paint);
    }
}
