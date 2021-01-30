package net.iGap.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import net.iGap.model.GalleryItemModel;
import net.iGap.model.GalleryVideoModel;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.interfaces.GalleryItemListener;
import net.iGap.observers.interfaces.OnRotateImage;
import net.iGap.observers.interfaces.ToolbarListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FragmentGallery extends BaseFragment {

    private final String MODE_KEY = "MODE";
    private final String FOLDER_KEY = "FOLDER";
    private final String SUB_FOLDER_KEY = "SUB_FOLDER";
    private final String ID_KEY = "ID";
    private final String RETURN_DIRECTLY_KEY = "RETURN_DIRECT";
    private final String LISTENER_KEY = "LISTENER";

    private AdapterGalleryPhoto mGalleryPhotoAdapter;
    private AdapterGalleryVideo mGalleryVideoAdapter;
    private AdapterGalleryMusic mGalleryMusicAdapter;
    private String mFolderName, mFolderId;
    private boolean isSubFolder = false;
    private HelperToolbar mHelperToolbar;
    private GalleryFragmentListener mGalleryListener;
    private GalleryMode mGalleryMode;
    private boolean isReturnResultDirectly;
    private boolean isMusicSortedByDate = true;
    private static boolean canMultiSelected;

    public FragmentGallery() {
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, String folder, String id) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.FOLDER_KEY, folder);
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putString(fragment.ID_KEY, id);
        bundle.putBoolean(fragment.SUB_FOLDER_KEY, true);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, boolean isReturnResultDirectly, String folder, String id, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.FOLDER_KEY, folder);
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putString(fragment.ID_KEY, id);
        bundle.putSerializable(fragment.LISTENER_KEY, listener);
        bundle.putBoolean(fragment.RETURN_DIRECTLY_KEY, isReturnResultDirectly);
        bundle.putBoolean(fragment.SUB_FOLDER_KEY, true);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FragmentGallery newInstance(boolean canMultiSelect, GalleryMode mode, GalleryFragmentListener listener) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString(fragment.MODE_KEY, mode.name());
        bundle.putSerializable(fragment.LISTENER_KEY, listener);
        canMultiSelected = canMultiSelect;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mGalleryMode = GalleryMode.valueOf(getArguments().getString(MODE_KEY));
            mFolderName = getArguments().getString(FOLDER_KEY, null);
            isReturnResultDirectly = getArguments().getBoolean(RETURN_DIRECTLY_KEY, false);
            mGalleryListener = (GalleryFragmentListener) getArguments().getSerializable(LISTENER_KEY);
            mFolderId = getArguments().getString(ID_KEY, null);
            isSubFolder = getArguments().getBoolean(SUB_FOLDER_KEY, false);
        }
        initToolbar(view);
        initRecyclerView(view);
    }

    private void initToolbar(View view) {
        ViewGroup lytToolbar = view.findViewById(R.id.toolbar);
        String toolbarTitle = "";
        if (mGalleryMode == GalleryMode.PHOTO) {
            toolbarTitle = getString(R.string.gallery);
        } else if (mGalleryMode == GalleryMode.VIDEO) {
            toolbarTitle = getString(R.string.videos);
        } else {
            toolbarTitle = getString(R.string.audios);
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setIGapLogoCheck(false)
                .setDefaultTitle(isSubFolder ? mFolderName : toolbarTitle)
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
                        } else if (mGalleryMode == GalleryMode.MUSIC) {
                            if (mGalleryMusicAdapter.getMusicsItem().size() != 0) {
                                showSortDialog();
                            } else {
                                if (getContext() != null)
                                    Toast.makeText(getContext(), getString(R.string.no_item), Toast.LENGTH_SHORT).show();
                            }
                        } /* else {
                            openAndroidOsGallery();
                        }*/
                    }

                });

        if (!isReturnResultDirectly) {
            if (mGalleryMode == GalleryMode.MUSIC) {
                mHelperToolbar.setRightIcons(R.string.more_icon);
            } else if (isSubFolder) {
                mHelperToolbar.setRightIcons(R.string.edit_icon);
            }
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
        if (mHelperToolbar.getRightButton() != null) {
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
                if (getActivity() instanceof ActivityMain) {
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
        mGalleryPhotoAdapter.setMultiState(canMultiSelected );
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

        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);

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

        FileManager.getDeviceMusics(getContext(), isMusicSortedByDate, result -> {
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

        if (!isReturnResultDirectly) {

            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, 0);
            fragmentEditImage.setIsReOpenChatAttachment(false);
            ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
                @Override
                public void startProcess() {

                }

                @Override
                public void success(String newPath) {
                    G.handler.post(() -> {
                        FragmentEditImage.insertItemList(newPath, "", false);
                        if (getActivity() != null)
                            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    });
                }
            });

            fragmentEditImage.setGalleryListener(() -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack(FragmentGallery.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        } else {
            if (mGalleryListener != null) {
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
        Fragment fragment = FragmentGallery.newInstance(canMultiSelected, mode, false, path, id, new GalleryFragmentListener() {
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
        if (mHelperToolbar.getRightButton() != null) {
            if (mGalleryMode == GalleryMode.PHOTO || mGalleryMode == GalleryMode.VIDEO) {
                if (size > 0) {
                    mHelperToolbar.getRightButton().setText(R.string.md_send_button);
                } else {
                    mHelperToolbar.getRightButton().setText(R.string.close_icon);
                }
            }
        }
    }

    private void showSortDialog() {
        if (getContext() == null) return;

        List<String> items = new ArrayList<>();
        items.add(getString(R.string.date));
        items.add(getString(R.string.name));

        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            switch (position) {
                case 0:
                    if (!isMusicSortedByDate) {
                        isMusicSortedByDate = true;
                        mGalleryMusicAdapter.setMusicsItem(new ArrayList<>());
                        initRecyclerView(requireView());
                    }
                    break;

                case 1:
                    if (isMusicSortedByDate) {
                        isMusicSortedByDate = false;
                        mGalleryMusicAdapter.setMusicsItem(new ArrayList<>());
                        initRecyclerView(requireView());
                    }
                    break;
            }
        }).show();
    }

    public interface GalleryFragmentListener extends Serializable {
        void openOsGallery();

        default void onGalleryResult(String path) {
        }

        default void onVideoPickerResult(List<String> videos) {
        }

        default void onMusicPickerResult(String music) {
        }
    }

    public enum GalleryMode {
        PHOTO, VIDEO, MUSIC
    }

}
