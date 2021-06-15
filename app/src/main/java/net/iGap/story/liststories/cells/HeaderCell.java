package net.iGap.story.liststories.cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;

public class HeaderCell extends AppCompatTextView {
    private final boolean isRtl = G.isAppRtl;

    public HeaderCell(Context context) {
        super(context);

        setPadding(LayoutCreator.dp(16), LayoutCreator.dp(4), LayoutCreator.dp(16), LayoutCreator.dp(4));
        setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT | Gravity.CENTER_VERTICAL);
        //setTextColor(Theme.getColor(Theme.GREY));
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(38));
    }
}
