package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.CombinedDrawable;

public class ShadowSectionCell extends View {

    private int size;
    private Context context;

    public ShadowSectionCell(Context context) {
        this(context, 12);
    }

    public ShadowSectionCell(Context context, int s) {
        super(context);
        this.context = context;
        setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_line));
        size = s;
    }

    public ShadowSectionCell(Context context, int s, int backgroundColor) {
        super(context);
        this.context = context;
        setColors(backgroundColor);
        size = s;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(size), MeasureSpec.EXACTLY));
    }

    public void setColors(int color) {
        Drawable shadowDrawable = Theme.getThemedDrawable(context, R.drawable.greydivider, Theme.key_line);
        Drawable background = new ColorDrawable(color);
        CombinedDrawable combinedDrawable = new CombinedDrawable(background, shadowDrawable, 0, 0);
        combinedDrawable.setFullsize(true);
        setBackground(combinedDrawable);
        invalidate();
    }
}
