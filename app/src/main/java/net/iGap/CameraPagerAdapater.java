package net.iGap;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import net.iGap.camera.CameraStoryFragment;
import net.iGap.fragments.FragmentGallery;

import java.io.IOException;

public class CameraPagerAdapater extends FragmentStateAdapter {

    public CameraPagerAdapater(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CameraStoryFragment();
            case 1:
                Fragment fragment = FragmentGallery.newInstance(true, FragmentGallery.GalleryMode.PHOTO, () -> {
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
