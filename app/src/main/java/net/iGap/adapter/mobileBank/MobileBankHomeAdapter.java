package net.iGap.adapter.mobileBank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import net.iGap.fragments.mobileBank.MobileBankHomeTabFragment;

import java.util.List;

public class MobileBankHomeAdapter extends FragmentStatePagerAdapter {

    private List<MobileBankHomeTabFragment.HomeTabMode> modes;
    private List<String> titles;

    public MobileBankHomeAdapter(@NonNull FragmentManager fm, List<MobileBankHomeTabFragment.HomeTabMode> mode, List<String> title) {
        super(fm);
        this.modes = mode;
        this.titles = title;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MobileBankHomeTabFragment.newInstance(modes.get(position));
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
