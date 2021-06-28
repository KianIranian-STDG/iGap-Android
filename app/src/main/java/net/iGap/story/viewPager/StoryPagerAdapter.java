package net.iGap.story.viewPager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class StoryPagerAdapter extends FragmentStatePagerAdapter {

    List<StoryUser> storyUsers;

    public StoryPagerAdapter(FragmentManager fm, List<StoryUser> storyUsers) {
        super(fm);
        this.storyUsers = storyUsers;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return StoryDisplayFragment.newInstance(position, storyUsers.get(position));
    }

    @Override
    public int getCount() {
        return storyUsers.size();
    }

    public Fragment findFragmentByPosition(ViewPager viewPager, int position) {
        return (Fragment) instantiateItem(viewPager, position);
    }
}
