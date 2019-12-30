package net.iGap.adapter.items.cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.helper.LayoutCreator;
import net.iGap.view.ProgressButton;

public class AddAnimatedStickerCell extends FrameLayout {

    private EventListener eventListener;

    private LottieAnimationView groupAvatarIv;
    private TextView groupNameTv;
    private TextView groupStickerCountTv;
    private ProgressButton button;
    private boolean isRtl = G.isAppRtl;

    public AddAnimatedStickerCell(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);

        groupAvatarIv = new LottieAnimationView(getContext());
        groupAvatarIv.setRepeatCount(LottieDrawable.INFINITE);
        groupAvatarIv.setRepeatMode(LottieDrawable.REVERSE);
        groupAvatarIv.setFailureListener(result -> Log.e(getClass().getName(), "AddAnimatedStickerCell: ", result));
        addView(groupAvatarIv, LayoutCreator.createFrame(52, 52, (isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));

        groupNameTv = new TextView(getContext());
        groupNameTv.setTextColor(new Theme().getTitleTextColor(getContext()));
        groupNameTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        groupNameTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        groupNameTv.setLines(1);
        groupNameTv.setMaxLines(1);
        groupNameTv.setSingleLine(true);
        groupNameTv.setEllipsize(TextUtils.TruncateAt.END);
        groupNameTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(groupNameTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 120 : 78, 8, isRtl ? 78 : 120, 0));

        groupStickerCountTv = new TextView(getContext());
        groupStickerCountTv.setTextColor(new Theme().getTitleTextColor(getContext()));
        groupStickerCountTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        groupStickerCountTv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        groupStickerCountTv.setLines(1);
        groupStickerCountTv.setMaxLines(1);
        groupStickerCountTv.setSingleLine(true);
        groupStickerCountTv.setEllipsize(TextUtils.TruncateAt.END);
        groupStickerCountTv.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(groupStickerCountTv, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 78, 34, isRtl ? 78 : 0, 0));

        button = new ProgressButton(getContext());
        addView(button, LayoutCreator.createFrame(100, 40, (isRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, isRtl ? 12 : 0, 0, isRtl ? 0 : 12, 0));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dpToPx(61), MeasureSpec.EXACTLY));
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(new Theme().getDividerColor(getContext()));
        canvas.drawLine(isRtl ? 0 : LayoutCreator.dpToPx(62), getHeight() - 1, isRtl ? getWidth() - LayoutCreator.dpToPx(62) : getWidth() - getPaddingRight(), getHeight() - 1, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        groupAvatarIv.playAnimation();

        if (eventListener != null)
            EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, eventListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (eventListener != null)
            EventManager.getInstance().removeEventListener(EventManager.STICKER_DOWNLOAD, eventListener);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public LottieAnimationView getGroupAvatarIv() {
        return groupAvatarIv;
    }

    public TextView getGroupNameTv() {
        return groupNameTv;
    }

    public TextView getGroupStickerCountTv() {
        return groupStickerCountTv;
    }

    public ProgressButton getButton() {
        return button;
    }
}
