package net.iGap.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnUnreadChange;
import net.iGap.libs.bottomNavigation.BottomNavigation;
import net.iGap.libs.bottomNavigation.Event.OnBottomNavigationBadge;
import net.iGap.realm.RealmRoom;

public class TestFragment extends Fragment implements OnUnreadChange {

    private ViewPager mViewPager;
    private BottomNavigation bottomNavigation;
    private View loadinView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        G.onUnreadChange = this;
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = view.findViewById(R.id.viewpager);
        bottomNavigation = view.findViewById(R.id.bn_main_bottomNavigation);
        loadinView = view.findViewById(R.id.loadingContent);

        Log.wtf(this.getClass().getName(), "initTabStrip");
        new Handler().postDelayed(() -> initTabStrip(),1000);
        Log.wtf(this.getClass().getName(), "initTabStrip");

        loadinView.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        Log.wtf(this.getClass().getName(), "onResume");
        super.onResume();
        Log.wtf(this.getClass().getName(), "onResume");
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

        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getFragmentManager()));
        mViewPager.setCurrentItem(bottomNavigation.getDefaultItem());
        /*mViewPager.setOffscreenPageLimit(1);*/
        bottomNavigation.setVisibility(View.VISIBLE);

        if (HelperCalander.isPersianUnicode) {
            ViewMaker.setLayoutDirection(mViewPager, View.LAYOUT_DIRECTION_RTL);
        }
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

        /*Log.i("aabolfazl", "onChange: " + RealmRoom.getAllUnreadCount());*/
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            int position = HelperCalander.isPersianUnicode ? i : 4 - i;
            switch (position) {
                case 0:
                    return new FragmentUserProfile();
                case 1:
                    return DiscoveryFragment.newInstance(0);
                case 2:
                    return FragmentMain.newInstance(FragmentMain.MainType.all);
                case 3:
                    return FragmentCall.newInstance(true);
                default:
                    return RegisteredContactsFragment.newInstance(false);

            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
