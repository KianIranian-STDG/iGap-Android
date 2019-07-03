package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.ViewPagerAdapter;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.realm.RealmRoom;

public class BottomNavigationFragment extends Fragment implements OnUnreadChange {

    //Todo: create viewModel for this it was test class and become main class :D
    private ViewPager mViewPager;
    private BottomNavigation bottomNavigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.onUnreadChange = this;
        return inflater.inflate(R.layout.fragment_bottom_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = view.findViewById(R.id.viewpager);
        bottomNavigation = view.findViewById(R.id.bn_main_bottomNavigation);

        Log.wtf(this.getClass().getName(), "initTabStrip");
        initTabStrip();
        Log.wtf(this.getClass().getName(), "initTabStrip");
    }

    @Override
    public void onResume() {
        Log.wtf(this.getClass().getName(), "onResume");
        super.onResume();
        Log.wtf(this.getClass().getName(), "onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        G.onUnreadChange = null;
    }

    @Override
    public void onChange() {

        int unReadCount = RealmRoom.getAllUnreadCount();

        bottomNavigation.setOnBottomNavigationBadge(new OnBottomNavigationBadge() {
            @Override
            public int callCount() {
                return 0;
            }

            @Override
            public int messageCount() {
                return unReadCount;
            }

            @Override
            public int badgeColor() {
                return getResources().getColor(R.color.red);
            }
        });

    }

    private void initTabStrip() {

        boolean isRtl = HelperCalander.isPersianUnicode;
        bottomNavigation.setDefaultItem(2);

        bottomNavigation.setOnItemChangeListener(i -> {
            if (isRtl) {
                if (i == 4)
                    mViewPager.setCurrentItem(0, false);
                if (i == 3)
                    mViewPager.setCurrentItem(1, false);
                if (i == 2)
                    mViewPager.setCurrentItem(2, false);
                if (i == 1)
                    mViewPager.setCurrentItem(3, false);
                if (i == 0)
                    mViewPager.setCurrentItem(4, false);
            } else {
                mViewPager.setCurrentItem(i, false);
            }
        });


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (isRtl) {
                    if (i == 4)
                        bottomNavigation.setCurrentItem(0);
                    if (i == 3)
                        bottomNavigation.setCurrentItem(1);
                    if (i == 2)
                        bottomNavigation.setCurrentItem(2);
                    if (i == 1)
                        bottomNavigation.setCurrentItem(3);
                    if (i == 0)
                        bottomNavigation.setCurrentItem(4);
                } else
                    bottomNavigation.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(new ViewPagerAdapter(getFragmentManager()));
        mViewPager.setCurrentItem(bottomNavigation.getDefaultItem());

        if (HelperCalander.isPersianUnicode) {
            ViewMaker.setLayoutDirection(mViewPager, View.LAYOUT_DIRECTION_RTL);
        }
    }

    public void goToUserProfile() {
        mViewPager.setCurrentItem(HelperCalander.isPersianUnicode ? 0 : 4);
    }

    public void setChatPage(FragmentChat fragmentChat) {
        if (getFragmentManager() != null) {
            if (mViewPager.getCurrentItem() != 2) {
                mViewPager.setCurrentItem(2);
            }
            Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
            // based on the current position you can then cast the page to the correct
            // class and call the method:
            if (page instanceof TabletMainFragment) {
                ((TabletMainFragment) page).loadChatFragment(fragmentChat);
            }
        }
    }

    public Fragment getViewPagerCurrentFragment() {
        if (getFragmentManager() != null) {
            return getFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
        } else {
            return null;
        }
    }
}
