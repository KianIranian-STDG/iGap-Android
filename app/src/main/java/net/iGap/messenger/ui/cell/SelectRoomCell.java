package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.module.customView.CheckBox;

public class SelectRoomCell extends FrameLayout {
    private final CircleImageView imageView;
    private final TextView roomName;
    private final CheckBox checkBox;
    private final boolean isRtl = G.isAppRtl;

    public SelectRoomCell(Context context) {
        super(context);
        setWillNotDraw(false);
        imageView = new CircleImageView(context);
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
        addView(imageView);

        checkBox = new CheckBox(getContext(), R.drawable.round_check);
        checkBox.setVisibility(VISIBLE);
        addView(checkBox);

        roomName = new TextView(context);
        roomName.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        roomName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        roomName.setText("Room Name");
        roomName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        roomName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        roomName.setSingleLine();
        addView(roomName);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(48), MeasureSpec.EXACTLY));
        checkBox.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY));
        roomName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - imageView.getMeasuredWidth() - checkBox.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) - LayoutCreator.dp(8), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(52));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int imageViewTop = (getMeasuredHeight() - imageView.getMeasuredHeight()) / 2;
        int imageViewBottom = imageViewTop + imageView.getMeasuredHeight();
        int imageViewLeft = isRtl ? getMeasuredWidth() - imageView.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int imageViewRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + imageView.getMeasuredWidth();
        imageView.layout(imageViewLeft, imageViewTop, imageViewRight, imageViewBottom);

        checkBox.layout(isRtl ? imageViewLeft : imageViewRight - LayoutCreator.dp(20), imageViewBottom - LayoutCreator.dp(20), isRtl ? imageViewLeft + LayoutCreator.dp(20) : imageViewRight, imageViewBottom);

        int roomNameTop = LayoutCreator.dp(8);
        int roomNameBottom = roomNameTop + roomName.getMeasuredHeight();
        int roomNameLeft = isRtl ? imageViewLeft - roomName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int roomNameRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : roomNameLeft + roomName.getMeasuredWidth();
        roomName.layout(roomNameLeft, roomNameTop, roomNameRight, roomNameBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(isRtl ? 4 : imageView.getRight(), getMeasuredHeight() - 1, isRtl ? imageView.getLeft() : getWidth(), getMeasuredHeight(), Theme.dividerPaint);
        super.onDraw(canvas);
    }

    public void setText(String text) {
        roomName.setText(text);
    }

    public void setCheck() {
        checkBox.setChecked(!checkBox.isChecked(), true);
    }

    public void setTextColor(int color) {
        roomName.setTextColor(color);
        invalidate();
    }
}
