package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.module.CircleImageView;
import net.iGap.module.Theme;
import net.iGap.realm.RealmRoom;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;

public class Toolbar extends FrameLayout {
    public static final int SEARCH_TAG = 1020;
    private TextView titleTextView;
    private TextView subTitleTextView;
    private ImageView backIcon;
    protected ToolbarListener listener;
    private ToolbarItems items;
    private ToolbarItems actionItems;
    private String actionModeTag;
    private boolean isSearchBoxVisible;
    private int actionModeColor;
    private int extraHeight;
    private boolean actionItemsVisible;
    private AnimatorSet actionModeAnimation;
    private boolean titleIsFontIcon;
    private CircleImageView circleImageView;
    private TextView muteIcon;
    private TextView verifiedChannelsIcon;

    public Toolbar(@NonNull Context context) {
        super(context);
        setBackgroundColor(Theme.getInstance().getToolbarBackgroundColor(context));
    }

    public void setTitle(CharSequence title) {
        if (titleTextView == null) {
            createTitleTextView();
        }
        titleIsFontIcon = false;
        titleTextView.setText(title);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        titleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
    }

    public AppCompatImageView addAvatar(RealmRoom item, AvatarHandler avatarHandler) {
        if (circleImageView == null) {
            circleImageView = new CircleImageView(getContext());
        }

        long idForGetAvatar;
        if (item.getType() == CHAT) {
            idForGetAvatar = item.getChatRoom().getPeerId();
        } else {
            idForGetAvatar = item.getId();
        }
        avatarHandler.getAvatar(new ParamWithInitBitmap(circleImageView, idForGetAvatar)
                .initBitmap(HelperImageBackColor.drawAlphabetOnPicture((int)
                        getContext().getResources().getDimension(R.dimen.dp40), item.getInitials(), item.getColor())));

        addView(circleImageView);
        return circleImageView;
    }

    public TextView addMuteIcon() {
        if (muteIcon == null)
            createMuteIcon();
        muteIcon.setVisibility(GONE);
        addView(muteIcon);
        return muteIcon;
    }

    private void createMuteIcon() {
        if (muteIcon != null)
            return;
        muteIcon = new TextView(getContext());
        muteIcon.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon));
        muteIcon.setText(R.string.mute_icon);
        muteIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        muteIcon.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
    }

    public TextView addVerifiedChannelsIcon() {
        if (verifiedChannelsIcon == null)
            createVerifiedIcon();
        addView(verifiedChannelsIcon);
        return verifiedChannelsIcon;
    }

    private void createVerifiedIcon() {
        verifiedChannelsIcon = new TextView(getContext());
        verifiedChannelsIcon.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon));
        verifiedChannelsIcon.setText(R.string.verify_icon);
        verifiedChannelsIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        verifiedChannelsIcon.setTextColor(getContext().getResources().getColor(R.color.verify_color));
    }

    public void setTitle(@StringRes int title) {
        if (titleTextView == null) {
            titleTextView = new TextView(getContext());
            titleTextView.setSingleLine(true);
            addView(titleTextView);
        }
        titleIsFontIcon = true;
        titleTextView.setGravity(Gravity.LEFT);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        titleTextView.setTextColor(0xffffffff);
        titleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon));
        titleTextView.setText(title);
    }

    public void setTitleSize(int size) {
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setSubTitle(String subTitle) {
        if (subTitleTextView == null) {
            createSubtitleTextView();
        }
        subTitleTextView.setText(subTitle);
        subTitleTextView.requestLayout();
    }

    public void setBackIcon(Drawable drawable) {
        if (backIcon == null) {
            createBackButtonImage();
        }
        backIcon.setImageDrawable(drawable);
        backIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(-1);
            }
            if (isSearchBoxVisible) {
                closeSearchBox();
            }
        });
        if (drawable instanceof BackDrawable) {
            BackDrawable backDrawable = (BackDrawable) drawable;
            backDrawable.setRotatedColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        }
    }

    public void setBackIcon(@StringRes int iconRes) {
        setBackIcon(getResources().getDrawable(iconRes));
    }

    public void setBackIconToNull() {
        backIcon = null;
    }

    public void setListener(ToolbarListener listener) {
        this.listener = listener;
    }

    public ToolbarItem addItem(int tag, int icon, @ColorInt int color) {
        return addItem(tag, null, icon, color);
    }

    public ToolbarItem addItem(int tag, String text, int icon, @ColorInt int color) {
        if (items == null) {
            items = new ToolbarItems(getContext(), this);
            addView(items, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        }

        return items.addItem(tag, text, icon, 54, color);
    }

    public void onSearchVisibilityChanged(boolean toggleSearch) {
        isSearchBoxVisible = toggleSearch;
        if (titleTextView != null && titleTextView.length() != 0) {
            titleTextView.setVisibility(toggleSearch ? INVISIBLE : VISIBLE);
        }

        if (subTitleTextView != null && subTitleTextView.length() != 0) {
            subTitleTextView.setVisibility(toggleSearch ? INVISIBLE : VISIBLE);
        }
    }

    public boolean isSearchFieldVisible() {
        return isSearchBoxVisible;
    }

    public TextView getVerifiedIcon() {
        return verifiedChannelsIcon;
    }

    public TextView getMuteIcon() {
        return muteIcon;
    }

    public interface ToolbarListener {
        void onItemClick(int i);
    }

    public ToolbarItems createToolbarItems() {
        if (items != null) {
            return items;
        }
        items = new ToolbarItems(getContext(), this);
        addView(items, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        return items;
    }

    private void createBackButtonImage() {
        if (backIcon != null) {
            return;
        }
        backIcon = new ImageView(getContext());
        backIcon.setScaleType(ImageView.ScaleType.CENTER);

        addView(backIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
    }

    private void closeSearchBox() {
        closeSearchBox(true);
    }

    public void closeSearchBox(boolean closeKeyboard) {
        if (!isSearchBoxVisible || items == null) {
            return;
        }
        items.closeSearchBox(closeKeyboard);
    }

    private void createTitleTextView() {
        if (titleTextView != null) {
            return;
        }

        titleTextView = new TextView(getContext());
        titleTextView.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        titleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        titleTextView.setGravity(Gravity.LEFT);
        titleTextView.setSingleLine();
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(titleTextView);
    }

    private void createSubtitleTextView() {
        if (subTitleTextView != null) {
            return;
        }

        subTitleTextView = new TextView(getContext());
        subTitleTextView.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        subTitleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        subTitleTextView.setGravity(Gravity.LEFT);
        subTitleTextView.setSingleLine();
        subTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(subTitleTextView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = getCurrentActionBarHeight();
        int actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, MeasureSpec.EXACTLY);
        int titleLeft = 0;

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), actionBarHeight);
        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(46), MeasureSpec.EXACTLY), backIcon.getMeasuredHeight());
            titleLeft += LayoutCreator.dp(54); // 46 icon width + 8 margin
        } else {
            titleLeft += LayoutCreator.dp(16);
        }

        if (circleImageView != null && circleImageView.getVisibility() != GONE) {
            circleImageView.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(46), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(46), MeasureSpec.EXACTLY));
            titleLeft = LayoutCreator.dp(58);
        }
        int menuWidth = 0;
        if (items != null && items.getVisibility() != GONE) {
            if (isSearchBoxVisible) {
                menuWidth = MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(64), MeasureSpec.EXACTLY);
            } else {
                menuWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            }
            items.measure(menuWidth, actionBarHeightSpec);
        }

        if (titleTextView != null && titleTextView.getVisibility() != GONE || subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
            int textWidth = width - (items != null ? items.getMeasuredWidth() : 0) - LayoutCreator.dp(16) - titleLeft;

            if (titleTextView != null && titleTextView.getVisibility() != GONE) {
                if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                    if (!titleIsFontIcon)
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                    subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                } else {
                    if (!titleIsFontIcon)
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                }
                titleTextView.measure(MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
            }
            if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                subTitleTextView.measure(MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
            }
            if (verifiedChannelsIcon != null && verifiedChannelsIcon.getVisibility() != GONE) {
                verifiedChannelsIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(12), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(12), MeasureSpec.EXACTLY));
            }
            if (muteIcon != null && muteIcon.getVisibility() != GONE) {
                muteIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(12), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(12), MeasureSpec.EXACTLY));
            }
        }

        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == backIcon || child == titleTextView || child == subTitleTextView || child == items || child == circleImageView || child == verifiedChannelsIcon || child == muteIcon) {
                continue;
            }
            measureChildWithMargins(child, menuWidth, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY), 0);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int titleLeft;
        int titleTop = 0;
        int avatarTop;
        int verifyLeft = 0;

        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.layout(0, 0, backIcon.getMeasuredWidth(), getMeasuredHeight());
            if (circleImageView != null)
                titleLeft = LayoutCreator.dp(46); // 46 icon width
            else
                titleLeft = LayoutCreator.dp(54); // 46 icon width + 8 margin
            if (backIcon.getDrawable() == null) {
                titleLeft = LayoutCreator.dp(16);
            }
        } else {
            titleLeft = LayoutCreator.dp(16);
        }

        if (circleImageView != null && circleImageView.getVisibility() != GONE) {
            avatarTop = getMeasuredHeight() / 2 - (circleImageView.getMeasuredHeight() / 2);
            circleImageView.layout(titleLeft, avatarTop, titleLeft + circleImageView.getMeasuredWidth(), circleImageView.getMeasuredWidth() + avatarTop);
            titleLeft += LayoutCreator.dp(54);// 46 icon width + 46 avatar width + 8 margin.
        }

        if (titleTextView != null && titleTextView.getVisibility() != GONE) {
            int titleTextHeight = titleTextView.getMeasuredHeight();

            if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                titleTop = (getMeasuredHeight() / 2 - titleTextHeight) / 2 + LayoutCreator.dp(5);
            } else {
                titleTop = (getMeasuredHeight() - titleTextHeight) / 2;
            }

            titleTextView.layout(titleLeft, titleTop, titleLeft + titleTextView.getMeasuredWidth(), titleTop + titleTextHeight);
            verifyLeft = titleLeft + titleTextView.getMeasuredWidth() + LayoutCreator.dp(5);
        }

        if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
            int textTop = getMeasuredHeight() / 2 + (getMeasuredHeight() / 2 - subTitleTextView.getMeasuredHeight()) / 2 - LayoutCreator.dp(4);
            subTitleTextView.layout(titleLeft, textTop, titleLeft + subTitleTextView.getMeasuredWidth(), textTop + subTitleTextView.getMeasuredHeight());
        }
        if (verifiedChannelsIcon != null && verifiedChannelsIcon.getVisibility() != GONE) {
            int verifyTop = titleTop + (titleTextView.getMeasuredHeight() / 2 - verifiedChannelsIcon.getMeasuredHeight() / 2);
            verifiedChannelsIcon.layout(verifyLeft, verifyTop, verifyLeft + verifiedChannelsIcon.getMeasuredWidth(), verifiedChannelsIcon.getMeasuredHeight() + verifyTop);
            verifyLeft += verifiedChannelsIcon.getMeasuredWidth() + LayoutCreator.dp(5);
        }

        if (muteIcon != null && muteIcon.getVisibility() != GONE) {
            int muteTop = titleTop + (titleTextView.getMeasuredHeight() / 2 - muteIcon.getMeasuredHeight() / 2);
            muteIcon.layout(verifyLeft, muteTop, verifyLeft + muteIcon.getMeasuredWidth(), muteIcon.getMeasuredHeight() + muteTop);
        }

        if (items != null) {
            int menuLeft = isSearchBoxVisible ? LayoutCreator.dp(64) : getMeasuredWidth() - items.getMeasuredWidth();
            items.layout(menuLeft, 0, menuLeft + items.getMeasuredWidth(), items.getMeasuredHeight());
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE || child == titleTextView || child == subTitleTextView || child == items || child == backIcon || child == circleImageView || child == verifiedChannelsIcon || child == muteIcon) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int childLeft;
            int childTop;

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = Gravity.TOP | Gravity.LEFT;
            }

            final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = (right - left - width) / 2 + lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = right - width - lp.rightMargin;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = lp.leftMargin;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = (bottom - top - height) / 2 + lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = (bottom - top) - height - lp.bottomMargin;
                    break;
                default:
                    childTop = lp.topMargin;
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }

    public boolean isInActionMode() {
        return isInActionMode(null);
    }

    public boolean isInActionMode(String tag) {
        return actionItems != null && (actionModeTag == null && tag == null) || (actionModeTag != null && actionModeTag.equals(tag));
    }

    public ImageView getBackIcon() {
        return backIcon;
    }

    public ToolbarItems createActionToolbar(String tag) {
        if (isInActionMode(tag)) {
            return actionItems;
        }
        if (actionItems != null) {
            removeView(actionItems);
            actionItems = null;
        }

        actionModeTag = tag;
        actionItems = new ToolbarItems(getContext(), this) {
            @Override
            public void setBackgroundColor(int color) {
                super.setBackgroundColor(actionModeColor = color);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    if (x < backIcon.getWidth()) {
                        return false;
                    }
                }
                return true;
            }
        };

        actionItems.isActionMode = true;
        actionItems.setClickable(true);
        actionItems.setBackgroundColor(Theme.getInstance().getToolbarBackgroundColor(getContext()));
        addView(actionItems, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        actionItems.setVisibility(INVISIBLE);

        return actionItems;
    }

    public boolean isActionModeShowed() {
        return actionItems != null && actionItemsVisible;
    }

    public static int getCurrentActionBarHeight() {
        if (G.twoPaneMode) {
            return LayoutCreator.dp(68);
        } else {
            return LayoutCreator.dp(60);
        }
    }

    public String getSubTitleText() {
        if (subTitleTextView != null)
            return subTitleTextView.getText().toString();
        return "";
    }

    public void showActionToolbar() {
        if (actionItems == null || actionItemsVisible) {
            return;
        }

        actionItemsVisible = true;

        if (actionModeAnimation != null) {
            actionModeAnimation.cancel();
        }
        actionModeAnimation = new AnimatorSet();
        actionModeAnimation.playTogether(
                ObjectAnimator.ofFloat(actionItems, View.ALPHA, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(items, View.ALPHA, 1.0f, 0.0f),
                ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(), Theme.getInstance().getToolbarBackgroundColor(getContext()), Theme.getInstance().getToolbarActionModeBackgroundColor(getContext()))
        );
        actionModeAnimation.setDuration(100);
        actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                actionItems.setVisibility(VISIBLE);

                if (titleTextView != null) {
                    titleTextView.setVisibility(INVISIBLE);
                }

                if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                    subTitleTextView.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                    if (titleTextView != null) {
                        titleTextView.setVisibility(INVISIBLE);
                    }

                    if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                        subTitleTextView.setVisibility(INVISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                }
            }
        });
        actionModeAnimation.start();

        if (backIcon != null) {
            Drawable drawable = backIcon.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(1, true);
            }
        }
    }

    public void hideActionToolbar() {
        if (actionItems == null || !actionItemsVisible) {
            return;
        }

        actionItemsVisible = false;
        if (actionModeAnimation != null) {
            actionModeAnimation.cancel();
        }

        actionModeAnimation = new AnimatorSet();
        actionModeAnimation.playTogether(
                ObjectAnimator.ofFloat(actionItems, View.ALPHA, 0.0f),
                ObjectAnimator.ofFloat(items, View.ALPHA, 1.0f),
                ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(), Theme.getInstance().getToolbarActionModeBackgroundColor(getContext()), Theme.getInstance().getToolbarBackgroundColor(getContext()))
        );
        actionModeAnimation.setDuration(100);
        actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                    actionItems.setVisibility(INVISIBLE);
                }

                if (!isSearchBoxVisible) {
                    if (titleTextView != null) {
                        titleTextView.setVisibility(VISIBLE);
                    }
                    if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                        subTitleTextView.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                }
            }
        });
        actionModeAnimation.start();

        if (backIcon != null) {
            Drawable drawable = backIcon.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(0, true);
            }
        }
    }

    public void setActionModeColor(int color) {
        if (actionItems != null) {
            actionItems.setBackgroundColor(color);
        }
    }

    public ToolbarItems getActionMode() {
        return actionItems;
    }

}
