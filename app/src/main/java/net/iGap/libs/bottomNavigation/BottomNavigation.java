package net.iGap.libs.bottomNavigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.libs.bottomNavigation.Event.OnItemChangeListener;
import net.iGap.libs.bottomNavigation.Event.OnItemSelected;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.AppUtils;

public class BottomNavigation extends LinearLayout implements OnItemSelected, View.OnClickListener {

    public static final String TAG = "aabolfazlNavigation";
    private OnItemChangeListener onItemChangeListener;
    private int defaultItem;
    private int selectedItemPosition = defaultItem;
    private OnLongClickListener onLongClickListener;

    public BottomNavigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Theme.getColor(Theme.key_background_footer));
        setMinimumHeight(Utils.dpToPx(50));
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupChildren();
    }

    private void setupChildren() {
        try {
            for (int i = 0; i < getChildCount(); i++) {
                TabItem tabItem = (TabItem) getChildAt(i);
                tabItem.setPosition(i);
                tabItem.setOnTabItemSelected(this);
                if (tabItem.haveAvatarImage) {
                    tabItem.setOnLongClickListener(this::onLongClick);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void selectedTabItem(final int position) {
        if (position != selectedItemPosition) {
            selectedItemPosition = position;
            onSelectedItemChanged();
            if (onItemChangeListener != null) {
                onItemChangeListener.onSelectedItemChanged(((TabItem) getChildAt(position)).getPosition());
            }
        } else {
            onItemChangeListener.onSelectAgain(((TabItem) getChildAt(position)).getPosition());
        }
    }

    public void setCurrentItem(int position) {
        defaultItem = position;
        for (int i = 0; i < getChildCount(); i++) {
            if (((TabItem) getChildAt(i)).getPosition() == position) {
                if (position != selectedItemPosition) {
                    selectedItemPosition = position;
                    onSelectedItemChanged();
                    if (onItemChangeListener != null) {
                        onItemChangeListener.onSelectedItemChanged(((TabItem) getChildAt(i)).getPosition());
                    }
                }
            }
        }
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    private void onSelectedItemChanged() {
        try {
            for (int i = 0; i < getChildCount(); i++) {
                ((TabItem) getChildAt(i)).setSelectedItem(((TabItem) getChildAt(i)).getPosition() == selectedItemPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDefaultItem() {
        return defaultItem;
    }

    public void setDefaultItem(int defaultItem) {
        this.defaultItem = defaultItem;
        this.selectedItemPosition = defaultItem;
    }

    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        onItemChangeListener.onSelectedItemChanged(defaultItem);
        this.onItemChangeListener = onItemChangeListener;
    }

    public void setOnBottomNavigationBadge(int unreadCount, boolean isForStoryFragment) {

        for (int i = 0; i < getChildCount(); i++) {
            TabItem tabItem;
            if (isForStoryFragment) {
                tabItem = (TabItem) getChildAt(BottomNavigationFragment.STORY_FRAGMENT);
                tabItem.updateBadge(unreadCount, false);
            } else {
                if (i != BottomNavigationFragment.STORY_FRAGMENT) {
                    tabItem = (TabItem) getChildAt(i);
                    tabItem.updateBadge(unreadCount, true);
                }
            }


        }
    }

    public int getCurrentTab() {
        return selectedItemPosition;
    }

    @Override
    public void onClick(View v) {
        if (selectedItemPosition != BottomNavigationFragment.PROFILE_FRAGMENT) {
            v.setSelected(!v.isSelected());
        }
        selectedTabItem(BottomNavigationFragment.PROFILE_FRAGMENT);
    }

    public boolean onLongClick(View v) {
        if (onItemChangeListener != null) {
            onLongClickListener.onLongClick(v);
            AppUtils.setVibrator(15);
        }
        return true;
    }

    public void setProfileOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

}
