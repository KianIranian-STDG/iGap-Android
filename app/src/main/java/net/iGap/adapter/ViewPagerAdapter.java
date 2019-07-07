package net.iGap.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.iGap.G;
import net.iGap.fragments.BeepTunesFragment;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentMain;
import net.iGap.fragments.FragmentUserProfile;
import net.iGap.fragments.RegisteredContactsFragment;
import net.iGap.fragments.TabletMainFragment;
import net.iGap.helper.HelperCalander;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        int position = HelperCalander.isPersianUnicode ? i : 4 - i;
        switch (position) {
            case 0:
                return new FragmentUserProfile();
            case 1:
                return new BeepTunesFragment();
            case 2:
                return (G.twoPaneMode) ? new TabletMainFragment() : FragmentMain.newInstance(FragmentMain.MainType.all);
            case 3:
                return FragmentCall.newInstance(true);
            default:
                return RegisteredContactsFragment.newInstance(false, false, RegisteredContactsFragment.CONTACTS);

        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}