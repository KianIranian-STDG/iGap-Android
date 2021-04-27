package net.iGap;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.iGap.story.camera.CameraStoryFragment;
import net.iGap.fragments.FragmentGallery;

public class CameraPagerAdapater extends FragmentStateAdapter {
    private CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked;

    public CameraPagerAdapater(@NonNull FragmentActivity fragmentActivity, CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked) {
        super(fragmentActivity);
        this.onGalleryIconClicked = onGalleryIconClicked;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CameraStoryFragment.newInstance(onGalleryIconClicked);
            case 1:
                Fragment fragment = FragmentGallery.newInstance(true, FragmentGallery.GalleryMode.STORY, () -> {
                    Log.e("dlkfdlfd", "createFragment: ");
                });
                return fragment;
            default:
                return new CameraStoryFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
