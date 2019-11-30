package net.iGap.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityTrimVideo;
import net.iGap.adapter.AdapterGalleryMusic;
import net.iGap.adapter.AdapterGalleryPhoto;
import net.iGap.adapter.AdapterGalleryVideo;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.GalleryItemListener;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.GalleryItemModel;
import net.iGap.model.GalleryVideoModel;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentGallery extends BaseFragment {

    private AdapterGalleryPhoto mGalleryPhotoAdapter;
    private AdapterGalleryVideo mGalleryVideoAdapter;
    private AdapterGalleryMusic mGalleryMusicAdapter;
    private String mFolderName, mFolderId;
    private boolean isSubFolder = false;
    private HelperToolbar mHelperToolbar;
    private GalleryFragmentListener mGalleryListener;
    private GalleryMode mGalleryMode;
    private boolean isReturnResultDirectly;

    public FragmentGallery() {
    }

    public static FragmentGallery newInstance(GalleryMode mode, String folder, String id) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.mFolderName = folder;
        fragment.mFolderId = id;
        fragment.mGalleryMode = mode;
        fragment.isSubFolder = true;
        return fragment;
    }

    public static FragmentGallery newInstance(GalleryMode mode,boolean isReturnResultDirectly , String folder, String id, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.mFolderName = folder;
        fragment.mFolderId = id;
        fragment.mGalleryMode = mode;
        fragment.mGalleryListener = listener;
        fragment.isSubFolder = true;
        fragment.isReturnResultDirectly =isReturnResultDirectly;
        return fragment;
    }

    public static FragmentGallery newInstance(GalleryMode mode, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        fragment.mGalleryListener = listener;
        fragment.mGalleryMode = mode;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        initRecyclerView(view);
    }

    private void initToolbar(View view) {
        ViewGroup lytToolbar = view.findViewById(R.id.toolbar);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setIGapLogoCheck(false)
                .setDefaultTitle(isSubFolder ? mFolderName : getString(R.string.gallery))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        galleryOnBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (isSubFolder) {
                            if (mGalleryMode == GalleryMode.PHOTO) {
                                checkPhotoMultiSelectAndSendToEdit();
                            } else {
                                checkVideoMultiSelectAndSendToEdit();
                            }
                        } else {
                            openAndroidOsGallery();
                        }
                    }
                });

        if (!isReturnResultDirectly){
            mHelperToolbar.setRightIcons(isSubFolder ? R.string.edit_icon : R.string.more_icon);
        }

        lytToolbar.addView(mHelperToolbar.getView());
    }

    private void initRecyclerView(View view) {

        RecyclerView rvGallery = view.findViewById(R.id.rv_gallery);
        switch (mGalleryMode) {
            case PHOTO:
                rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? 3 : 2));
                setupGalleryWithPhotoAdapter(view, rvGallery);
                break;

            case VIDEO:
                rvGallery.setLayoutManager(new GridLayoutManager(rvGallery.getContext(), isSubFolder ? 3 : 2));
                setupGalleryWithVideoAdapter(view, rvGallery);
                break;

            case MUSIC:
                rvGallery.setLayoutManager(new LinearLayoutManager(rvGallery.getContext()));
                setupGalleryWithMusicAdapter(view, rvGallery);
                break;
        }
    }

    private void galleryOnBackPressed() {
        if (mGalleryMode == GalleryMode.PHOTO) {
            if (mGalleryPhotoAdapter != null && mGalleryPhotoAdapter.getMultiSelectState()) {
                mHelperToolbar.getRightButton().setText(R.string.edit_icon);
                mGalleryPhotoAdapter.setMultiSelectState(!mGalleryPhotoAdapter.getMultiSelectState());
                return;
            }
        } else if (mGalleryMode == GalleryMode.VIDEO) {
            if (mGalleryVideoAdapter != null && mGalleryVideoAdapter.getMultiSelectState()) {
                mHelperToolbar.getRightButton().setText(R.string.edit_icon);
                mGalleryVideoAdapter.setMultiSelectState(!mGalleryVideoAdapter.getMultiSelectState());
                return;
            }
        }

        popBackStackFragment();
    }

    private void openAndroidOsGallery() {
        if (mGalleryListener != null) {
            popBackStackFragment();
            mGalleryListener.openOsGallery();
        }
    }

    private void checkPhotoMultiSelectAndSendToEdit() {
        if (mGalleryPhotoAdapter == null) return;
        if (mGalleryPhotoAdapter.getMultiSelectState()) {
            mHelperToolbar.getRightButton().setText(R.string.edit_icon);
            if (mGalleryPhotoAdapter.getSelectedPhotos().size() > 0)
                sendSelectedPhotos(mGalleryPhotoAdapter.getSelectedPhotos());
        } else {
            mHelperToolbar.getRightButton().setText(R.string.close_icon);
        }
        mGalleryPhotoAdapter.setMultiSelectState(!mGalleryPhotoAdapter.getMultiSelectState());
    }

    private void checkVideoMultiSelectAndSendToEdit() {
        if (mGalleryVideoAdapter == null) return;
        if (mGalleryVideoAdapter.getMultiSelectState()) {
            mHelperToolbar.getRightButton().setText(R.string.edit_icon);
            if (mGalleryVideoAdapter.getSelectedVideos().size() > 0) {
                List<String> videos = new ArrayList<>();
                for (GalleryVideoModel video : mGalleryVideoAdapter.getSelectedVideos()) {
                    videos.add(video.getPath());
                }
                mGalleryListener.onVideoPickerResult(videos);
                popBackStackFragment();
                if (getActivity() instanceof ActivityMain){
                    ((ActivityMain) getActivity()).goneDetailFrameInTabletMode();
                }
            }
        } else {
            mHelperToolbar.getRightButton().setText(R.string.close_icon);
        }
        mGalleryVideoAdapter.setMultiSelectState(!mGalleryVideoAdapter.getMultiSelectState());
    }

    private void setupGalleryWithVideoAdapter(View view, RecyclerView rvGallery) {
        mGalleryVideoAdapter = new AdapterGalleryVideo(isSubFolder);
        rvGallery.setAdapter(mGalleryVideoAdapter);
        mGalleryVideoAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (isSubFolder) {
                    //open video
                    openVideoForEdit(path);
                } else {
                    //open sub directory
                    openGallerySubDirectory(GalleryMode.VIDEO, path, id);
                }
            }

            @Override
            public void onMultiSelect(int size) {
                handleUiWithMultiSelect(size);
            }
        });

        if (isSubFolder) {

            FileManager.getFolderVideosById(getContext(), mFolderId, result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> setVideoGalleryAdapter(result, view, rvGallery));
                }
            });

        } else {

            FileManager.getDeviceVideoFolders(getContext(), result -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> setVideoGalleryAdapter(result, view, rvGallery));
                }
            });

        }
    }

    private void setupGalleryWithPhotoAdapter(View view, RecyclerView rvGallery) {

        mGalleryPhotoAdapter = new AdapterGalleryPhoto(isSubFolder);
        rvGallery.setAdapter(mGalleryPhotoAdapter);
        mGalleryPhotoAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (isSubFolder) {
                    //open Image
                    openImageForEdit(path);
                } else {
                    //open sub directory
                    openGallerySubDirectory(GalleryMode.PHOTO, path, id);
                }
            }

            @Override
            public void onMultiSelect(int size) {
                handleUiWithMultiSelect(size);
            }
        });

        if (isSubFolder) {
            FileManager.getFolderPhotosById(getContext(), mFolderId, result -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    mGalleryPhotoAdapter.setPhotosItem(result);
                    setPhotoGalleryUI(view, rvGallery);
                });
            });
        } else {
            FileManager.getDevicePhotoFolders(getContext(), result -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    mGalleryPhotoAdapter.setAlbumsItem(result);
                    setPhotoGalleryUI(view, rvGallery);
                });
            });
        }

    }

    private void setupGalleryWithMusicAdapter(View view, RecyclerView rvGallery) {

        mGalleryMusicAdapter = new AdapterGalleryMusic();
        rvGallery.setAdapter(mGalleryMusicAdapter);
        mGalleryMusicAdapter.setListener(new GalleryItemListener() {
            @Override
            public void onItemClicked(String path, String id) {
                if (path == null) return;
                if (mGalleryListener != null) mGalleryListener.onMusicPickerResult(path);
                popBackStackFragment();
            }

            @Override
            public void onMultiSelect(int size) {
                //don't support yet

                //handleUiWithMultiSelect(size);
            }
        });

        FileManager.getDeviceMusics(getContext(), result ->  {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                mGalleryMusicAdapter.setMusicsItem(result);
                setMusicGalleryUI(view, rvGallery);
            });
        });

    }

    private void setVideoGalleryAdapter(List<GalleryVideoModel> result, View view, RecyclerView rvGallery) {
        mGalleryVideoAdapter.setVideosItem(result);

        if (isSubFolder && mGalleryVideoAdapter.getVideosItem().size() < 2) {//disable multi select when photo count was 1 or 0
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
        }

        if (!isSubFolder && (mGalleryVideoAdapter.getVideosItem().size() == 1 || mGalleryVideoAdapter.getVideosItem().size() == 0)) {//check 1 because we add all statically
            showNoItemInGallery(rvGallery, view);
        }

        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void setPhotoGalleryUI(View view, RecyclerView rvGallery) {
        if (!isReturnResultDirectly && isSubFolder && mGalleryPhotoAdapter.getPhotosItem().size() < 2) {//disable multi select when photo count was 1 or 0
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
        }

        if (!isSubFolder && (mGalleryPhotoAdapter.getAlbumsItem().size() == 1 || mGalleryPhotoAdapter.getAlbumsItem().size() == 0)) {//check 1 because we add all statically
            showNoItemInGallery(rvGallery, view);
        }

        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void setMusicGalleryUI(View view, RecyclerView rvGallery) {
        if (mGalleryMusicAdapter.getMusicsItem().size() == 0) {
            showNoItemInGallery(rvGallery, view);
        }
        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void openVideoForEdit(String path) {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
            Intent intent = new Intent(getActivity(), ActivityTrimVideo.class);
            intent.putExtra("PATH", path);
            getActivity().startActivityForResult(intent, AttachFile.request_code_trim_video);
            return;
        }
        List<String> videos = new ArrayList<>();
        videos.add(path);
        mGalleryListener.onVideoPickerResult(videos);
        popBackStackFragment();
    }

    private void openImageForEdit(String path) {
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        if (!isReturnResultDirectly){

            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, 0);
            fragmentEditImage.setIsReOpenChatAttachment(false);

            //rotate and send image for edit
            ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
                @Override
                public void startProcess() {
                    //nothing
                }

                @Override
                public void success(String newPath) {
                    FragmentEditImage.insertItemList(newPath, "", false);
                    G.handler.post(() -> {
                        if (getActivity() == null) return;
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    });
                }
            });

            fragmentEditImage.setGalleryListener(() -> {
                popBackStackFragment();
                popBackStackFragment();
            });
        }else {
            if (mGalleryListener != null){
                mGalleryListener.onGalleryResult(path);
            }
        }
    }

    private void sendSelectedPhotos(List<GalleryItemModel> selectedPhotos) {
        if (getActivity() == null || selectedPhotos.size() == 0) return;

        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();
        for (GalleryItemModel photo : selectedPhotos) {
            FragmentEditImage.insertItemList(photo.getAddress(), "", false);
        }
        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, selectedPhotos.size() - 1);
        fragmentEditImage.setIsReOpenChatAttachment(false);
        fragmentEditImage.setGalleryListener(() -> {
            popBackStackFragment();
            popBackStackFragment();
        });
        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
    }

    private void showNoItemInGallery(View rv, View view) {
        rv.setVisibility(View.GONE);
        view.findViewById(R.id.tv_no_item).setVisibility(View.VISIBLE);
    }

    private void openGallerySubDirectory(GalleryMode mode, String path, String id) {
        if (id == null || getActivity() == null) return;
        Fragment fragment = FragmentGallery.newInstance(mode, false, path, id, new GalleryFragmentListener() {
            @Override
            public void openOsGallery() {

            }

            @Override
            public void onVideoPickerResult(List<String> videos) {
                if (mGalleryListener != null) mGalleryListener.onVideoPickerResult(videos);
                popBackStackFragment();
            }
        });
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(false);

    }

    private void handleUiWithMultiSelect(int size) {
        if (size > 0) {
            mHelperToolbar.getRightButton().setText(R.string.md_send_button);
        } else {
            mHelperToolbar.getRightButton().setText(R.string.close_icon);
        }
    }

    @Override
    public void onDestroy() {
        if (mGalleryVideoAdapter != null) mGalleryVideoAdapter.clearThumbnailCache();
        if (mGalleryMusicAdapter != null) mGalleryMusicAdapter.clearThumbnailCache();

        super.onDestroy();
    }

    public interface GalleryFragmentListener {
        void openOsGallery();

        default void onGalleryResult(String path){}

        default void onVideoPickerResult(List<String> videos) {
        }

        default void onMusicPickerResult(String music) {
        }
    }

    public enum GalleryMode {
        PHOTO, VIDEO, MUSIC
    }

}
