package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.CircleImageView;

public class SearchRoomCell extends FrameLayout {
    private final CircleImageView imageView;
    private final TextView roomName;
    private final boolean isRtl = G.isAppRtl;
    private int id;

    public SearchRoomCell(Context context) {
        super(context);

        setBackground(getResources().getDrawable(R.drawable.drawable_rounded_corners));
        imageView = new CircleImageView(context);
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));
        addView(imageView);
        roomName = new TextView(context);
        roomName.setGravity(isRtl ? Gravity.RIGHT | Gravity.CENTER_VERTICAL : Gravity.LEFT | Gravity.CENTER_VERTICAL);
        roomName.setPadding(0, 0, 0, LayoutCreator.dp(1));
        roomName.setText("Room Name");
        roomName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        roomName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        roomName.setSingleLine();
        roomName.setEllipsize(TextUtils.TruncateAt.END);
        addView(roomName);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(32), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(32), MeasureSpec.EXACTLY));
        roomName.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) / 3 - imageView.getMeasuredWidth() - LayoutCreator.dp(8)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(32), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec) / 3, LayoutCreator.dp(32));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int imageViewLeft = isRtl ? getMeasuredWidth() - imageView.getMeasuredWidth() : 0;
        int imageViewRight = isRtl ? getMeasuredWidth() : imageView.getMeasuredWidth();
        imageView.layout(imageViewLeft, 0, imageViewRight, getMeasuredHeight());

        int roomNameTop = (getMeasuredHeight() - roomName.getMeasuredHeight()) / 2;
        int roomNameBottom = roomNameTop + roomName.getMeasuredHeight();
        int roomNameLeft = isRtl ? imageViewLeft - roomName.getMeasuredWidth() - LayoutCreator.dp(8) : imageViewRight + LayoutCreator.dp(8);
        int roomNameRight = isRtl ? imageViewLeft - LayoutCreator.dp(8) : roomNameLeft + roomName.getMeasuredWidth();
        roomName.layout(roomNameLeft, roomNameTop, roomNameRight, roomNameBottom);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setTextColor(int color) {
        roomName.setTextColor(color);
        invalidate();
    }
}
