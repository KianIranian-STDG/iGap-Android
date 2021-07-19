package net.iGap.story.viewPager;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class StoryPagerAdapter extends FragmentStatePagerAdapter {

    private List<StoryUser> storyUsers;
    private StoryDisplayFragment.PageViewOperator pageViewOperator;
    private boolean isMyStory = false;

    public StoryPagerAdapter(@NonNull FragmentManager fm, int behavior, List<StoryUser> storyUsers, StoryDisplayFragment.PageViewOperator pageViewOperator, boolean isMyStory) {
        super(fm, behavior);
        this.storyUsers = storyUsers;
        this.pageViewOperator = pageViewOperator;
        this.isMyStory = isMyStory;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        StoryDisplayFragment storyDisplayFragment = StoryDisplayFragment.newInstance(position, storyUsers.get(position),isMyStory);
        storyDisplayFragment.setPageViewOperator(pageViewOperator);
        return storyDisplayFragment;
    }

    @Override
    public int getCount() {
        return storyUsers.size();
    }

    public Fragment findFragmentByPosition(ViewPager viewPager, int position) {
        return (Fragment) instantiateItem(viewPager, position);
    }
}
