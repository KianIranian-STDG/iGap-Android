package net.iGap.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.discovery.DiscoveryFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return FragmentMain.newInstance(FragmentMain.MainType.all);
            case 1:
                return DiscoveryFragment.newInstance(0);
            case 2:
                return FragmentCall.newInstance(true);
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 5;
    }
}