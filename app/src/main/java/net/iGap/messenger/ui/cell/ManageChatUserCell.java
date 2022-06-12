package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.realm.RealmRegisteredInfo;

import static net.iGap.G.context;

public class ManageChatUserCell extends FrameLayout {

    private CircleImageView avatarImageView;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;
    private ImageView optionsButton;
    private ImageView customImageView;

    private int statusOnlineColor;
    private boolean needDivider;
    private int namePadding;
    private RealmRegisteredInfo realmRegisteredInfo;

    private ManageChatUserCellDelegate delegate;

    public void setTextColor(int color) {
        nameTextView.setTextColor(color);
        statusTextView.setTextColor(color);
        invalidate();
    }

    public interface ManageChatUserCellDelegate {
        boolean onOptionsButtonCheck(ManageChatUserCell cell, boolean click);
    }

    public ManageChatUserCell(Context context,int avatarPadding, int nPadding, boolean needOption) {
        super(context);
        statusOnlineColor = Theme.getColor(Theme.key_title_text);
        namePadding = nPadding;

        avatarImageView = new CircleImageView(context);
        addView(avatarImageView, LayoutCreator.createFrame(46, 46, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 0 : 7 + avatarPadding, 8, G.isAppRtl ? 7 + avatarPadding : 0, 0));

        nameTextView = new SimpleTextView(context);
        nameTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        nameTextView.setTextSize(17);
        nameTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        nameTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 28 + 18 : (68 + namePadding), 11.5f, G.isAppRtl ? (68 + namePadding) : 28 + 18, 0));

        statusTextView = new SimpleTextView(context);
        statusTextView.setTextSize(14);
        statusTextView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        statusTextView.setTextColor(statusOnlineColor);
        statusTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(statusTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 28 : (68 + namePadding), 34.5f, G.isAppRtl ? (68 + namePadding) : 28, 0));

        if (needOption) {
            optionsButton = new ImageView(context);
            optionsButton.setFocusable(false);
            optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_black)));
            optionsButton.setImageResource(R.drawable.ic_ab_other);
            optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_line), PorterDuff.Mode.MULTIPLY));
            optionsButton.setScaleType(ImageView.ScaleType.CENTER);
            addView(optionsButton, LayoutCreator.createFrame(60, 64, (G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP));
            optionsButton.setOnClickListener(v -> delegate.onOptionsButtonCheck(ManageChatUserCell.this, true));
            optionsButton.setContentDescription(getContext().getString(R.string.AccDescrUserOption));
        }
    }

    public void setCustomImageVisible(boolean visible) {
        if (customImageView == null) {
            return;
        }
        customImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setData(RealmRegisteredInfo realmRegisteredInfo,boolean Divider){
        this.realmRegisteredInfo = realmRegisteredInfo;
        needDivider = Divider;
        nameTextView.setText(realmRegisteredInfo.getDisplayName());
        String status = LastSeenTimeUtil.computeTime(context, realmRegisteredInfo.getId(), realmRegisteredInfo.getLastSeen(), false);
        statusTextView.setText(status);
        if (optionsButton != null) {
            optionsButton.setVisibility(VISIBLE);
            nameTextView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 46 : (68 + namePadding), status == null || status.length() > 0 ? 11.5f : 20.5f, G.isAppRtl ? (68 + namePadding) : 46 , 0));
            statusTextView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 46 : (68 + namePadding), 34.5f, G.isAppRtl ? (68 + namePadding) : 46 , 0));
        } else if (customImageView != null) {
            boolean visible = customImageView.getVisibility() == VISIBLE;
            nameTextView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? (visible ? 54 : 28) : (68 + namePadding), status == null || status.length() > 0 ? 11.5f : 20.5f, G.isAppRtl ? (68 + namePadding) : (visible ? 54 : 28), 0));
            statusTextView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 20, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? (visible ? 54 : 28) : (68 + namePadding), 34.5f, G.isAppRtl ? (68 + namePadding) : (visible ? 54 : 28), 0));
        }
        setWillNotDraw(!needDivider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public CircleImageView getAvatarImageView(){
         return avatarImageView;
    }


    public void setDelegate(ManageChatUserCellDelegate manageChatUserCellDelegate) {
        delegate = manageChatUserCellDelegate;
    }

    public RealmRegisteredInfo getRealmRegisteredInfo(){
        return realmRegisteredInfo;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider && Theme.dividerPaint != null) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(68), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(68) : 0), getMeasuredHeight() - 1,Theme.dividerPaint);
        }
    }
}