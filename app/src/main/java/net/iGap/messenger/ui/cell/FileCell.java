package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.IconView;

public class FileCell extends FrameLayout {

    private final ImageView imageView;
    private final TextView fileName;
    private final TextView creationDate;
    private final IconView iconView;

    private final boolean isRtl = G.isAppRtl;

    public FileCell(Context context) {
        super(context);

        setWillNotDraw(false);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawable_rounded_corners_stroke));
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        addView(imageView);

        fileName = new TextView(context);
        fileName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        fileName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        fileName.setText("FileName.pdf");
        fileName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        fileName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        fileName.setSingleLine();
        addView(fileName);

        creationDate = new TextView(context);
        creationDate.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        creationDate.setSingleLine();
        creationDate.setText("314.7KB,07.09.19 at 6:29 PM");
        creationDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        creationDate.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        creationDate.setTextColor(Theme.getColor(Theme.key_title_text));
        addView(creationDate);

        iconView = new IconView(context);
        iconView.setText(getResources().getString(R.string.ic_download));
        iconView.setTextColor(Theme.getColor(Theme.key_red));
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        iconView.setGravity(isRtl ? Gravity.LEFT : Gravity.RIGHT);
        iconView.setSingleLine();
        iconView.setTypeface(ResourcesCompat.getFont(context, R.font.font_icon_new));
        addView(iconView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(40), MeasureSpec.EXACTLY));
        fileName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        creationDate.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - iconView.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) / 2 - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        iconView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(14), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(14), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(52));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int imageViewTop = (getMeasuredHeight() - imageView.getMeasuredHeight()) / 2;
        int imageViewBottom = imageViewTop + imageView.getMeasuredHeight();
        int imageViewLeft = isRtl ? getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int imageViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + imageView.getMeasuredWidth();

        imageView.layout(imageViewLeft, imageViewTop, imageViewRight, imageViewBottom);

        int fileNameTop = LayoutCreator.dp(8);
        int fileNameBottom = fileNameTop + fileName.getMeasuredHeight();
        int fileNameLeft = isRtl ? imageViewLeft - fileName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int fileNameRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : fileNameLeft + fileName.getMeasuredWidth();
        fileName.layout(fileNameLeft, fileNameTop, fileNameRight, fileNameBottom);

        int iconViewBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int iconViewTop = iconViewBottom - iconView.getMeasuredHeight();
        int iconViewLeft = isRtl ? imageViewLeft - iconView.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int iconViewRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : iconViewLeft + iconView.getMeasuredWidth();
        iconView.layout(iconViewLeft, iconViewTop, iconViewRight, iconViewBottom);

        int creationDateBottom = getMeasuredHeight() - LayoutCreator.dp(8);
        int creationDateTop = creationDateBottom - creationDate.getMeasuredHeight();
        int creationDateLeft = isRtl ? iconViewLeft - creationDate.getMeasuredWidth() - LayoutCreator.dp(8) : iconViewRight + LayoutCreator.dp(8);
        int creationDateRight = isRtl ? iconViewLeft - LayoutCreator.dp(8) : creationDateLeft + creationDate.getMeasuredWidth();
        creationDate.layout(creationDateLeft, creationDateTop, creationDateRight, creationDateBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(),Theme.dividerPaint);
        super.onDraw(canvas);
    }

    public void setTextColor(int color) {
        fileName.setTextColor(color);
        creationDate.setTextColor(color);
        invalidate();
    }
}