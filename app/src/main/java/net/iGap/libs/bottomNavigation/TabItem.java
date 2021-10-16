package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.module.CircleImageView;
import net.iGap.module.customView.TextBadge;

import static android.view.View.MeasureSpec.AT_MOST;

public class TabItem extends LinearLayout implements View.OnClickListener {

    private BottomNavigation bottomNavigation;
    private OnItemSelected onTabItemSelected;

    private int selectedIcon;
    private int unSelectedIcon;
    private int darkSelectedIcon;
    private int darkUnSelectedIcon;
    private int position;
    private int text;
    public boolean haveAvatarImage;
    public boolean hasUnreadBadge;
    private ImageView imageView;
    private TextBadge badgeView;
    private AppCompatTextView textView;
    private AvatarHandler avatarHandler;

    private boolean active = false;
    private boolean isRtl = G.isAppRtl;
    private boolean isDarkTheme = G.themeColor == Theme.DARK;


    public TabItem(Context context) {
        super(context);
        init(null);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        parseAttr(attributeSet);

        if (haveAvatarImage) {
            if (imageView == null) {
                imageView = new CircleImageView(getContext());
                imageView.setBackgroundResource(new Theme().getUserProfileTabSelector(getContext()));
                imageView.setPadding((int) getResources().getDimension(R.dimen.dp2), (int) getResources().getDimension(R.dimen.dp2), (int) getResources().getDimension(R.dimen.dp2), (int) getResources().getDimension(R.dimen.dp2));
                if (avatarHandler == null) {
                    avatarHandler = new AvatarHandler();
                    avatarHandler.registerChangeFromOtherAvatarHandler();
                    avatarHandler.getAvatar(new ParamWithAvatarType(imageView, AccountManager.getInstance().getCurrentUser().getId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
                }
            }
        } else {
            if (imageView == null)
                imageView = new AppCompatImageView(getContext());

        }

        if (textView == null)
            textView = new AppCompatTextView(getContext());

        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        textView.setText(text);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected}, // selected
                new int[]{-android.R.attr.state_selected}, // none
        };

        int[] colors = new int[]{
                new Theme().getAccentColor(textView.getContext()),
                new Theme().getSubTitleColor(textView.getContext())
        };

        ColorStateList myList = new ColorStateList(states, colors);
        textView.setTextColor(myList);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);

        addView(imageView);
        addView(textView);

        setOnClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;

        int viewWidth = LayoutCreator.dpToPx(position == BottomNavigationFragment.DISCOVERY_FRAGMENT ? 32 : 26);
        imageView.measure(LayoutCreator.manageSpec(viewWidth, MeasureSpec.EXACTLY), LayoutCreator.manageSpec(viewWidth, MeasureSpec.EXACTLY));
        int viewLeft = (w - viewWidth) / 2;
        int viewTop = position == BottomNavigationFragment.DISCOVERY_FRAGMENT ? LayoutCreator.dpToPx(2) : (h - viewWidth) / 4;
        int viewHeight;
        imageView.layout(viewLeft, viewTop, w - viewLeft, viewTop + viewWidth);

        viewWidth = LayoutCreator.getTextWidth(textView);
        viewHeight = LayoutCreator.getTextHeight(textView);
        textView.measure(LayoutCreator.manageSpec(viewWidth, MeasureSpec.EXACTLY), LayoutCreator.manageSpec(viewHeight, MeasureSpec.EXACTLY));
        viewLeft = (w - viewWidth) / 2;
        viewTop = imageView.getBottom() + (position == BottomNavigationFragment.DISCOVERY_FRAGMENT ? LayoutCreator.dpToPx(1) : LayoutCreator.dpToPx(2));
        textView.layout(viewLeft, viewTop, w - viewLeft, viewTop + viewHeight);

        if (badgeView != null) {
            viewHeight = LayoutCreator.getTextHeight(badgeView.getTextView());
            viewWidth = LayoutCreator.getTextWidth(badgeView.getTextView());

            viewLeft = imageView.getRight() - LayoutCreator.dpToPx(8);
            viewTop = LayoutCreator.dpToPx(2);

            badgeView.measure(LayoutCreator.manageSpec(viewWidth, AT_MOST), LayoutCreator.manageSpec(viewHeight, AT_MOST));
            badgeView.layout(viewLeft, viewTop, imageView.getRight() + viewWidth - LayoutCreator.dpToPx(2), imageView.getTop() + LayoutCreator.dpToPx(10));
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkParent();
    }

    private void checkParent() {
        post(() -> {
            if (getParent() instanceof BottomNavigation) {
                bottomNavigation = (BottomNavigation) getParent();
                setupViews();
            }

        });
    }


    private void setupViews() {
        if (!haveAvatarImage) {
            if (isDarkTheme) {
                imageView.setImageResource(darkSelectedIcon);
            } else {
                imageView.setImageResource(selectedIcon);
            }
        }
        if (position == bottomNavigation.getDefaultItem())
            active = true;
        setSelectedItem(active);

    }

    private void parseAttr(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TabItem);

            try {
                selectedIcon = typedArray.getResourceId(R.styleable.TabItem_selected_icon, -1);
                unSelectedIcon = typedArray.getResourceId(R.styleable.TabItem_unselected_icon, -1);
                darkSelectedIcon = typedArray.getResourceId(R.styleable.TabItem_dark_selected_icon, -1);
                darkUnSelectedIcon = typedArray.getResourceId(R.styleable.TabItem_dark_unselected_icon, -1);
                text = typedArray.getResourceId(R.styleable.TabItem_item_text, R.string.error);
                haveAvatarImage = typedArray.getBoolean(R.styleable.TabItem_haveAvatarImage, false);
                hasUnreadBadge = typedArray.getBoolean(R.styleable.TabItem_hasUnreadBadge, false);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;

        if (hasUnreadBadge) {
            badgeView = new TextBadge(getContext());
            postDelayed(() -> addView(badgeView), 150);
        }
    }

    @Override
    public void onClick(View v) {
        if (onTabItemSelected != null)
            onTabItemSelected.selectedTabItem(position);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (haveAvatarImage && avatarHandler == null) {
            avatarHandler = new AvatarHandler();
            avatarHandler.registerChangeFromOtherAvatarHandler();
            avatarHandler.getAvatar(new ParamWithAvatarType(imageView, AccountManager.getInstance().getCurrentUser().getId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (avatarHandler != null) {
            avatarHandler.unregisterChangeFromOtherAvatarHandler();
        }
    }

    public void setSelectedItem(boolean isActive) {
        if (active != isActive) {
            active = isActive;
        }
        this.setSelected(isActive);
        /*textView.setSelected(isActive);*/
        /*imageView.setSelected(isActive);*/
        if (!haveAvatarImage) {
            ContextThemeWrapper wrapper = new ContextThemeWrapper(getContext(), new Theme().getTheme(getContext()));
            if (isDarkTheme) {
                if (active) {
                    imageView.setImageDrawable(VectorDrawableCompat.create(getResources(), darkSelectedIcon, wrapper.getTheme()));
                } else {
                    imageView.setImageDrawable(VectorDrawableCompat.create(getResources(), darkUnSelectedIcon, wrapper.getTheme()));
                }
            } else {
                if (active) {
                    imageView.setImageDrawable(VectorDrawableCompat.create(getResources(), selectedIcon, wrapper.getTheme()));
                } else {
                    imageView.setImageDrawable(VectorDrawableCompat.create(getResources(), unSelectedIcon, wrapper.getTheme()));
                }
            }
        }

    }

    public void setOnTabItemSelected(OnItemSelected onItemSelected) {
        this.onTabItemSelected = onItemSelected;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private String getUnreadCount(int unreadCount) {
        if (unreadCount > 99) {
            if (isRtl)
                return HelperCalander.convertToUnicodeFarsiNumber("99+");
            else
                return "+99";
        } else {
            String s = String.valueOf(unreadCount);
            if (isRtl)
                return HelperCalander.convertToUnicodeFarsiNumber(s);
            else
                return s;

        }
    }

    public boolean isActive() {
        return active;
    }

    public void updateBadge(int unreadTotal, boolean isNeedText) {
        if (badgeView != null) {
            badgeView.setBadgeColor(getContext().getResources().getColor(R.color.red));
            badgeView.setText(getUnreadCount(unreadTotal));
            badgeView.getTextView().setTextSize(9);
            badgeView.getTextView().setSingleLine(true);
            if (unreadTotal == 0) {
                badgeView.setVisibility(GONE);
            } else
                badgeView.setVisibility(VISIBLE);
        }
    }
}
