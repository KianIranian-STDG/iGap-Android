package net.iGap.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.LayoutCreator;

public class TextBadge extends AppCompatTextView {

    private int color;

    public TextBadge(Context context) {
        super(context);
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setGravity(Gravity.CENTER);
        ViewMaker.setTextSize(this, R.dimen.dp10);
        setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
    }

    public void setColor(int color) {
        this.color = color;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();
        paint.setColor(color);
        path.addRoundRect(new RectF(-LayoutCreator.dp(4), LayoutCreator.dp(3), getMeasuredWidth() + LayoutCreator.dp(4),
                getMeasuredHeight() - LayoutCreator.dp(3)), 25, 25, Path.Direction.CCW);
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }
}
