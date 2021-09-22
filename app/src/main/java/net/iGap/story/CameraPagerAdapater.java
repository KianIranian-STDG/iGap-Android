package net.iGap.story;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.iGap.fragments.FragmentGallery;

public class CameraPagerAdapater extends FragmentStateAdapter {
    private CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked;
    private StoryGalleryFragment.OnRVScrolled onRVScrolled;

    public CameraPagerAdapater(@NonNull FragmentActivity fragmentActivity, CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked, StoryGalleryFragment.OnRVScrolled onRVScrolled) {
        super(fragmentActivity);
        this.onGalleryIconClicked = onGalleryIconClicked;
        this.onRVScrolled = onRVScrolled;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CameraStoryFragment.newInstance(onGalleryIconClicked);
            case 1:
                return new StoryGalleryFragment(onRVScrolled);
            default:
                return CameraStoryFragment.newInstance(onGalleryIconClicked);
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
