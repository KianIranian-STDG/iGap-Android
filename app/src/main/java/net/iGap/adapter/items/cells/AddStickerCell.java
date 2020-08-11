package net.iGap.adapter.items.cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.customView.ProgressButton;
import net.iGap.module.customView.StickerView;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;

public class AddStickerCell extends FrameLayout implements EventListener {

    private StickerView groupAvatarIv;
    private TextView groupNameTv;
    private TextView groupStickerCountTv;
    private ProgressButton button;
    private String stickerGroupId;
    private boolean isRtl = G.isAppRtl;

    public AddStickerCell(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);

        groupAvatarIv = new StickerView(getContext());
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

    public void setStickerGroupId(String stickreGroupId) {
        this.stickerGroupId = stickreGroupId;
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
        EventManager.getInstance().addEventListener(EventManager.STICKER_CHANGED, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventManager.getInstance().removeEventListener(EventManager.STICKER_CHANGED, this);
    }

    public void loadAvatar(StructIGStickerGroup stickerGroup) {
        groupAvatarIv.loadStickerGroup(stickerGroup);
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

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.STICKER_CHANGED) {
            String groupId = (String) message[0];
            boolean isInUserList = (boolean) message[1];

            if (groupId.equals(stickerGroupId))
                G.handler.post(() -> getButton().setMode(isInUserList ? 0 : 1));
        }
    }
}
