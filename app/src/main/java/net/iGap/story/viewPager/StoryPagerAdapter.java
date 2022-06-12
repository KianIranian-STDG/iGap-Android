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
    private boolean isForReply;

    public StoryPagerAdapter(@NonNull FragmentManager fm, int behavior, List<StoryUser> storyUsers, StoryDisplayFragment.PageViewOperator pageViewOperator, boolean isMyStory, boolean isForReply) {
        super(fm, behavior);
        this.storyUsers = storyUsers;
        this.pageViewOperator = pageViewOperator;
        this.isMyStory = isMyStory;
        this.isForReply = isForReply;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        StoryDisplayFragment storyDisplayFragment = new StoryDisplayFragment(position, position < storyUsers.size() ? storyUsers.get(position) : storyUsers.get(0), isMyStory, isForReply);
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
