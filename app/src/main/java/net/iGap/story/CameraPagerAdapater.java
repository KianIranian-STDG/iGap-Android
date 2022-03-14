package net.iGap.story;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CameraPagerAdapater extends FragmentStateAdapter {
    private CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked;
    private StoryGalleryFragment.OnRVScrolled onRVScrolled;
    private int listMode;
    private boolean isForRoom;
    private long roomId;
    private String roomTitle;

    public CameraPagerAdapater(@NonNull FragmentActivity fragmentActivity, boolean isForRoom, long roomId, int listMode, String roomTitle, CameraStoryFragment.OnGalleryIconClicked onGalleryIconClicked, StoryGalleryFragment.OnRVScrolled onRVScrolled) {
        super(fragmentActivity);
        this.onGalleryIconClicked = onGalleryIconClicked;
        this.onRVScrolled = onRVScrolled;
        this.isForRoom = isForRoom;
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.listMode = listMode;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CameraStoryFragment.newInstance(onGalleryIconClicked, this.isForRoom,this.roomId,this.listMode,this.roomTitle);
            case 1:
                return new StoryGalleryFragment(onRVScrolled, this.isForRoom,this.roomId,this.listMode,this.roomTitle);
            default:
                return CameraStoryFragment.newInstance(onGalleryIconClicked, this.isForRoom,this.roomId,this.listMode,this.roomTitle);
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
