package net.iGap.story;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import net.iGap.R;
import net.iGap.fragments.FragmentGallery;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.observers.interfaces.OnGetPermission;

import java.io.IOException;

public class StoryPagerFragment extends Fragment implements CameraStoryFragment.OnGalleryIconClicked, StoryGalleryFragment.OnRVScrolled {
    ViewPager2 viewPager2;
    private static String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        viewPager2 = view.findViewById(R.id.pager);
        if (allPermissionsGranted()) {
            viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
            viewPager2.setOffscreenPageLimit(2);
            viewPager2.setAdapter(new CameraPagerAdapater(getActivity(), StoryPagerFragment.this::onGalleryIconClicked, StoryPagerFragment.this));
        } else {
            try {
                HelperPermission.getStoragePermision(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() {

                        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setAdapter(new CameraPagerAdapater(getActivity(), StoryPagerFragment.this::onGalleryIconClicked, StoryPagerFragment.this));


                    }

                    @Override
                    public void deny() {
                        showDeniedPermissionMessage();
                        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setAdapter(new CameraPagerAdapater(getActivity(), StoryPagerFragment.this::onGalleryIconClicked, StoryPagerFragment.this));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private boolean allPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(getContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), REQUIRED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void showDeniedPermissionMessage() {
        if (getContext() != null)
            HelperError.showSnackMessage(getContext().getString(R.string.you_need_to_allow) + " " + getContext().getString(R.string.permission_storage), false);
    }
}
