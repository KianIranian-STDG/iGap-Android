package net.iGap.story;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import net.iGap.R;
import net.iGap.fragments.FragmentGallery;

public class StoryPagerFragment extends Fragment implements CameraStoryFragment.OnGalleryIconClicked, StoryGalleryFragment.OnRVScrolled {
    ViewPager2 viewPager2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        viewPager2 = view.findViewById(R.id.pager);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setAdapter(new CameraPagerAdapater(getActivity(), this::onGalleryIconClicked, this));
        return view;
    }

    @Override
    public void onGalleryIconClicked() {
        viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
    }

    @Override
    public void scrolled(boolean isScrolled) {
        viewPager2.setUserInputEnabled(isScrolled);
    }

    @Override
    public void changeItem() {
        viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
    }
}
